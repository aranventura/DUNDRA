package lexical;

import syntactic.symbolTableTree.SymbolTableTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    private FileManager fileManager;
    private HashMap<String, String> dict;
    private ArrayList<String> words;
    private int i_tokens;
    private SymbolTableTree symbolTableTree;

    public LexicalAnalyzer(String filename, SymbolTableTree symbolTableTree) {
        this.fileManager = new FileManager(filename);
        this.words = new ArrayList<>();
        this.symbolTableTree = symbolTableTree;
        this.i_tokens = 0;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public void generateDictionary(String filename){
        dict = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                Pattern pattern = Pattern.compile(parts[0]);
                dict.put(pattern.pattern(), parts[1]);
            }
        } catch (IOException e) {
            System.err.println("Error loading dictionary file: " + e.getMessage());
        }
    }


    private String getNextWord() {
        if (i_tokens == words.size()){
            String word = this.fileManager.readSingleToken();
            if (word != null) {
                words.add(word);
                return word;
            } else {
                return null;
            }
        } else {
            return words.get(i_tokens);
        }
    }


    private Token analizeToken(Token firstWord) {
        int q_words = 1;
        Token token = firstWord;

        switch (firstWord.getValue()) {
            // DEFINICIÓN DE TOKENS DE INICIO Y FIN MAIN
            case "Erase":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("una")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if(words.get(words.size()-1).equals("vez")) {
                        token = new Token(Token.Type.START_MAIN, "Erase una vez");
                    } else {
                        q_words -= 2;
                    }

                } else {
                    q_words--;
                }
                break;

            case "Fin":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("de")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if (words.get(words.size()-1).equals("la")) {
                        words.add(this.fileManager.readSingleToken());
                        q_words++;

                        if(words.get(words.size()-1).equals("historia")) {
                            token = new Token(Token.Type.END_MAIN, "Fin de la historia");
                        } else {
                            q_words -= 3;
                        }
                    } else {
                        q_words -= 2;
                    }
                } else {
                    q_words--;
                }
                break;

            // DEFINICIÓN DE TOKENS DE COMENTARIOS
            case "El":
                words.add(this.fileManager.readSingleToken());
                q_words++;
                if(words.get(words.size()-1).equals("DM")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;
                    if(words.get(words.size()-1).equals("penso")) {
                        String word;
                        do {
                            word = this.fileManager.readSingleToken();
                            words.add(word);
                            q_words++;
                        } while (!word.equals("."));

                        token = new Token(Token.Type.IGNORE, "");

                    } else if (words.get(words.size()-1).equals("dijo")) {
                        token = new Token(Token.Type.PRINT, firstWord.getValue());
                    } else q_words -= 2;

                } else if (words.get(words.size()-1).equals("viaje")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if(words.get(words.size()-1).equals("termino")) {
                        token = new Token(Token.Type.EL_VIAJE_TERMINO, "El viaje termino");
                    } else {
                        q_words -= 2;
                    }
                } else {
                    q_words--;
                }
                break;

            //DEFINICION DE TOKEN DE ASIGNAR VALOR
            case "tiene":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("de")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if(words.get(words.size()-1).equals("valor")) {
                        token = new Token(Token.Type.TIENE_VALOR, "tiene de valor");
                    } else {
                        q_words -= 2;
                    }

                } else {
                    q_words--;
                }
                break;

            // DEFINICIÓN DE TOKENS DE VERBOS
            case "llamado":
                token = new Token(Token.Type.ASSIGN, firstWord.getValue());
                break;

            // DEFINICIÓN DE TOKENS DE TIPO
            case "barbaro":
                token = new Token(Token.Type.BARBARO, firstWord.getValue());
                break;

            case "mago":
                token = new Token(Token.Type.MAGO, firstWord.getValue());
                break;

            case "picaro":
                token = new Token(Token.Type.PICARO, firstWord.getValue());
                break;

            case "paladin":
                token = new Token(Token.Type.PALADIN, firstWord.getValue());
                break;

            // DEFINICIÓN DE TOKENS DE END
            case ".":
            case "?":
            case "...":
            case "!":
                token = new Token(Token.Type.END, firstWord.getValue());
                break;

            case "-":
                token = new Token(Token.Type.NEGATIVE, firstWord.getValue());
                break;

            case "vio":
                token = new Token(Token.Type.IGUALACION, firstWord.getValue());
                break;

            case "curo":
                token = new Token(Token.Type.SUMA, firstWord.getValue());
                break;

            case "ataco":
                token = new Token(Token.Type.RESTA, firstWord.getValue());
                break;

            case "inspiro":
                token = new Token(Token.Type.MULT, firstWord.getValue());
                break;

            case "separo":
                token = new Token(Token.Type.DIV, firstWord.getValue());
                break;

            case "y":
                token = new Token(Token.Type.AND, firstWord.getValue());
                break;

            case "o":
                token = new Token(Token.Type.OR, firstWord.getValue());
                break;

            case "menor":
            case "mayor":
            case "igual":
            case "diferente":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("que")) {
                    firstWord.setValue(firstWord.getValue() + " que");
                    if (firstWord.getValue().contains("menor")) token = new Token(Token.Type.MENOR, firstWord.getValue());
                    if (firstWord.getValue().contains("mayor")) token = new Token(Token.Type.MAYOR, firstWord.getValue());
                    if (firstWord.getValue().contains("igual")) token = new Token(Token.Type.IGUAL, firstWord.getValue());
                    if (firstWord.getValue().contains("diferente")) token = new Token(Token.Type.DIF, firstWord.getValue());
                } else if (words.get(words.size()-1).equals("o") && (firstWord.getValue().contains("menor") || firstWord.getValue().contains("mayor"))) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if (words.get(words.size()-1).equals("igual")) {
                        words.add(this.fileManager.readSingleToken());
                        q_words++;

                        if(words.get(words.size()-1).equals("que")) {
                            firstWord.setValue(firstWord.getValue() + " o igual que");

                            if (firstWord.getValue().contains("menor"))
                                token = new Token(Token.Type.MENOR_O_IGUAL, firstWord.getValue());
                            if (firstWord.getValue().contains("mayor"))
                                token = new Token(Token.Type.MAYOR_O_IGUAL, firstWord.getValue());
                        } else q_words -= 3;
                    } else q_words -= 2;
                } else q_words--;

                break;

            case "Si":
            case "si":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("no")) {
                    token = new Token(Token.Type.ELSE, firstWord.getValue() + " no");
                } else {
                    token = new Token(Token.Type.IF, firstWord.getValue());
                    q_words--;
                }

                break;

            case "Entonces":
            case "entonces":
                token = new Token(Token.Type.THEN, firstWord.getValue());
                break;

            case "nada":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("mas")) {
                    token = new Token(Token.Type.ENDIF, firstWord.getValue() + " mas");
                } else q_words--;

                break;

            case "combate":
                token = new Token(Token.Type.LOOP, firstWord.getValue());
                break;

            case "rondas":
                token = new Token(Token.Type.ROUNDS, firstWord.getValue());
                break;

            case "\n\n\n":
                token = new Token(Token.Type.END_BLOCK, "\\n\\n\\n");
                break;

            case "bifurcacion":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("de")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if (words.get(words.size()-1).equals("caminos")) {
                        token = new Token(Token.Type.SWITCH, "bifurcacion de caminos");
                    } else q_words -= 2;
                } else q_words--;

                break;

            case ":":
                token = new Token(Token.Type.COLON, firstWord.getValue());
                break;

            case "Final":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("de")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if(words.get(words.size()-1).equals("camino")) {
                        token = new Token(Token.Type.SWITCH_BREAK, "Final de camino");
                    } else q_words -= 2;
                } else q_words--;

                break;

            case "cierto":
                token = new Token(Token.Type.BOOL, firstWord.getValue());
                break;

            case "falso":
                token = new Token(Token.Type.BOOL, firstWord.getValue());
                break;

            // Start operacion aritmetica
            case "En":
                words.add(this.fileManager.readSingleToken());
                q_words++;
                if(words.get(words.size()-1).equals("la")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;
                    if (words.get(words.size()-1).equals("pelea")) {
                        words.add(this.fileManager.readSingleToken());
                        token = new Token(Token.Type.START_ARITMETICA, "En la pelea");
                    } else q_words -= 2;
                } else q_words--;
                break;

            case "Volveria":
                token = new Token(Token.Type.VOLVERIA, firstWord.getValue());
                break;

            case "viaje":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("a")) {
                    token = new Token(Token.Type.VIAJE_A, firstWord.getValue() + " a");
                } else q_words--;

                break;

            case "integrantes":
                token = new Token(Token.Type.INTEGRANTES, firstWord.getValue());
                break;

            case "ningun":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("integrante")) {
                    token = new Token(Token.Type.NINGUN_INTEGRANTE, firstWord.getValue() + " integrante");
                } else q_words--;
                break;

            case ",":
                token = new Token(Token.Type.COMMA, firstWord.getValue());
                break;

            case "con":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("valor")) {
                    token = new Token(Token.Type.CON_VALOR, firstWord.getValue() + " valor:");
                } else q_words--;
                break;

            case "sin":
                words.add(this.fileManager.readSingleToken());
                q_words++;

                if(words.get(words.size()-1).equals("ningun")) {
                    words.add(this.fileManager.readSingleToken());
                    q_words++;

                    if(words.get(words.size()-1).equals("valor")) {
                        token = new Token(Token.Type.SIN_NINGUN_VALOR, "Final de camino");
                    } else q_words -= 2;
                } else q_words--;

                break;


            default:
                if (firstWord.getValue().equals("") && fileManager.isAtEnd()) {
                    token.setType(Token.Type.EOF);
                    break;
                }

                try {
                    if (words.get(words.size() - 2).equals("llamado") || symbolTableTree.isInScope(firstWord.getValue()) || (words.size() > 2 && words.get(words.size() - 3).equals("viaje") && words.get(words.size() - 2).equals("a"))) {
                        token.setType(Token.Type.ID);
                        break;
                    }
                } catch (IndexOutOfBoundsException ignored) {}

                if(firstWord.getValue().matches("[0-9]+")) {
                    token = new Token(Token.Type.INT, firstWord.getValue());
                    break;
                }

                if (firstWord.getValue().matches("[0-9]*['][0-9]+")) {
                    token = new Token(Token.Type.FLOAT, firstWord.getValue());
                    break;
                }

                if (token.getValue().length() == 3 && token.getValue().charAt(0) == '<' && token.getValue().charAt(2) == '>') {
                    token.setType(Token.Type.CHAR);
                    token.setValue(token.getValue().replace("<", "").replace(">", ""));
                }

                break;
        }

        i_tokens += q_words;
        return token;
    }


    public Token getNextToken() {

        String actualWord = getNextWord();
        if (actualWord != null) {
            Token token = new Token(Token.Type.OTHER, actualWord);
            Token tokenAnalized = analizeToken(token);

            /*DEBUG*/// System.out.println(tokenAnalized.toString());

            return tokenAnalized;
        } else {
            return new Token(Token.Type.EOF, "$");
        }

    }
}