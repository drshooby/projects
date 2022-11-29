/*
 * Generate.java
 * by: David S.
 *
 * Used to generate a Huffman Codebook based on a training file.
 * Reads the input from a file titled trainer.txt (change if necessary) to create the book.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Node implements Comparable<Node>{ // class for making nodes

    char character;
    int count;
    String symbol;
    Node left;
    Node right;

    Node(Character character, Integer count, String symbol) { // general Node constructor
		this.character = character;
		this.count = count;
		this.symbol = symbol;
		left = null;
		right = null;
	}

    Node(Integer count) { // for non-leaf nodes (for the sum of two nodes in Huffman Algo)
        this.character = (char) 1;
        this.count = count;
        this.symbol = null;
        left = null;
        right = null;
    }


    public int compareTo(Node n) {
        return Integer.compare(count, n.count);
    }

} // end Node


class List {
    // make nodes for every char, not only ones in text file
    ArrayList<Node> CreateList(HashMap<Character, Integer> map) {
        ArrayList<Node> list = new ArrayList<Node>();
        for (Map.Entry<Character, Integer> elements : map.entrySet()) {
            Character key = elements.getKey();
            Integer value = elements.getValue();
            Node n = new Node(key, value, null);
            list.add(n);
        }
        return list;
    }

}

class CharCounter { // from previous lab to count characters in an input

    private int[] charCount = new int[256];
    private HashMap<Character, Integer> map = new HashMap<Character, Integer>();

    void CountChars(String line) {
        try {
            for (int i = 0; i < line.length(); i++) {
                charCount[(int) line.charAt(i)]++; // add 1 to the position in the int[] that corresponds with the decimal unicode value of the character
            }
            map.put('\u0004', 0); // end of transmission
            for (int i = 0; i < line.length(); i++) {
                if (!map.containsKey(line.charAt(i))) {
                    map.put(line.charAt(i), 1);
                } else { // if key is already present and char is found again, add 1 to count
                    map.put(line.charAt(i), map.get(line.charAt(i)) + 1);
                }
            }

            for (int i = 7; i < 255; i++) { // add the rest of unicode chars
                if (!map.containsKey((char) i)) {
                    map.put((char) i, 0);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("ERROR: character not between unicode 0-256" + e);
            System.exit(0);
        }
    }

    HashMap<Character, Integer> GetMap() {
        return map;
    }

} // end CharCounter

class HuffmanAlgo {
    Node root;

    void DoHuffman(ArrayList<Node> L) { // create huffman code book
        try {
            FileWriter writer = new FileWriter(new File("codebook"));
            ArrayList<Node> list = L;
            /*
             * Huffman Algorithm
            */
            while (list.size() != 1) {
                Collections.sort(list);
                Node n1 = list.get(0);
                Node n2 = list.get(1);
                Node n = new Node(n1.count + n2.count);
                n.right = n1;
                n.left = n2;
                list.remove(0);
                list.remove(0); // because second element becomes new first
                list.add(n);
            }
            root = list.get(0);
            SetRoot();
            PrintNodes(root, writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("ERROR: file not found");
        }
    }

    void SetRoot() { // add symbols to root
        root.symbol = "";
        root.left.symbol = "0";
        root.right.symbol = "1";
        Set(root.left);
        Set(root.right);
    }

    void Set(Node n) { // add symbols to rest, search recursively for leaf nodes
        if (n.left != null) {
            n.left.symbol = n.symbol + "0";
            Set(n.left);
        }
        if (n.right != null) {
            n.right.symbol = n.symbol + "1";
            Set(n.right);
        }
    }

    void PrintNodes(Node n, FileWriter writer) { // recursively print node values
        if (n.left == null && n.right == null) { // leaf node that contains the char
            try {
                writer.write(String.valueOf((int) n.character) + ":" + n.symbol + "\n"); // format
            } catch (IOException e) {
                System.err.println("ERROR: issue with writing to codebook");
            }
        }
        if (n.left != null) {
            PrintNodes(n.left, writer);
        }
        if (n.right != null) {
            PrintNodes(n.right, writer);
        }
    }


} // end HuffmanAlgo

public class Generate{
    public static void main(String[] args) {
        try {
            // class objects
            Scanner scan = new Scanner(new File("trainer.txt")); // training file here
            CharCounter counter = new CharCounter(); // count characters in file
            List createList = new List(); // create list for all nodes
            HuffmanAlgo huff = new HuffmanAlgo(); // huffman algorithm
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                counter.CountChars(line);
            }
            ArrayList<Node> nodeList = createList.CreateList(counter.GetMap());
            huff.DoHuffman(nodeList); // huffman on the nodelist with sorting
            scan.close();
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: file not found");
        }

    }
} // end Generate