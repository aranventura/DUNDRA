package tac;

import java.util.LinkedList;

public class BasicBlock {

    private LinkedList<Quadruple> quadruples;
    private String label;
    private boolean isInFunc;

    public BasicBlock() {
        quadruples = new LinkedList<>();
    }

    public BasicBlock(String label) {
        quadruples = new LinkedList<>();
        this.label = label;
        this.isInFunc = false;
    }

    public LinkedList<Quadruple> getQuadruples() {
        return quadruples;
    }

    public void setQuadruples(LinkedList<Quadruple> quadruples) {
        this.quadruples = quadruples;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void addQuad(Quadruple q){
        quadruples.add(q);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < quadruples.size(); i++) {
            if (i==0 && label != null) {
                sb.append("\t" + label + ":\t" + quadruples.get(i).toString() + "\n");
                continue;
            }

            sb.append("\t\t" + quadruples.get(i).toString() + "\n");
        }

        return sb.toString();
    }

    public boolean isEmpty() {
        return quadruples.size() == 0;
    }
}
