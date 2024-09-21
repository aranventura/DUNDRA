package lexical;

public class Token {
    public enum Type { START_MAIN, END_MAIN, IGNORE, PRINT,
                ASSIGN, BARBARO, MAGO, PICARO, PALADIN,
                END, OTHER, ID, INT, FLOAT, CHAR, BOOL,
                TIENE_VALOR, IGUALACION, SUMA, RESTA, MULT, DIV,
                AND, OR, MENOR, MAYOR, MENOR_O_IGUAL, MAYOR_O_IGUAL, IGUAL, DIF,
                IF, THEN, ELSE, ENDIF, LOOP, ROUNDS, EOF,
                END_BLOCK, SWITCH, SWITCH_BREAK, START_ARITMETICA,
                NEGATIVE, COLON, VOLVERIA, VIAJE_A, INTEGRANTES, NINGUN_INTEGRANTE,
                COMMA, EL_VIAJE_TERMINO, CON_VALOR, SIN_NINGUN_VALOR, VOID }

    private Type type;
    private String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() { return type; }

    public String getValue() { return value; }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isStartMain() {
        return type == Type.START_MAIN;
    }

    public boolean isEndMain() {
        return type == Type.END_MAIN;
    }

    public boolean isIgnore() {
        return type == Type.IGNORE;
    }

    public boolean isAssign() {
        return type == Type.ASSIGN;
    }

    public boolean isBarbaro() {
        return type == Type.BARBARO;
    }

    public boolean isMago() {
        return type == Type.MAGO;
    }

    public boolean isPicaro() {
        return type == Type.PICARO;
    }

    public boolean isPaladin() {
        return type == Type.PALADIN;
    }

    public boolean isEnd() { return type == Type.END; }

    public boolean isOther() {
        return type == Type.OTHER;
    }

    public boolean isID() {
        return type == Type.ID;
    }

    public boolean isInt() { return type == Type.INT; }

    public boolean isFloat() { return type == Type.FLOAT; }

    public boolean isChar() { return type == Type.CHAR; }

    public boolean isBool() {
        return type == Type.BOOL;
    }

    public boolean isNum() { return type == Type.FLOAT || type == Type.INT; }

    public boolean isTieneValor() {
        return type == Type.TIENE_VALOR;
    }

    public boolean isEOF() {
        return type == Type.EOF;
    }

    public boolean isIgualacion() {
        return this.type == Type.IGUALACION;
    }

    public boolean isSuma() {
        return type == Type.SUMA;
    }

    public boolean isResta() {
        return type == Type.RESTA;
    }

    public boolean isMult() {
        return type == Type.MULT;
    }

    public boolean isDiv() {
        return type == Type.DIV;
    }

    public boolean isAnd() {
        return type == Type.AND;
    }

    public boolean isOr() {
        return type == Type.OR;
    }

    public boolean isMenor() {
        return type == Type.MENOR;
    }

    public boolean isMayor() {
        return type == Type.MAYOR;
    }

    public boolean isMayorOIgual() {
        return type == Type.MAYOR_O_IGUAL;
    }

    public boolean isMenorOIgual() {
        return type == Type.MENOR_O_IGUAL;
    }

    public boolean isIgual() {
        return type == Type.IGUAL;
    }

    public boolean isDif() {
        return type == Type.DIF;
    }

    public boolean isIf() {
        return type == Type.IF;
    }

    public boolean isThen() {
        return type == Type.THEN;
    }

    public boolean isElse() {
        return type == Type.ELSE;
    }

    public boolean isEndIf() {
        return type == Type.ENDIF;
    }

    public boolean isPrint() {
        return type == Type.PRINT;
    }

    public boolean isLoop() {
        return type == Type.LOOP;
    }

    public boolean isRounds() {
        return type == Type.ROUNDS;
    }

    public boolean isEndBlock() {
        return type == Type.END_BLOCK;
    }

    public boolean isSwitch() {
        return type == Type.SWITCH;
    }

    public boolean isSwitchBreak() {
        return type == Type.SWITCH_BREAK;
    }

    public boolean isStartAritmetica() {
        return type == Type.START_ARITMETICA;
    }

    public boolean isNegative() {
        return type == Type.NEGATIVE;
    }

    public boolean isColon() {
        return type == Type.COLON;
    }

    public boolean isVolveria() {
        return type == Type.VOLVERIA;
    }

    public boolean isViajeA() {
        return type == Type.VIAJE_A;
    }

    public boolean isIntegrantes() {
        return type == Type.INTEGRANTES;
    }

    public boolean isNingunIntegrante() {
        return type == Type.NINGUN_INTEGRANTE;
    }

    public boolean isComma() {
        return type == Type.COMMA;
    }

    public boolean isElViajeTermino() {
        return type == Type.EL_VIAJE_TERMINO;
    }

    public boolean isConValor() {
        return type == Type.CON_VALOR;
    }

    public boolean isSinNingunValor() {
        return type == Type.SIN_NINGUN_VALOR;
    }

    public boolean isVoid() {
        return type == Type.VOID;
    }


    public String toString() {
        return "(" + type + ", " + value + ")";
    }

}