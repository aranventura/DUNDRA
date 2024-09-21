package syntactic;

import java.util.ArrayList;

public class SyntaxException extends Exception {
    private ArrayList<Exception> exceptions;

    public SyntaxException(ArrayList<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public void print() {
        for (Exception i: exceptions) i.printStackTrace();
    }

    public String[] getErrorMessages() {
        String[] errors = new String[exceptions.size()];

        int i=0;
        for (Exception e: exceptions) errors[i++] = e.getMessage();

        return errors;
    }
}
