package lexical;

import java.io.*;

public class FileManager {
    private final String filename;
    private BufferedReader reader;

    public FileManager(String fileName) {
        this.filename = fileName;
        // We check in case the file does not exist or is not found
        try {
            this.reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException f) {
            Exception e = new Exception("????? The input source file '" + fileName + "' was not found");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public BufferedReader getReader() {
        return reader;
    }

    public boolean isAtEnd() {
        try {
            reader.mark(1);
            boolean isEnd = (reader.read() == -1);
            reader.reset();

            return isEnd;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean checkFileIsEmpty() {
        // Self-explainatory, we will check if the file contains anything or nothing at all
        File file = new File(filename);
        return file.length() <= 0;
    }

    public String readSingleToken() {
        try {
            StringBuilder sb = new StringBuilder();

            int character;
            while ((character = reader.read()) != -1) {
                char c = (char) character;

                if (c == '\r') continue;

                if (c == ' ') {
                    reader.mark(1);
                    while ((character = reader.read()) != -1 && (char) character == ' ') {
                        reader.mark(1);
                    }

                    reader.reset();
                    break;
                } else if (c == '\n') {
                    if (sb.length() > 0) break; //Para que se retorne el token leido hasta el momento
                    else {
                        try {
                            reader.reset(); //Vuelve a leer el ultimo \n leido
                        } catch (IOException ignored) {
                            return null;
                        }
                        if ((character = reader.read()) != -1 && (character = (char) character == '\r' ? reader.read() : character) != -1 && (char) character == '\n') {
                            if ((character = reader.read()) != -1 && (character = (char) character == '\r' ? reader.read() : character) != -1 && (char) character == '\n') {
                                reader.mark(1);
                                if ((character = reader.read()) != -1 && (character = (char) character == '\r' ? reader.read() : character) != -1 && (char) character == '\n') {    //Si hay \n\n\n, se envia como palabra (end block token)
                                    sb.append("\n\n\n");

                                    while ((char) character == '\r' || (char) character == '\n') {
                                        reader.mark(1);
                                        character = reader.read();
                                        if (character == -1) break;
                                    }

                                    reader.reset();
                                    break;
                                } else reader.reset();
                            }
                        }
                    }
                } else if (c == '.' || c == '!' || c == ':' || c == '?' || c == '-' || c == ',') {
                    if (sb.length() > 0) {
                        reader.reset();
                        break;
                    } else {
                        sb.append(c);

                        reader.mark(1);
                        if ((character = reader.read()) != -1 && (char) character == '.') {    //Caso para '...'
                            if ((character = reader.read()) != -1 && (char) character == '.') {
                                sb.append("..");
                            } else reader.reset();
                        } else reader.reset();

                        reader.mark(1);
                    }
                } else {
                    reader.mark(1); //Pointer al ultimo char leido
                    sb.append(c);
                }
            }

            return sb.length() > 0 ? sb.toString() : "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
