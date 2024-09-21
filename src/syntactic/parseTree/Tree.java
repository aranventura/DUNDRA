package syntactic.parseTree;

public class Tree {
    private Node root;
    private Node current;

    public Tree() {
        root = new Node(new TreeToken(TreeToken.Type.ROOT));
        current = root;
    }

    public void add(Node node) {
        node.setParent(current);
        current.addChild(node);
        current = node;
    }

    public void back() {
        current = current.getParent();
    }

    public Node getRoot() {
        return root;
    }

    public Node getCurrentNode() {
        return current;
    }
}
