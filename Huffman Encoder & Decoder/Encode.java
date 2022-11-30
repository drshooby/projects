/*
 * Encode.java
 * by: David S.
 *
 * Used to encode text from an input file based on the codebook from Generate.
 * Takes two command-line arguments; the input file to encode and a file to output to.
 */


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Encode {
    public static void main(String[] args) {
        try {
            FileWriter output = new FileWriter(new File(args[1])); // outfile, label "encodedfile.txt"
            Scanner input = new Scanner(new File(args[0])); // input file, label "encode.txt"
            Scanner bReader = new Scanner(new File("codebook")); // "book" reader
            HashMap<Character, String> map = new HashMap<Character, String>();

            while (bReader.hasNextLine()) { // create the table
                String line1 = bReader.nextLine(); // line1 var for bReader
                String[] separate = line1.split(":"); // split table values by ":"
                try {
                    int toChar = Integer.parseInt(separate[0]);
                    char c1 = (char) toChar;
                    map.put(c1, separate[1]); // adding to key/value to hashmap
                } catch (java.lang.NumberFormatException e) {
                    System.err.println("ERROR: converting string to int");
                }
            }
            while (input.hasNextLine()) { // write to output using map keys
                String line2 = input.nextLine(); // line2 var for input
                for (int i = 0; i < line2.length(); i++) { // iterate through line of input file to grab chars
                    char c2 = line2.charAt(i);
                    try {
                        output.write(map.get(c2)); // write to outfile
                    } catch (IOException e) {
                        System.err.println("ERROR: issue with writing to output");
                    }
                }
                output.write(map.get('\n'));
            }
            output.write(map.get('\u0004')); // add end of transmission character at the end


            output.close();
            input.close();
            bReader.close();
        } catch (IOException e) {
            System.err.println("ERROR: file not found");
        }
    }
}
