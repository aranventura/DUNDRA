package semantic;

import lexical.Token;
import syntactic.Symbol;
import syntactic.parseTree.*;
import syntactic.symbolTableTree.SymbolTableTree;
import syntactic.Condition;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class SemanticAnalyzer {

    private ArrayList<Exception> exceptions;
    private SymbolTableTree symbolTableTree;
    private Symbol.Type result;
    private boolean activeOperation;

    public SemanticAnalyzer(ArrayList<Exception> exceptions, SymbolTableTree symbolTableTree) {
        this.exceptions = exceptions;
        this.symbolTableTree = symbolTableTree;
        this.activeOperation = false;
    }

    public boolean checkTypeMatches(String tag, Symbol value) {

        Symbol symbol = symbolTableTree.getSymbol(tag);
        Symbol.Type type = symbol.getType();

        if (!value.isLiteral())
            if (value.getType() == type) return true;

        switch (type) {
            case INT:
                try {
                    Integer.parseInt(value.getInfo());
                } catch (Exception e) {
                    exceptions.add(new Exception("Tried to assign wrong type of value for " + tag + ", of type " + type));
                    return false;
                }
                return true;

            case FLOAT:
                try {
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setDecimalSeparator('.');
                    DecimalFormat format = new DecimalFormat("0.#");
                    format.setDecimalFormatSymbols(symbols);
                    format.parse(value.getValue().toString()).floatValue();
                } catch (Exception e) {
                    exceptions.add(new Exception("Tried to assign wrong type of value for " + tag + ", of type " + type));
                    return false;
                }
                return true;

            case CHAR:
                try {
                    String x = value.getInfo();
                    x.charAt(1);
                    // Si hi supera aquesta sentència vol dir que no hem rebut un sol caracter, i per tant afegim l'excepcio
                    exceptions.add(new Exception("Tried to assign wrong type of value for " + tag + ", of type " + type));
                    return false;
                } catch (Exception ignored) { return true; }

            case BOOL:
                if (!value.getInfo().equals("cierto") && !value.getInfo().equals("falso")) {
                    exceptions.add(new Exception("Tried to assign wrong type of value for " + tag + ", of type " + type));
                    return false;
                }
                return true;
        }

        return false;
    }

    public void validateFunctionDeclaration(Node fNode) {
        String function = ((TreeTokenFUNCTION) fNode.getToken()).getTag();
        Token.Type returnType = ((TreeTokenFUNCTION) fNode.getToken()).getReturnType();

        if (fNode.getChilds().size() > 0 && fNode.getLastChild().getToken() instanceof TreeTokenRETURN) {
            if (returnType == Token.Type.VOID) {
                Symbol returnSymbol = ((TreeTokenRETURN) fNode.getLastChild().getToken()).getSymbol();
                exceptions.add(new Exception("Mistaken return type '" + returnSymbol.getType() + "' for '" + returnType + "' function '" + function + "'"));
                return;
            }
        } else {
            if (returnType != Token.Type.VOID) {
                exceptions.add(new Exception("Missing return type for '" + returnType + "' function '" + function + "'"));
                return;
            } else return;
        }

        Symbol returnSymbol = ((TreeTokenRETURN) fNode.getLastChild().getToken()).getSymbol();

        switch (returnType) {
            case BARBARO:
                if (returnSymbol.isInt()) return;
                break;

            case MAGO:
                if (returnSymbol.isFloat()) return;
                break;

            case PICARO:
                if (returnSymbol.isChar()) return;
                break;

            case PALADIN:
                if (returnSymbol.isBool()) return;
                break;
        }

        exceptions.add(new Exception("Mistaken return type '" + returnSymbol.getType() + "' for '" + returnType + "' function '" + function + "'"));

        return;
    }

    public void validateFunctionCall(TreeTokenFUNCTION fToken, ArrayList<Symbol> params) {
        if (fToken.getParams().size() > params.size()) {
            exceptions.add(new Exception("Too few arguments on function call '" + fToken.getTag() + "'"));
            return;
        }

        if (fToken.getParams().size() < params.size()) {
            exceptions.add(new Exception("Too many arguments on function call '" + fToken.getTag() + "'"));
            return;
        }

        int count = 0;
        for (Symbol i: fToken.getParams().values()) {
            Symbol j = params.get(count++);
            if (i.getType() != j.getType()) {
                exceptions.add(new Exception("Expected type '" + i.getType() + "' but found type '" + j.getType() + "' for argument number " + count + " on '" + fToken.getTag() + "' function call"));
            }
        }
    }

    public void validateAssign(Node node) {
        Symbol assigned = symbolTableTree.getSymbol(((TreeTokenASSIGN) node.getToken()).getTag());
        if (node.getChilds().size() > 0) {
            TreeToken firstToken = node.getChilds().get(0).getToken();
            if (firstToken instanceof TreeTokenOP) {
                if (!assigned.isInt() && !assigned.isFloat()) {
                    exceptions.add(new Exception("Can't assign arithmetic operation to variable '" + assigned.getTag() + "' of type '" + assigned.getType() + "'"));
                }
            } else if (firstToken instanceof TreeTokenID) {
                Symbol fTokenSymbol = ((TreeTokenID) firstToken).getSymbol();
                if (assigned.getType() != fTokenSymbol.getType()) {
                    exceptions.add(new Exception("Can't assign type '" + fTokenSymbol.getType() + "' to variable '" + assigned.getTag() + "' of type '" + assigned.getType() + "'"));
                }
            } else if (firstToken instanceof TreeTokenCALL) {
                Token.Type fType = ((TreeTokenCALL) firstToken).getReturnType();
                if (fType == Token.Type.BARBARO && assigned.isInt()) return;
                if (fType == Token.Type.MAGO && assigned.isFloat()) return;
                if (fType == Token.Type.PICARO && assigned.isChar()) return;
                if (fType == Token.Type.PALADIN && assigned.isBool()) return;
                exceptions.add(new Exception("Can't assign function type '" + fType + "' to variable '" + assigned.getTag() + "' of type '" + assigned.getType() + "'"));
            }
        }
    }

    public void validateArithmeticOperation(Node operand) {
        if (operand.getToken() instanceof TreeTokenID) {
            Symbol symbol = ((TreeTokenID) operand.getToken()).getSymbol();
            if (!symbol.isInt() && !symbol.isFloat()) {
                exceptions.add(new Exception("Can't perform arithmetic operation with type '" + symbol.getType() + "' for symbol '" + symbol.getTag() + "'"));
            }
        } else if (operand.getToken() instanceof TreeTokenCALL) {
            Token.Type returnType = ((TreeTokenCALL) operand.getToken()).getReturnType();
            if (returnType != Token.Type.BARBARO && returnType != Token.Type.MAGO) {
                exceptions.add(new Exception("Can't perform arithmetic operation with function of type '" + returnType + "' for function '" + ((TreeTokenCALL) operand.getToken()).getTag() + "'"));
            }
        }
    }

    public void validateLogicalOperation(Condition condition) {
        if (!condition.getA().isInt() && !condition.getA().isFloat()) {
            exceptions.add(new Exception("Can't perform relational operation with type '" + condition.getA().getType() + "' for symbol '" + condition.getA().getTag() + "'"));
        } else if (!condition.getB().isInt() && !condition.getB().isFloat()) {
            exceptions.add(new Exception("Can't perform relational operation with type '" + condition.getB().getType() + "' for symbol '" + condition.getB().getTag() + "'"));
        }

    }

    public void validateFor(Symbol symbol) {
        if (!symbol.isInt()) exceptions.add(new Exception("Found type '" + symbol.getType() + "' for loop number of rounds. Number of rounds must be of type 'BARBARO'"));
    }

    public void validateSwitch(Node node) {
        Symbol symbol = ((TreeTokenSWITCH) node.getToken()).getValue();

        for (Node i: node.getChilds()) {
            Symbol caseSymbol = ((TreeTokenCASE) i.getToken()).getValue();

            if (symbol.getType() != caseSymbol.getType()) {
                exceptions.add(new Exception("Case '" + caseSymbol.getInfo() + "' does not match switch type '" + symbol.getType() + "'"));
            }
        }
    }
}