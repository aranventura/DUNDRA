package syntactic;

import lexical.LexicalAnalyzer;
import lexical.Token;
import mips.MIPS;
import semantic.SemanticAnalyzer;
import syntactic.parseTree.*;
import syntactic.symbolTableTree.SymbolTableTree;
import tac.TAC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SyntaxAnalyser {

    private final String dictionary_file = "dictionary.txt";
    private String code_file;
    private LexicalAnalyzer lexicalAnalyzer;
    private SemanticAnalyzer semanticAnalyzer;
    private Tree tree;
    private SymbolTableTree symbolTableTree;
    private ArrayList<Exception> exceptions;
    private Token currentToken;
    private boolean error;

    private HashMap<String, TreeTokenFUNCTION> fTokens;

    private Node currentOPNode;

    public SyntaxAnalyser(String code_file) {
        this.code_file = code_file;
        this.currentToken = null;
        tree = new Tree();
        symbolTableTree = new SymbolTableTree();
        exceptions = new ArrayList<>();
        fTokens = new HashMap<>();
    }

    public Tree compile() throws SyntaxException {
        this.lexicalAnalyzer = new LexicalAnalyzer(code_file, symbolTableTree);
        this.lexicalAnalyzer.generateDictionary(this.dictionary_file);
        this.semanticAnalyzer = new SemanticAnalyzer(exceptions, symbolTableTree);

        checkGrammar();

        if (exceptions.size() == 0) {
            System.out.println("\n" + tree.getRoot().toString());
            return tree;
        }

        displayErrors();

        return tree;
    }

    private void checkGrammar() {
        // We check if the file is empty
        if (lexicalAnalyzer.getFileManager().checkFileIsEmpty()) {
            exceptions.add(new Exception("The file is empty or has no tokens"));
            return;
        }

        currentToken = lexicalAnalyzer.getNextToken();
        if (currentToken.getType() == Token.Type.EOF) {
            exceptions.add(new Exception("The source file can't start with empty spaces"));
            return;
        }

        declareFunctions();
    }

    private void declareFunctions() {
        if (currentToken.isVolveria()) {
            DECL_FUNC();

            currentToken = lexicalAnalyzer.getNextToken();
            declareFunctions();
        } else MAIN();
    }

    private void DECL_FUNC() {
        P();
        Token.Type type = Token.Type.VOID;
        if (currentToken.isBarbaro() || currentToken.isMago() || currentToken.isPicaro() || currentToken.isPaladin()) {
            type = currentToken.getType();

            P();
        }

        if (!currentToken.isViajeA()) {
            exceptions.add(new Exception("'viaje a' expected but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            return;
        }

        if (!(currentToken = lexicalAnalyzer.getNextToken()).isID()) {
            exceptions.add(new Exception("Function name expected but found reserved syntax '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            return;
        }

        String tag = currentToken.getValue();

        if (symbolTableTree.isInScope(tag)) {
            exceptions.add(new Exception("Symbol '" + tag + "' already exists"));
            //TODO: recuperar del error
            return;
        }

        symbolTableTree.getCurrentSymbolTable().add(currentToken.getValue(), new Symbol(currentToken.getValue(), Symbol.Type.FUNCTION));
        TreeTokenFUNCTION fToken = new TreeTokenFUNCTION(type, tag, null);
        tree.add(new Node(fToken));

        P();

        symbolTableTree.newScope();
        if (currentToken.isIntegrantes()) {
            if (!(currentToken = lexicalAnalyzer.getNextToken()).isColon()) {
                exceptions.add(new Exception("Expected ':' after 'integrantes'"));
                //TODO: recuperar del error
                return;
            }

            P();
            PARAMS();

            fToken.setParams(new LinkedHashMap<>(symbolTableTree.getCurrentSymbolTable().getTable()));
            fTokens.put(tag, fToken);

            if (!currentToken.isEnd()) P();
        } else if (!currentToken.isNingunIntegrante()) {
            exceptions.add(new Exception("Expected function end parameters syntax but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            return;
        }

        END();

        EXPR();

        if (!currentToken.isElViajeTermino()) {
            exceptions.add(new Exception("Expected 'El viaje termino' but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            return;
        }

        currentToken = lexicalAnalyzer.getNextToken();
        if (currentToken.isConValor()) {
            if (!(currentToken = lexicalAnalyzer.getNextToken()).isColon()) {
                exceptions.add(new Exception("Expected ':' after 'valor'"));
                //TODO: recuperar del error
                return;
            }

            currentToken = lexicalAnalyzer.getNextToken();
            Symbol symbol = VALOR();
            if (symbol == null) {
                exceptions.add(new Exception("Expected return value found '" + currentToken.getValue() + "'"));
                //TODO: recuperar del error
                return;
            } else {
                tree.add(new Node(new TreeTokenRETURN(symbol)));
                tree.back();
            }
        } else if (!currentToken.isSinNingunValor()) {
            exceptions.add(new Exception("Expected return but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            return;
        }

        currentToken = lexicalAnalyzer.getNextToken();
        END();

        currentToken = lexicalAnalyzer.getNextToken();
        END_BLOCK();

        semanticAnalyzer.validateFunctionDeclaration(tree.getCurrentNode());

        tree.back();
        symbolTableTree.back();
    }

    private void PARAMS() {
        if (currentToken.isBarbaro() || currentToken.isMago() || currentToken.isPicaro() || currentToken.isPaladin()) {
            DECL();
            PARAMS();
        } else if (!currentToken.isNingunIntegrante()) {
            exceptions.add(new Exception("Expected more parameters or function end parameters syntax but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
        }
    }

    private Node FUNCTION_CALL() {
        if (!(currentToken = lexicalAnalyzer.getNextToken()).isID()) {
            exceptions.add(new Exception("Function name expected but found reserved syntax '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            return null;
        }

        String tag = currentToken.getValue();

        if (!symbolTableTree.isInScope(tag)) {
            exceptions.add(new Exception("Symbol '" + tag + "' does not exist"));
            //TODO: recuperar del error
            return null;
        }

        P();

        ArrayList<Symbol> params = new ArrayList<>();
        if (currentToken.isIntegrantes()) {
            if (!(currentToken = lexicalAnalyzer.getNextToken()).isColon()) {
                exceptions.add(new Exception("Expected ':' after 'integrantes'"));
                //TODO: recuperar del error
                return null;
            }

            P();
            PARAMS_CALL(params);
        } else if (!currentToken.isNingunIntegrante()) {
            exceptions.add(new Exception("Expected function end parameters syntax but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            return null;
        }

        semanticAnalyzer.validateFunctionCall(fTokens.get(tag), params);

        Token.Type returnType = fTokens.get(tag).getReturnType();

        return new Node(new TreeTokenCALL(returnType, tag, params));
    }

    private void PARAMS_CALL(ArrayList<Symbol> params) {
        Symbol symbol = VALOR();
        if (symbol != null) {

            if (!symbol.isLiteral() && !symbolTableTree.isInScope(symbol.getTag())) {
                exceptions.add(new Exception("Variable '" + symbol.getTag() + "' is not declared"));
                return;
            }

            params.add(symbol);

            P();
            PARAMS_CALL(params);

        } else if (!currentToken.isNingunIntegrante()) {
            exceptions.add(new Exception("Expected more parameters or function end parameters syntax but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
        }
    }

    private void MAIN() {
        START_MAIN();
        EXPR();
        END_MAIN();
    }

    private void START_MAIN(){
        if (!currentToken.isStartMain()) {
            exceptions.add(new Exception(("Main not found")));
            error=true;
            return;
        }

        tree.add(new Node(new TreeToken(TreeToken.Type.MAIN)));
        symbolTableTree.newScope();

        P();
        END();
    }
    private void END_MAIN(){
        if (!currentToken.isEndMain()) {
            exceptions.add(new Exception("End main not found"));
            error=true;
            return;
        }

        currentToken = lexicalAnalyzer.getNextToken();
        END();
    }

    private void END() {
        while(error){
            if (!currentToken.isEnd() && !currentToken.isEOF()){
                currentToken = lexicalAnalyzer.getNextToken();
            } else error=false;
        }

        if (!currentToken.isEnd() && !currentToken.isEOF()) {
            exceptions.add(new Exception("End token expected but not found"));
            error=true;
            END();
        }
    }

    private void EXPR() {
        P();
        EXPR_P();
    }

    private void EXPR_P() {
        if (currentToken.isBarbaro() || currentToken.isMago() || currentToken.isPicaro() || currentToken.isPaladin()) {
            DECL();
            if (!currentToken.isEnd()) P();
            END();
            EXPR();
        } else if (currentToken.isStartAritmetica()) {
            P();
            if (currentToken.isID()) {
                IGUALACION();
                if (!currentToken.isEnd()) P();
                END();
                EXPR();
            } else {
                exceptions.add(new Exception("Expected ID but found '" + currentToken.getValue() + "'"));
                //TODO: Error, gestionar recuperacion
            }
        } else if (currentToken.isID()) {
            IGUALACION();
            if (!currentToken.isEnd()) P();
            END();
            EXPR();
        } else if (currentToken.isIf()) {
            TreeTokenIF ifToken = new TreeTokenIF();
            tree.add(new Node(ifToken));
            CONDICIONES(ifToken);
            symbolTableTree.newScope();
            EXPR();
            END_IF();
            tree.back();
            symbolTableTree.back();
            EXPR();
        } else if (currentToken.isSwitch()) {
            SWITCH();
            EXPR();
        } else if(currentToken.isLoop()){
            FOR();
            EXPR();
        } else if (currentToken.isViajeA()) {
            Node fNode = FUNCTION_CALL();
            if (fNode != null) {
                tree.add(fNode);
                tree.back();
            }

            if (!currentToken.isEnd()) P();

            END();
            EXPR();
        }
    }

    private void FOR(){
        P();
        Symbol symbol = VALOR();
        if(symbol != null){
            P();

            if(currentToken.isRounds()) {
                semanticAnalyzer.validateFor(symbol);
                tree.add(new Node(new TreeTokenFOR(symbol)));

                symbolTableTree.newScope();

                P();
                END();
                EXPR();

                symbolTableTree.back();

                END_BLOCK();

                tree.back();
            } else {
                exceptions.add(new Exception("Rondas expected but not found"));
            }
        } else {
            exceptions.add(new Exception("Value expected but not found"));
        }
    }

    private void SWITCH() {
        P();
        Symbol symbol = VALOR();
        if (symbol != null) {
            tree.add(new Node(new TreeTokenSWITCH(symbol)));
            CASES();
            semanticAnalyzer.validateSwitch(tree.getCurrentNode());
            tree.back();
        } else {
            exceptions.add(new Exception("Expected value but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
        }
    }

    private void CASES() {
        P();

        Symbol symbol = VALOR();
        if (symbol != null) {
            Token caseToken = currentToken;

            currentToken = lexicalAnalyzer.getNextToken();
            if (currentToken.isColon()) {
                tree.add(new Node(new TreeTokenCASE(symbol)));
                symbolTableTree.newScope();

                EXPR();

                tree.back();
                symbolTableTree.back();

                if (currentToken.isSwitchBreak()) CASES();
                else {
                    exceptions.add(new Exception("'Final de camino' expected but found '" + currentToken.getValue() + "'"));
                    //TODO: recuperar del error
                }
            } else {
                exceptions.add(new Exception("Expected ':' after case '" + caseToken.getValue() + "' but found '" + currentToken.getValue() + "'"));
                //TODO: recuperar del error
            }
        } else END_BLOCK();
    }

    private void END_BLOCK() {
        if (!currentToken.isEndBlock()) {
            exceptions.add(new Exception("End of block expected but not found"));
            //TODO: recuperar del error
        }
    }

    private void CONDICIONES(TreeTokenIF ifToken) {
        P();

        if(currentToken.isID() || currentToken.isNum()) {
            Symbol a = VALOR();

            OP_LOGICA(ifToken, a);
            CONDICIONES_P(ifToken);

        } else {
            exceptions.add(new Exception("Expected value or variable but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
        }
    }

    private void OP_LOGICA(TreeTokenIF ifToken, Symbol a) {
        P();

        if(currentToken.isMayor() || currentToken.isMenor() || currentToken.isIgual() || currentToken.isDif() || currentToken.isMayorOIgual() || currentToken.isMenorOIgual()) {
            Token.Type cType = currentToken.getType();

            currentToken = lexicalAnalyzer.getNextToken();
            if(currentToken.isID() | currentToken.isNum()) {
                Symbol b = VALOR();

                ifToken.getConditions().add(new Condition(a, b, cType));
                semanticAnalyzer.validateLogicalOperation(new Condition(a, b, cType));
            } else {
                exceptions.add(new Exception("Expected value or variable but found '" + currentToken.getValue() + "'"));
                //TODO: recuperar del error
            }

        } else {
            exceptions.add(new Exception("Expected conditions but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
        }
    }

    private void CONDICIONES_P(TreeTokenIF ifToken) {
        currentToken = lexicalAnalyzer.getNextToken();

        if(currentToken.isOr() | currentToken.isAnd()) {
            ifToken.getOperands().add(currentToken.getType());
            CONDICIONES(ifToken);
        } else if (!currentToken.isThen()) {
            exceptions.add(new Exception("Expected 'entonces' but found '" + currentToken.getValue() + "'"));
        }
    }

    private void END_IF() {
        if (currentToken.isEndIf()) {
            P();
            END();
        } else if (currentToken.isElse()) {
            tree.add(new Node(new TreeToken(TreeToken.Type.ELSE)));
            symbolTableTree.newScope();

            EXPR();

            if (currentToken.isEndIf()) {
                symbolTableTree.back();
                P();
                END();
                tree.back();
            } else {
                exceptions.add(new Exception("Expected end else but found '" + currentToken.getValue() + "'"));
                //TODO: recuperar del error
            }
        } else {
            exceptions.add(new Exception("Expected end if but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
        }
    }

    private void IGUALACION() {
        Token destination = currentToken;

        if ((currentToken = lexicalAnalyzer.getNextToken()).isIgualacion()) {

            tree.add(new Node(new TreeTokenASSIGN(destination.getValue())));

            OP();

            tree.back();
        } else {
            exceptions.add(new Exception("Expected 'vio' but found '" + currentToken.getValue() + "'"));
            //TODO: Error, gestionar recuperacion
        }
    }

    private void OP() {
        currentOPNode = null;

        OP2();
        OP_P();

        if (currentOPNode != null) {
            while (currentOPNode.getParent() != null) currentOPNode = currentOPNode.getParent();

            tree.add(currentOPNode);
            tree.back();

            semanticAnalyzer.validateAssign(tree.getCurrentNode());
        }
    }

    private void OP2() {
        OP3();
        OP2_P();
    }

    private void OP3() {
        P();

        if (currentToken.isViajeA()) {
            Node OPNode = FUNCTION_CALL();

            if (currentOPNode == null) {
                currentOPNode = OPNode;
            } else {
                currentOPNode.addChild(OPNode);
                if (OPNode.getParent().getChilds().size() > 1) semanticAnalyzer.validateArithmeticOperation(OPNode);
                currentOPNode = OPNode;
            }
        } else {
            Symbol symbol = VALOR();
            if (symbol != null) {
                Node OPNode = new Node(new TreeTokenID(symbol));

                if (currentOPNode == null) {
                    currentOPNode = OPNode;
                } else {
                    currentOPNode.addChild(OPNode);
                    if (OPNode.getParent().getChilds().size() > 1) semanticAnalyzer.validateArithmeticOperation(OPNode);
                    currentOPNode = OPNode;
                }
            } else {
                exceptions.add(new Exception("Expected value but found '" + currentToken.getValue() + "'"));
                //TODO: recuperar del error
            }
        }

        P();
    }

    private void OP_P() {
        if (currentToken.isSuma() || currentToken.isResta()) {

            if (currentOPNode.getParent() != null) {
                while (currentOPNode.getParent() != null) {
                    if (currentOPNode.getToken() instanceof TreeTokenOP) {
                        if (((TreeTokenOP) currentOPNode.getToken()).isSuma()) break;
                        if (((TreeTokenOP) currentOPNode.getToken()).isResta()) break;
                    }

                    currentOPNode = currentOPNode.getParent();
                }
            }

            Node OPNode = new Node(new TreeTokenOP(currentToken.isSuma() ? TreeTokenOP.OPType.SUMA : TreeTokenOP.OPType.RESTA));

            OPNode.addChild(currentOPNode);
            currentOPNode = OPNode;

            OP2();
            OP_P();
        }
    }

    private void OP2_P() {
        if (currentToken.isMult() || currentToken.isDiv()) {

            Node OPNode = new Node(new TreeTokenOP(currentToken.isMult() ? TreeTokenOP.OPType.MULT : TreeTokenOP.OPType.DIV));

            Node nodeToSwap = null;
            if (currentOPNode.getParent() != null) {
                if (currentOPNode.getParent().getToken() instanceof TreeTokenOP) {
                    if (((TreeTokenOP) currentOPNode.getParent().getToken()).isSuma()) nodeToSwap = currentOPNode;
                    else if (((TreeTokenOP) currentOPNode.getParent().getToken()).isResta()) nodeToSwap = currentOPNode;
                    else nodeToSwap = currentOPNode.getParent();
                }
            }

            if (nodeToSwap != null) {
                if (nodeToSwap.getParent() != null) {
                    nodeToSwap.getParent().removeLastChild();
                    nodeToSwap.getParent().addChild(OPNode);
                }

                OPNode.addChild(nodeToSwap);
                currentOPNode = OPNode;
            } else {
                OPNode.addChild(currentOPNode);
                currentOPNode = OPNode;
            }

            OP3();
            OP2_P();
        }
    }

    private void DECL() {
        Token type = currentToken;

        currentToken = lexicalAnalyzer.getNextToken();
        if (currentToken.isAssign()) {

            currentToken = lexicalAnalyzer.getNextToken();
            if (currentToken.isID()) {
                String tag = currentToken.getValue();

                if (symbolTableTree.isInScope(tag)) {
                    exceptions.add(new Exception("Variable '" + tag + "' already declared"));
                    return;
                }

                tree.add(new Node(new TreeTokenDECL(tag, type.getType())));

                Symbol.Type symbolType = Symbol.Type.INT;

                if (type.isBarbaro()) symbolType = Symbol.Type.INT;
                else if (type.isMago()) symbolType = Symbol.Type.FLOAT;
                else if (type.isPicaro()) symbolType = Symbol.Type.CHAR;
                else if (type.isPaladin()) symbolType = Symbol.Type.BOOL;

                symbolTableTree.getCurrentSymbolTable().add(tag, new Symbol(tag, symbolType));

                OPT_ASSIGN(tag);

                tree.back();
            } else {
                exceptions.add(new Exception("Expected variable name but found reserved syntax '" + currentToken.getValue() + "'"));
                //TODO: recuperar del error
                error=true;
            }
        } else {
            exceptions.add(new Exception("Expected syntax 'llamado' but found '" + currentToken.getValue() + "'"));
            //TODO: recuperar del error
            error=true;
        }
    }

    private void OPT_ASSIGN(String tag) {
        P();

        if (currentToken.isTieneValor()) {

            currentToken = lexicalAnalyzer.getNextToken();
            Symbol symbol = VALOR();
            if (symbol != null) {
                if (semanticAnalyzer.checkTypeMatches(tag, symbol)) {
                    symbolTableTree.getCurrentSymbolTable().update(tag, symbol);
                    tree.add(new Node(new TreeTokenID(symbol)));
                    tree.back();
                }
            } else {
                exceptions.add(new Exception("Expected value but found '" + currentToken.getValue() + "'"));
                error=true;
                //TODO: recuperar del error
            }
        }
    }

    private Symbol VALOR() {
        Symbol symbol = null;
        if(currentToken.isID()) {
            symbol = symbolTableTree.getSymbol(currentToken.getValue());
        } else if (currentToken.isInt() || currentToken.isFloat() || currentToken.isBool() || currentToken.isChar()) { //TODO: Faltan chars
            Symbol.Type symbolType = Symbol.Type.INT;

            if (currentToken.isInt()) symbolType = Symbol.Type.INT;
            else if (currentToken.isFloat()) symbolType = Symbol.Type.FLOAT;
            else if (currentToken.isChar()) symbolType = Symbol.Type.CHAR;
            else if (currentToken.isBool()) symbolType = Symbol.Type.BOOL;

            symbol = new Symbol(symbolType, currentToken.getValue());
        }

        return symbol;
    }

    private void P() {
        currentToken = lexicalAnalyzer.getNextToken();
        if (currentToken.isOther() || currentToken.isComma() || currentToken.isAnd() || currentToken.isOr()) P();
    }

    private void displayErrors() throws SyntaxException {
        throw new SyntaxException(exceptions);
    }

    public SymbolTableTree getSymbolTableTree() {
        return symbolTableTree;
    }
}