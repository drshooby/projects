/*
 * Decode.java
 * by: David S.
 *
 * Used to decode text from an input file based on the codebook from Generate.
 * Takes two command-line arguments; the input file to decode and a file to output to.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Node {
    String symbol;
    char character;
    Node left;
    Node right;

    Node(String symbol, char character) { // defualt constructor
        this.symbol = symbol;
        this.character = character;
        left = null;
        right = null;
    }

    Node(String symbol) { // for non-leaf nodes
        this.symbol = symbol;
        this.character = (char) 0;
        left = null;
        right = null;
    }
}

class Output {

    void OutputToFile(String input, String codebook, String output) {
        Node root = new Node("", (char) 0);
        Node pointer = root;
        try {
            // readers and writer
            Scanner bReader = new Scanner(new File(codebook)); // "book" reader
            Scanner iReader = new Scanner(new File(input)); // input reader
            FileWriter outfile = new FileWriter(new File(output)); // outfile

            while (bReader.hasNextLine()) {
                String line = bReader.nextLine();
                String[] separate = line.split(":");
                int makeChar = Integer.parseInt(separate[0]);
                // vars to keep track off
                String symbol = separate[1]; // symbol
                char c = (char) makeChar; // char
                // building a tree
                pointer = root;
                for (int i = 0; i < symbol.length(); i++) {
                    String tmp = "";
                    tmp += symbol.charAt(i);
                    if (tmp.equals("0")) { // check left of tree
                        if (pointer.left == null) {
                            Node n = new Node(tmp);
                            pointer.left = n; // make the left of pointer equal to the new node
                            pointer = n; // now point to the new node
                        } else {
                            pointer = pointer.left;
                        }
                    } else if (tmp.equals("1")) { // check right of tree
                        if (pointer.right == null) {
                            Node n = new Node(tmp);
                            pointer.right = n;
                            pointer = n;
                        } else {
                            pointer = pointer.right;
                        }
                    }
                }
                pointer.character = c; // make sure to set the character in the currently pointed-to node
            }
            String inputLine = iReader.nextLine();
            boolean done = false;
            int count = 0;
            // Decoding the encoded file

            pointer = root; // start from the top
            while (!done) {
                if (inputLine.charAt(count) == '0') { // check left
                    pointer = pointer.left;
                    if (pointer.left == null || pointer.right == null){ // leaf node
                        if ((int) pointer.character == 4) { // EOT = stop
                            done = true;
                        } else { // otherwise, write the character to outfile
                            outfile.write(pointer.character);
                            pointer = root; // 'take it from the top' - of the tree
                        }
                    } else { // character "code" not found
                        count++;
                        continue;
                    }
                } else if (inputLine.charAt(count) == '1') { // check right, same deal
                    pointer = pointer.right;
                    if (pointer.left == null || pointer.right == null){
                        if ((int) pointer.character == 4) {
                            done = true;
                        } else {
                            outfile.write(pointer.character);
                            pointer = root;
                        }
                    } else {
                        count++;
                        continue;
                    }
                }
                count++;
            }
            bReader.close();
            iReader.close();
            outfile.close();
        } catch (IOException e) {
            System.err.println("ERROR: issue with file(s)");
        }
    } // end OutputToFile
}

public class Decode {
    public static void main(String[] args) {
        String input = args[0]; // encoded huffman file
        String codebook = "codebook";
        String output = args[1]; // outfile
        Output doStuff = new Output();
        doStuff.OutputToFile(input, codebook, output);
    }
}
