import mips.MIPS;
import syntactic.SyntaxAnalyser;
import syntactic.SyntaxException;
import tac.TAC;

public class Main {

    public static void main(String[] args) {
        String code_file = "code.txt";

        SyntaxAnalyser syntaxAnalyser = new SyntaxAnalyser(code_file);
        TAC tac = new TAC();
        MIPS mips = new MIPS();

        try {
            tac.handler(syntaxAnalyser.compile());

            mips.translateToMIPS(tac.getBasicBlockLinkedList(), syntaxAnalyser.getSymbolTableTree().getRoot());
        } catch (SyntaxException e) {
            e.print();
        }
    }
}
