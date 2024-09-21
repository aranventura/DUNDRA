package syntactic.symbolTableTree;

import syntactic.Symbol;
import syntactic.SymbolTable;

public class SymbolTableTree {
    private SymbolTableNode root;
    private SymbolTableNode current;

    public SymbolTableTree() {
        root = new SymbolTableNode(new SymbolTable());
        current = root;
    }

    public void newScope(){
        SymbolTableNode node = new SymbolTableNode(new SymbolTable());
        node.setParent(current);
        current.addChild(node);
        current = node;
    }

    public void add(SymbolTableNode symbolTableNode) {
        symbolTableNode.setParent(current);
        current.addChild(symbolTableNode);
        current = symbolTableNode;
    }

    public boolean isInScope(String tag) {
        SymbolTableNode scope = current;
        do {
            if (scope.getSymbolTable().exists(tag)) return true;
        } while (current != root && (scope = scope.getParent()) != null);

        return false;
    }

    public void back() {
        current = current.getParent();
    }

    public SymbolTableNode getRoot() {
        return root;
    }

    public SymbolTableNode getCurrent() {
        return current;
    }

    public SymbolTable getCurrentSymbolTable(){ return current.getSymbolTable(); }

    public Symbol getSymbol(String tag) {
        SymbolTableNode scope = current;
        do {
            if (scope.getSymbolTable().exists(tag)) return scope.getSymbolTable().get(tag);
        } while (current != root && !(scope = scope.getParent()).equals(root));

        return null;
    }
}
