package tac;

public class Quadruple {

    private String arg1;
    private String arg2;
    private String operation;
    private String result;
    private String scope;

    public Quadruple(String arg1, String arg2, String operation, String result) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operation = operation;
        this.result = result;
    }

    public Quadruple(String arg1, String arg2, String operation, String result, String scope) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operation = operation;
        this.result = result;

        this.scope = scope;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        String a1 = (arg1 == null ? "" : arg1);
        String a2 = (arg2 == null ? "" : arg2);
        String op = (arg2 == null || operation == null ? "" : operation);
        String arrow = (arg1 == null && arg2 == null && operation == null ? "" : " <- ");

        return result + arrow + a1 + " " + op + " " + a2 + "\t(" + scope + ")";
    }
}
