package syntactic.symbolTableTree;

import syntactic.SymbolTable;

import java.util.ArrayList;

public class SymbolTableNode {
    private SymbolTableNode parent;
    private ArrayList<SymbolTableNode> childs;
    private SymbolTable symbolTable;

    public SymbolTableNode(SymbolTable symbolTable) {
        childs = new ArrayList<>();
        this.symbolTable = symbolTable;
    }

    public SymbolTableNode getParent() {
        return parent;
    }

    public void setParent(SymbolTableNode parent) {
        this.parent = parent;
    }

    public void addChild(SymbolTableNode symbolTableNode) {
        childs.add(symbolTableNode);
    }

    public ArrayList<SymbolTableNode> getChilds() {
        return childs;
    }

    public void setChilds(ArrayList<SymbolTableNode> childs) {
        this.childs = childs;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}
