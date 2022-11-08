/*
 * Wordle.java
 * by David S.
 *
 * Rules and tips:
 * You have 6 tries to guess the 5 letter mystery word. Guessed letters in the exact position as
 * those in the random word will show up as the letter in the array result (ex. a), while
 * correct letters in the wrong position will show up in brackets in the array result (ex. [a]).
 *
 * Note: Please make sure to change the file paths to that on your computer for words.txt.
 * I have added a comment next to where the file paths should go.
 *
 * Good Luck!
 */

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Wordle {
    public static void main(String[] args) {

        WordList guessWord = new WordList();
        PlayGame yourWord = new PlayGame();
        Scanner scan = new Scanner(System.in);

        System.out.println("\nWelcome to Wordle!");
        System.out.println("The mystery word is a 5-letter English word.");
        System.out.println("You have 6 chances to guess it.\n");

        String wordToGuess = guessWord.getRandomWord(); // get the random word
        //System.out.println(wordToGuess);
        //if you are interested in seeing the random word, uncomment the previous statement.

        try { // this is inside a try-catch block because I want to display an error message if you end the program early
            for (int i = 1; i < 7; i++) {
                System.out.print("guess " + i + ":" + " ");
                String input = scan.nextLine();
                String guess = input.toLowerCase();

                while (guess.length() != 5 || !guess.matches("[a-zA-Z]+")) {
                    System.err.println("Your guess is either not 5 letters long OR contains a non-alphabetic character.");
                    System.out.print("guess " + i + ":" + " ");
                    guess = scan.nextLine();
                }
                yourWord.findLikeness(guess, wordToGuess);
            }
            scan.close();

        } catch (java.util.NoSuchElementException e) {
            System.err.println("\nEarly game termination!");
        }
    }
}

class WordList { // this class filters words.txt and saves all 5 letter words

    private int wordFinder() { // Method for counting 5 letter words

        int numFives = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("words.txt")); // words.txt file path here
            Closeable closer = reader;
            String line = reader.readLine();
            while (line != null) {
                if (line.length() == 5) {
                    numFives++;
                }
                line = reader.readLine();
            }
            closer.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: file not found");
            System.exit(0);
        } catch (EOFException e) {
            System.out.println("ERROR: end of file");
            System.exit(0);
        } catch (IOException e) {
            System.err.println("ERROR: unable to use file");
            System.exit(0);
        }
        return numFives;
    }


    private String[] fillWordsArray() { // Method for populating array based on the # of 5 letter words

        int count = 0;
        String[] myWords = new String[wordFinder()];
        try {
            BufferedReader reader = new BufferedReader(new FileReader("words.txt")); // words.txt file path here
            Closeable closer = reader;
            String line = reader.readLine();
            while (line != null) {
                if (line.length() == 5) {
                    myWords[count] = line;
                    count++;
                }
                line = reader.readLine();
            }
            closer.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: file not found");
        } catch (EOFException e) {
            System.out.println("ERROR: end of file");
        } catch (IOException e) {
            System.err.println("ERROR: unable to use file");
        }
        return myWords;
    }

    public String getRandomWord() {
        String[] words = fillWordsArray();
        String guessWord = words[(int) (Math.random() * words.length)];
        return guessWord;
    }
}

class PlayGame {

    private int guessCounter = 0;

    int[] countLetters(String randomWord) { // not necessary to write as separate method, but it helps with organization - counts letters of the random words

        int[] countLetters = new int[26];
        for (int i = 0; i < 5; i++) {
            int value = (int) randomWord.charAt(i) - 97;
            countLetters[value]++;
        }
        return countLetters;
    }

    void findLikeness(String input, String randomWord) {

        int[] counter = countLetters(randomWord);

        String[] temporary = new String[5]; // trick case handler
        for (int i = 0; i < temporary.length; i++) {
            String temp = "";
            temp += input.charAt(i);
            temporary[i] = temp;
        }

        String[] result = new String[5];
        result[0] = "_";
        result[1] = "_";
        result[2] = "_";
        result[3] = "_";
        result[4] = "_";

        for (int random = 0; random < input.length(); random++) { // rows
            for (int guess  = 0; guess < randomWord.length(); guess++) { // columns
                if (Character.valueOf(randomWord.charAt(random)).equals(Character.valueOf(input.charAt(guess))))  { // WORDLE MATRIX
                    if ((Character.valueOf(randomWord.charAt(random)).equals(Character.valueOf(input.charAt(guess)))) && (random == guess)) { // matching letter? This conditional triggers if its in the correct position
                        String temp = "";
                        temp += randomWord.charAt(random);
                        result[random] = temp;
                        counter[(int) randomWord.charAt(random) - 97] = counter[(int) randomWord.charAt(guess) - 97] - 1;
                        temporary[random] = "_"; // remove exact matchess after finding them
                    }
                    // remove matching + same pos letters
                }
            }
        }

        for (int i = 0; i < 5; i++) { // final check for trick case handler (ex. if guess has 3 a's and the answer has 2 a's, reflect that only 2 a's are necessary)
            if (counter[(int) input.charAt(i) - 97] == 0) {
                temporary[i] = "_";
            }
        }
        System.out.println(Arrays.toString(temporary));
        String test = ""; // string form of temporary array, should now be populated with some "_" if guess was close
        for (int i = 0; i < temporary.length; i++) {
            test += temporary[i];
        }

        for (int random = 0; random < test.length(); random++) { // checking letters that match but not same position
            for (int guess = 0; guess < randomWord.length(); guess++) {
                if ((Character.valueOf(randomWord.charAt(random)).equals(Character.valueOf(test.charAt(guess)))) && (random != guess)) {
                    String temp = "";
                    temp += test.charAt(guess);
                    int position = 0;
                    for (int i = 0; i < input.length(); i++) {
                        if (Character.valueOf(test.charAt(i)).equals(temp.charAt(0))) {
                            position = i;
                        }
                    }
                    result[position] = "[" + temp + "]";
                }
            }
        }
        // too many for-loops :)
        guessCounter++;

        String answer = "";

        for (int i = 0; i < result.length; i++) {
            answer += result[i];
        }

        if (!answer.equals(randomWord) && guessCounter != 6) {
            System.out.println(Arrays.toString(result));
        }

        if (answer.equals(randomWord)) {
            System.out.println(randomWord);
            System.out.println("Congrats! You guessed it!");
            System.exit(0);
        } else if (guessCounter == 6) {
            System.out.println("Sorry! Better luck next time!");
            System.out.println("The word was " + randomWord + ".");
        }
    }
}
