// TestBubble.java
// By David S.
// This file tests the python bubble sort to make sure all 10,000 integers are sorted and exist

import java.io.*;
import java.util.Scanner;

class ReadTest {
    void TryFile() {
        try {
            int counter = 1; // had to set this to 1 because sort stops at counter = 9999 which gives FAIL status
            Scanner scan = new Scanner(new File(("sort.txt")));
            int nextNum = scan.nextInt();
            int compare = nextNum; // the algorithm will be comparing two integers at a time
            while (scan.hasNextInt()){
                nextNum = scan.nextInt();
                if (compare <= nextNum) {
                    compare = nextNum; // keeping going if the order is good or the compared ints are the same
                } else {
                    System.out.println("SORT FAIL: incorrect sort..." + compare + " is not less than " + nextNum);
                    System.exit(0);
                }
                counter++;
            }
            if (counter != 10000) {
                System.out.println("SORT FAIL: incorrect element count " + counter + "\\10000");
                System.exit(0);
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: file not found");
            System.exit(0);
        }
        System.out.println("ALL TESTS PASS!");
    } // end ReadFile
} // end ReadTest class

public class TestBubble {
    public static void main(String[] args) {
        ReadTest test = new ReadTest();
        test.TryFile();
    }
}