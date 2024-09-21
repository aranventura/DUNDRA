package mips;

import syntactic.symbolTableTree.SymbolTableNode;
import tac.BasicBlock;
import tac.Quadruple;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

public class MIPS {

    private static final String DATA = ".data\n";
    private static final String TEXT = ".text\n";

    private SymbolTableNode currentNode;

    public MIPS() {
        currentNode = null;
    }

    public void translateToMIPS(LinkedList<BasicBlock> basicBlockLinkedList, SymbolTableNode symbolTablenode){
        currentNode = symbolTablenode.getChilds().get(0);
        try{
            Writer assembler = new FileWriter("main.asm", false);
            assembler.write(DATA);
            assembler.write(TEXT);

            // Write main first
            boolean firstExec = true;
            for (BasicBlock bb: basicBlockLinkedList) {
                // Saltamos al main si hay funciones
                if (firstExec && !bb.getLabel().equals("main")){
                    assembler.write("j main \n");
                }
                generateCode(assembler, bb);
                firstExec = false;
            }
            assembler.write("li $v0, 10          # system call for exit\n" +
                    "      syscall\n");
            assembler.close();
        }catch(IOException e){
            // Add error to error list
            e.printStackTrace();
        }
    }

    public void generateCode(Writer assembler, BasicBlock bb) throws IOException {
        if(bb.getLabel() != null) {
            try {
                assembler.write(bb.getLabel() + ": \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Quadruple quad : bb.getQuadruples()){
            switch (quad.getScope()){
                case "DECL":
                    declaration(assembler, quad);
                    break;
                case "ASSIGN":
                    assignation(assembler, quad);
                    break;
                case "OP":
                    operation(assembler, quad);
                    break;
                case "COND":
                    conditional(assembler, quad);
                    break;
                case "IF":
                    ifCheck(assembler, quad);
                    break;
                case "GOTO":
                    gotoCheck(assembler, quad);
                    break;
                case "PARAM_FUNC":
                    paramFunc(assembler, quad);
                    break;
                case "GOTO_FUNC":
                    gotoFunc(assembler, quad);
                    break;
                case "VAR_PARAM_FUNC":
                    RegisterManager.setParamFunction(quad.getArg1(), quad.getResult(), assembler);
                    break;
                case "RETURN":
                    returnFunc(assembler, quad);
                    break;
                case "RETURNED_VAL":
                    assignReturnedVal(assembler, quad);
                    break;
                case "SAVE_PARAMS":
                    saveNewInfoParams(assembler, quad);
                    break;
                case "LOAD_FOR":
                    loadVarFor(assembler, quad);
                    break;
            }
        }
    }

    private void loadVarFor(Writer assembler, Quadruple quad) throws IOException{
        String reg = RegisterManager.getRegisterByArg(quad.getResult());
        if(RegisterManager.doesMemoryExist(quad.getArg1())){
            int addr = RegisterManager.getMemoryAllocation(quad.getArg1());
            assembler.write("lw $" + reg + ", " + addr + "($sp)\n");
        }
    }

    private void saveNewInfoParams(Writer assembler, Quadruple quad) throws IOException{
        if(RegisterManager.doesMemoryExist(quad.getArg1())){
            int addr = RegisterManager.getMemoryAllocation(quad.getArg1());
            assembler.write("sw $a" + quad.getResult() + ", " + addr + "($sp)\n");
        }
    }

    private void assignReturnedVal(Writer assembler, Quadruple quad) throws IOException{
        String reg = checkRegister(assembler, quad.getResult(), true);
        assembler.write("move $" + reg + ", " + quad.getArg1() + "\n");
        RegisterManager.setMemoryAllocation(quad.getResult(), reg, assembler);
    }

    private void returnFunc(Writer assembler, Quadruple quad) throws IOException{
        String reg = checkRegister(assembler, quad.getArg1(), false);
        assembler.write("move $v0, $" + reg + "\n");
        assembler.write("jr $ra \n");
        RegisterManager.eraseParamsFunction();
        // TODO: funcion para dejar temporales vacios
        // Borrar vars de la funcion
    }

    private void paramFunc(Writer assembler, Quadruple quad) throws IOException{
        if(RegisterManager.doesMemoryExist(quad.getArg1())){
            int memAddr = RegisterManager.getMemoryAllocation(quad.getArg1());
            assembler.write("lw $a" + quad.getResult() + ", " + memAddr + "($sp)\n");
        } else {
            assembler.write("li $a" + quad.getResult() + ", " + quad.getArg1() + "\n");
        }
    }

    private void gotoFunc(Writer assembler, Quadruple quad) throws IOException{
        assembler.write("jal " + quad.getResult() + "\n");
    }

    private void gotoCheck(Writer assembler, Quadruple quad) throws IOException{
        String[] result = quad.getResult().split(" ");
        assembler.write("j " + result[1] + "\n");
    }

    private void ifCheck(Writer assembler, Quadruple quad) throws IOException{
        String registerArg1 = checkRegister(assembler, quad.getArg1(), false);

        String registerArg2;

        String[] result = quad.getResult().split(" ");

        if(quad.getArg2() == null){
            registerArg2 = "zero";
            assembler.write("beq $" + registerArg1 + ", $" + registerArg2 + ", " + result[1] + "\n");
            RegisterManager.freeRegister(quad.getArg1());
        } else {
            registerArg2 = checkRegister(assembler, quad.getArg2(), false);
            switch (quad.getOperation()) {
                case "<":
                    assembler.write("blt $" + registerArg1 + ", $" + registerArg2 + ", " + result[1] + "\n");
                    break;
                case ">":
                    assembler.write("bgt $" + registerArg1 + ", $" + registerArg2 + ", " + result[1] + "\n");
                    break;
                case "<=":
                    assembler.write("ble $" + registerArg1 + ", $" + registerArg2 + ", " + result[1] + "\n");
                    break;
                case ">=":
                    assembler.write("bge $" + registerArg1 + ", $" + registerArg2 + ", " + result[1] + "\n");
                    break;
                case "=":
                    assembler.write("beq $" + registerArg1 + ", $" + registerArg2 + ", " + result[1] + "\n");
                    break;
            }
        }
    }

    private String checkRegister(Writer assembler, String argument, boolean isAssign) throws IOException{
        String register;
        if(RegisterManager.getParamRegister(argument) != null){
            register = RegisterManager.getParamRegister(argument);
        } else {
            register = RegisterManager.getRegisterByArg(argument);
            if(RegisterManager.doesMemoryExist(argument) && !isAssign){
                int memAddr = RegisterManager.getMemoryAllocation(argument);
                assembler.write("lw $" + register + ", " + memAddr + "($sp)\n");
            } else if (RegisterManager.isNumber(argument)) {
                assembler.write("li $" + register + ", " + argument + "\n");
                RegisterManager.freeRegister(register);
            }
        }
        return register;
    }

    private void conditional(Writer assembler, Quadruple quad) throws IOException{

        String registerArg1 = checkRegister(assembler, quad.getArg1(), false);

        String registerArg2 = checkRegister(assembler, quad.getArg2(), false);

        String resultReg = RegisterManager.getRegisterByArg(quad.getResult());
        switch (quad.getOperation()) {
            case "<":
                assembler.write("slt $" + resultReg + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;
            case ">":
                assembler.write("sgt $" + resultReg + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;
            case "=":
                // Pone resulReg a 1si son iguales
                // TODO: mirar si es correcto
                assembler.write("seq $" + resultReg + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;
            case "&":
                assembler.write("and $" + resultReg + ", $" + registerArg1 + ", $" + registerArg2 + "\n");
                break;
        }
        RegisterManager.freeRegister(quad.getArg1());
        RegisterManager.freeRegister(quad.getArg2());
    }

    private void declaration(Writer assembler, Quadruple quad) throws IOException {
        if(RegisterManager.doesMemoryExist(quad.getArg1())){
            String regVar = checkRegister(assembler, quad.getArg1(), true);
            String regFinal = RegisterManager.getRegisterByArg(quad.getResult());
            assembler.write("move $" + regFinal + ", $" + regVar + "\n");
            RegisterManager.setMemoryAllocation(quad.getResult(), regFinal, assembler);
            RegisterManager.freeRegister(quad.getArg1());
        } else {
            String registerToUse = RegisterManager.getRegisterByArg(quad.getArg1());
            if (registerToUse.equals("FULL")) {
                // TODO: control no registers left
                System.out.println("NO REGISTERS");
            } else {
                if (quad.getArg1() == null) {
                    assembler.write("li $" + registerToUse + ", 0\n");
                } else {
                    assembler.write("li $" + registerToUse + ", " + quad.getArg1() + "\n");
                }

                RegisterManager.setMemoryAllocation(quad.getResult(), registerToUse, assembler);
                RegisterManager.freeRegister(quad.getArg1());
            }
            RegisterManager.freeRegister(quad.getArg1());
        }
    }

    private void assignation(Writer assembler, Quadruple quad) throws IOException{
        if (currentNode.getSymbolTable().exists(quad.getResult())){
            String registerArg1 = checkRegister(assembler, quad.getArg1(), false);
            String registerResult = checkRegister(assembler, quad.getResult(), true);

            if(registerResult.matches("a[0-9]")){
                assembler.write("move $" + registerResult + ", $" + registerArg1 + "\n");
            } else {
                int regAddr = RegisterManager.getMemoryAllocation(quad.getResult());
                assembler.write("move $" + registerResult + ", $" + registerArg1 + "\n");
                assembler.write("sw $" + registerResult + ", " + regAddr + "($sp)\n");
            }
        }
    }

    private void operation(Writer assembler, Quadruple quad) throws IOException{
        String registerArg1;
        String registerArg2;
        String registerResult = RegisterManager.getRegisterByArg(quad.getResult());

        if(RegisterManager.getParamRegister(quad.getArg1()) != null){
            registerArg1 = RegisterManager.getParamRegister(quad.getArg1());
        } else if (currentNode.getSymbolTable().exists(quad.getArg1())){
            registerArg1 = RegisterManager.getRegisterByArg(quad.getArg1());
            int memAddr = RegisterManager.getMemoryAllocation(quad.getArg1());
            assembler.write("lw $" + registerArg1 + ", " + memAddr + "($sp)\n");
        }else if (RegisterManager.isNumber(quad.getArg1())) {
            registerArg1 = RegisterManager.getRegisterByArg(quad.getArg1());
            assembler.write("li $" + registerArg1 + ", " + quad.getArg1() + "\n");
        } else if (RegisterManager.doesMemoryExist(quad.getArg1())){
            registerArg1 = RegisterManager.getRegisterByArg(quad.getArg1());
            int memAddr = RegisterManager.getMemoryAllocation(quad.getArg1());
            assembler.write("lw $" + registerArg1 + ", " + memAddr + "($sp)\n");
        } else {
            registerArg1 = RegisterManager.getRegisterByArg(quad.getArg1());
        }

        if(RegisterManager.getParamRegister(quad.getArg2()) != null){
            registerArg2 = RegisterManager.getParamRegister(quad.getArg2());
        } else if (currentNode.getSymbolTable().exists(quad.getArg2())){
            registerArg2 = RegisterManager.getRegisterByArg(quad.getArg2());
            int memAddr = RegisterManager.getMemoryAllocation(quad.getArg2());
            assembler.write("lw $" + registerArg2 + ", " + memAddr + "($sp)\n");
        }else if (RegisterManager.isNumber(quad.getArg2())){
            registerArg2 = RegisterManager.getRegisterByArg(quad.getArg2());
            assembler.write("li $" + registerArg2 + ", " + quad.getArg2() + "\n");
        } else if (RegisterManager.doesMemoryExist(quad.getArg2())){
            registerArg2 = RegisterManager.getRegisterByArg(quad.getArg2());
            int memAddr = RegisterManager.getMemoryAllocation(quad.getArg2());
            assembler.write("lw $" + registerArg2 + ", " + memAddr + "($sp)\n");
        } else {
            registerArg2 = RegisterManager.getRegisterByArg(quad.getArg2());
        }

        RegisterManager.freeRegister(quad.getArg1());
        RegisterManager.freeRegister(quad.getArg2());

        switch (quad.getOperation()){
            case "+":
                assembler.write("add $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 +"\n");
                break;
            case "-":
                assembler.write("sub $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 +"\n");
                break;
            case "*":
                assembler.write("mul $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 +"\n");
                break;
            case "/":
                assembler.write("div $" + registerResult + ", $" + registerArg1 + ", $" + registerArg2 +"\n");
                break;
        }

        if(RegisterManager.doesMemoryExist(quad.getResult())){
            int memAddr = RegisterManager.getMemoryAllocation(quad.getResult());
            assembler.write("sw $" + registerResult + ", " + memAddr + "($sp)\n");
            //RegisterManager.freeRegister(quad.getResult());
        }
    }
}