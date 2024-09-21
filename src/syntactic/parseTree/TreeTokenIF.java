package syntactic.parseTree;

import lexical.Token;
import syntactic.Condition;

import java.util.ArrayList;

public class TreeTokenIF extends TreeToken {
    private ArrayList<Condition> conditions;
    private ArrayList<Token.Type> operands;

    public TreeTokenIF() {
        super(Type.IF);
        conditions = new ArrayList<>();
        operands = new ArrayList<>();
    }

    public ArrayList<Condition> getConditions() {
        return conditions;
    }

    public ArrayList<Token.Type> getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<conditions.size(); i++) {
            sb.append(conditions.get(i).toString());

            if (i < operands.size()) sb.append(" " + operands.get(i) + " ");
            else break;
        }

        return super.toString() + sb;
    }
}
