package syntactic;

public class Symbol {
    public enum Type { FUNCTION, BOOL, INT, FLOAT, CHAR }

    private String tag;
    private Type type;
    private Object value;

    public Symbol(String tag, Type type) {
        this.tag = tag;
        this.type = type;
    }

    public Symbol(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public boolean isLiteral() {
        return tag == null;
    }

    public boolean isFunction() {
        return type == Type.FUNCTION;
    }

    public boolean isBool() {
        return type == Type.BOOL;
    }

    public boolean isInt() {
        return type == Type.INT;
    }

    public boolean isFloat() {
        return type == Type.FLOAT;
    }

    public boolean isChar() {
        return type == Type.CHAR;
    }

    public String getTag() {
        return tag;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getInfo() {
        return isLiteral() ? value.toString() : tag;
    }

    @Override
    public String toString() {
        return type + (value == null ? "" : " -> " + value);
    }
}
