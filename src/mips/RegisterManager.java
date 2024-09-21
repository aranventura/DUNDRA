package mips;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

public class RegisterManager {

    private static Register[] registers;
    private static HashMap<String, String> registerMap;
    private static HashMap<String, Integer> memoryMap; // Var -> memory position (offset)
    private static HashMap<String, String> paramsMap;
    private static int currentAddress;

    static {
        registerMap = new HashMap<>();
        registers = new Register[]{new Register("t",0),new Register("t",1),new Register("t",2),new Register("t",3),new Register("t",4),new Register("t",5),new Register("t",6),new Register("t",7),new Register("t",8),new Register("t",9)};
        memoryMap = new HashMap<>();
        paramsMap = new HashMap<>();
        currentAddress = 0;
    }

    public static String getRegisterByArg(String request) {
        if(registerMap.containsKey(request)){
            return registerMap.get(request);
        }
        String r = registerAvailable();
        if(r.equals("FULL")){
            // No registers available, treat it after
            return "FULL";
        }
        registerMap.put(request, r);
        return r;
    }

    public static void eraseParamsFunction(){
        paramsMap.clear();
    }

    public static void setParamFunction(String nameVar, String registerName, Writer assembler) throws IOException {
        if(!paramsMap.containsKey(nameVar)){
            paramsMap.put(nameVar, registerName);
            setMemoryAllocation(nameVar, registerName, assembler);
        }
    }

    public static String getParamRegister(String nameVar){
        return paramsMap.get(nameVar);
    }


    public static void setMemoryAllocation(String nameVar, String registerToUse, Writer assembler) throws IOException {
        if(!memoryMap.containsKey(nameVar)){
            memoryMap.put(nameVar, currentAddress);
            assembler.write("sw $" + registerToUse + ", " + currentAddress + "($sp)\n");
            //TODO: passarli la symbol table i mirartipus de var per sumar diferents valors, fer funció
            currentAddress += 4;
        } else {
            int addr = memoryMap.get(nameVar);
            assembler.write("sw $" + registerToUse + ", " + addr + "($sp)\n");
        }
    }

    public static Integer getMemoryAllocation(String nameVar){
        return memoryMap.get(nameVar);
    }

    public static void freeRegister(String value){
        String regToFree = registerMap.get(value);
        for (int i = 0; i < registers.length; i++) {
            Register r = registers[i];
            String register = r.getType() + r.getNumber();
            if (register.equals(regToFree)){
                registers[i].setUsed(false);
            }
        }
        registerMap.remove(value);
    }

    private static String registerAvailable() {
        for (int i = 0; i < registers.length; i++) {
            Register r = registers[i];
            if (!r.isUsed()){
                registers[i].setUsed(true);
                return r.getType() + r.getNumber();
            }
        }
        return "FULL";
    }

    public static boolean isNumber(String input){
        try {
            // Attempt to parse the string as an integer
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e1) {
            return false;
        }
    }

    public static boolean doesMemoryExist(String value) {
        return memoryMap.containsKey(value);
    }

}
