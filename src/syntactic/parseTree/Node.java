package syntactic.parseTree;

import java.util.ArrayList;
import java.util.Iterator;

public class Node {
    private Node parent;
    private ArrayList<Node> childs;
    private TreeToken token;

    public Node(TreeToken token) {
        childs = new ArrayList<>();
        this.token = token;
    }

    public void setToken(TreeToken token) {
        this.token = token;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void removeLastChild() {
        childs.remove(childs.size() - 1);
    }

    public ArrayList<Node> getChilds() {
        return childs;
    }

    public TreeToken getToken() {
        return token;
    }

    public void addChild(Node node) {
        node.setParent(this);
        childs.add(node);
    }

    public Node getLastChild() {
        return (childs.size() > 0 ? childs.get(childs.size() - 1) : null);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(token.toString());
        buffer.append('\n');
        for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
            Node next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
