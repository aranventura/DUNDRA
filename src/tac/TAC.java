package tac;

import lexical.Token;
import mips.RegisterManager;
import syntactic.Condition;
import syntactic.Symbol;
import syntactic.parseTree.*;
import syntactic.parseTree.TreeToken;

import java.util.LinkedList;
import java.util.Stack;

public class TAC {

    private LinkedList<BasicBlock> basicBlockLinkedList;
    private BasicBlock bb;
    private int tCount; //temporal variables
    private int lCount; //labels
    private int fCount;

    private Stack<String> labelStack;


    public TAC(){
        basicBlockLinkedList = new LinkedList<>();
        tCount = 0;
        fCount = 0;

        labelStack = new Stack<>();
    }

    public void handler(Tree tree){
        evaluate(tree.getRoot());

        optimize();

        for (BasicBlock i: basicBlockLinkedList) {
            System.out.println("Basic Block:");
            System.out.println(i.toString());
        }
    }

    private void evaluate(Node current){
        Quadruple q;
        for (Node node: current.getChilds()) {
            TreeToken token = node.getToken();
            switch(token.getTokenType()) {
                case FUNCTION:
                    bb = new BasicBlock(((TreeTokenFUNCTION) token).getTag());
                    basicBlockLinkedList.add(bb);
                    evaluate(node);
                    break;

                case MAIN:
                    bb = new BasicBlock("main");
                    basicBlockLinkedList.add(bb);
                    evaluate(node);
                    break;

                case DECL:
                    String tag = ((TreeTokenDECL)token).getTag();
                    if(node.getParent().getToken() instanceof TreeTokenFUNCTION){
                        TreeTokenFUNCTION tokenFUNCTION = ((TreeTokenFUNCTION) node.getParent().getToken());
                        if(tokenFUNCTION.getParams().get(tag) != null){
                            q = new Quadruple(tag, null, null, "a" + fCount);
                            q.setScope("VAR_PARAM_FUNC");
                            bb.addQuad(q);
                            fCount++;
                        } else {
                            if (node.getChilds().size() > 0) {
                                String value = ((TreeTokenID) node.getChilds().get(0).getToken()).getSymbol().getInfo();
                                q = new Quadruple(value, null, "=", tag);

                            } else {
                                q = new Quadruple("0", null, "=", tag);
                            }
                            q.setScope("DECL");
                            bb.addQuad(q);
                        }
                    } else {
                        if (node.getChilds().size() > 0) {
                            String value = ((TreeTokenID) node.getChilds().get(0).getToken()).getSymbol().getInfo();
                            String type = ((TreeTokenID) node.getChilds().get(0).getToken()).getSymbol().getType().name();
                            if (type.equals("CHAR")) value = String.valueOf((int) value.charAt(0));
                            else if (type.equals("BOOL")) value = value.equals("falso") ? "0" : "1";
                            else if (type.equals("FLOAT")) value = value.split("'")[0]; // TODO: We currently don't support floats in MIPS so we truncate the decimal value

                            q = new Quadruple(value, null, "=", tag);

                        } else {
                            q = new Quadruple("0", null, "=", tag);
                        }
                        q.setScope("DECL");
                        bb.addQuad(q);
                    }

                    break;

                case ASSIGN:
                    Node firstChild = node.getChilds().get(0);
                    tag = ((TreeTokenASSIGN) token).getTag();

                    if (firstChild.getToken() instanceof TreeTokenOP) {
                        String temporal = "t" + tCount;

                        evaluateOP(node.getChilds().get(0));

                        q = new Quadruple(temporal, null, "=", tag);
                        q.setScope("ASSIGN");

                        bb.addQuad(q);
                    } else if (firstChild.getToken() instanceof TreeTokenID){
                        String value = ((TreeTokenID) node.getChilds().get(0).getToken()).getSymbol().getInfo();

                        q = new Quadruple(value, null, "=", tag);
                        q.setScope("ASSIGN");

                        bb.addQuad(q);
                    } else if (firstChild.getToken() instanceof TreeTokenCALL) {
                        TreeTokenCALL callToken = ((TreeTokenCALL) firstChild.getToken());

                        if (!callToken.getParams().isEmpty()){
                            int i = 0;
                            for (Symbol s : callToken.getParams()) {
                                // En el result va el num del registre $a
                                if(s.getTag() != null){
                                    q = new Quadruple(s.getTag(), null, null, String.valueOf(i++));
                                } else {
                                    q = new Quadruple(s.getValue().toString(), null, null, String.valueOf(i++));
                                }
                                q.setScope("PARAM_FUNC");
                                bb.addQuad(q);
                            }
                        }
                        q = new Quadruple(null, null, null, callToken.getTag());
                        q.setScope("GOTO_FUNC");
                        bb.addQuad(q);

                        // Es una assignacio per tant ha de returnar un valor segur
                        TreeTokenASSIGN tAssign = ((TreeTokenASSIGN) node.getToken());
                        q = new Quadruple("$v0", null, "return", tAssign.getTag());
                        q.setScope("RETURNED_VAL");
                        bb.addQuad(q);

                        if (!callToken.getParams().isEmpty()){
                            int i = 0;
                            for (Symbol s : callToken.getParams()) {
                                // En el result va el num del registre $a
                                if(s.getTag() != null){
                                    q = new Quadruple(s.getTag(), null, null, String.valueOf(i++));
                                    q.setScope("SAVE_PARAMS");
                                    bb.addQuad(q);
                                }
                                i++;
                            }
                        }
                    }
                    break;

                case IF:
                    TreeTokenIF ifToken = ((TreeTokenIF) token);

                    if (ifToken.getConditions().size() <= 1) {
                        Condition c = ifToken.getConditions().get(0);

                        bb.addQuad(new Quadruple(c.getA().getInfo(), c.getB().getInfo(), Condition.conditionSymbol(c.negate()), "goto L" + lCount, "IF"));
                    } else {
                        String[] temporals = new String[ifToken.getConditions().size()];
                        for (int i = 0; i < ifToken.getConditions().size(); i++) {
                            Condition c = ifToken.getConditions().get(i);

                            temporals[i] = "t" + tCount;
                            bb.addQuad(new Quadruple(c.getA().getInfo(), c.getB().getInfo(), Condition.conditionSymbol(c.negate()), "t" + (tCount++), "COND"));
                        }

                        for (int i = 0; i < ifToken.getOperands().size(); i++) {
                            Token.Type nOperand = ifToken.getOperands().get(i) == Token.Type.AND ? Token.Type.OR : Token.Type.AND;
                            String nOp = nOperand == Token.Type.AND ? "&" : "|";

                            if (i == 0) {
                                bb.addQuad(new Quadruple(temporals[i], temporals[i+1], nOp, "t" + (tCount++), "COND"));
                            } else {
                                bb.addQuad(new Quadruple("t" + (tCount - 2), temporals[i+1], nOp, "t" + (tCount++), "COND"));
                            }
                        }

                        bb.addQuad(new Quadruple("t" + (tCount - 1), null, null, "goto L" + lCount, "IF"));
                    }

                    bb = new BasicBlock();
                    basicBlockLinkedList.add(bb);

                    labelStack.add("L" + (lCount++));

                    evaluate(node);

                    bb = new BasicBlock(labelStack.pop());
                    basicBlockLinkedList.add(bb);
                    break;

                case ELSE:
                    bb.addQuad(new Quadruple(null, null, null, "goto L" + lCount, "GOTO"));

                    bb = new BasicBlock(labelStack.pop());
                    labelStack.add("L" + (lCount++));

                    basicBlockLinkedList.add(bb);

                    evaluate(node);
                    break;

                case FOR:
                    TreeTokenFOR forToken = (TreeTokenFOR) token;

                    String rounds = "t" + (tCount++);
                    if(RegisterManager.isNumber(forToken.getSymbol().getInfo())){
                        bb.addQuad(new Quadruple(forToken.getSymbol().getInfo(), null, null, rounds, "DECL"));
                    } else {
                        bb.addQuad(new Quadruple(forToken.getSymbol().getInfo(), null, null, rounds, "LOAD_FOR"));
                    }

                    String forLabel = "L" + (lCount++);
                    bb = new BasicBlock(forLabel);
                    basicBlockLinkedList.add(bb);

                    String nextLabel = "L" + (lCount++);
                    bb.addQuad(new Quadruple(rounds, "0", Condition.conditionSymbol(Token.Type.MENOR_O_IGUAL), "goto " + nextLabel, "IF"));

                    evaluate(node);

                    bb.addQuad(new Quadruple(rounds, "1", "-", rounds, "OP"));
                    bb.addQuad(new Quadruple(null, null, null, "goto " + forLabel, "GOTO"));

                    bb = new BasicBlock(nextLabel);
                    basicBlockLinkedList.add(bb);
                    break;

                case RETURN:
                    String info = ((TreeTokenRETURN) token).getSymbol().getInfo();
                    bb.addQuad(new Quadruple(info, null, null, "return", "RETURN"));
                    break;
            }
        }
    }

    private void evaluateOP(Node node) {
        String tag = "t" + tCount;
        Quadruple q;
        String[] info = new String[2];
        int count = 0;
        for (Node i: node.getChilds()) {
            if (i.getToken() instanceof TreeTokenOP) {
                info[count++] = "t" + (++tCount);
                evaluateOP(i);
            } else if (i.getToken() instanceof TreeTokenID){
                String value = ((TreeTokenID) i.getToken()).getSymbol().getInfo();
                info[count++] = value;
            } else if (i.getToken() instanceof TreeTokenCALL) {
                //TODO: TAC para llamar funciones
            }
        }

        String operation = ((TreeTokenOP) node.getToken()).getTypeString();

        q = new Quadruple(info[0], info[1], operation, tag);
        q.setScope("OP");
        bb.addQuad(q);
    }


    public LinkedList<BasicBlock> getBasicBlockLinkedList() {
        return basicBlockLinkedList;
    }

    public void optimize() {
        LinkedList<BasicBlock> list = new LinkedList<>();

        for (int i=0; i<basicBlockLinkedList.size(); i++) {
            BasicBlock current = basicBlockLinkedList.get(i);

            if (current.isEmpty()) {
                String label = current.getLabel();

                if (i < basicBlockLinkedList.size() - 1) {
                    BasicBlock next = basicBlockLinkedList.get(i + 1);

                    for (BasicBlock j: basicBlockLinkedList) {
                        for (Quadruple q : j.getQuadruples()) {
                            q.setResult(q.getResult().replace(label, next.getLabel()));
                        }
                    }
                }
            } else list.add(current);
        }

        basicBlockLinkedList = list;
    }
}
