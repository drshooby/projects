import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * MovieTags class for reading tags.csv and returning various data in the file
 * @author David Shubov
 */
public class MovieTags {

    /**
     * Movies class for creating custom movie objects, genre and tags have the same meaning in this program
     */

    public static class Movies {

        public String genre;
        public int freq;

        public Movies(String genre, int freq) {
            this.genre = genre;
            this.freq = freq;
        }

        public String getGenre() {
            return genre;
        }

        public int getFreq() {
            return freq;
        }

        public void setFreq(int freq) {
            this.freq = freq;
        }

    }

    /**
     * main method used for reading the tags.csv file and handling user inputs
     * @param args
     */
    public static void main(String[] args) {

        ArrayList<Movies> movieList = new ArrayList<>(); // used in sorting tags alphabetically
        System.out.println("Reading data file.....");
        // reading the file generally takes around 2-3 minutes with my Intel i7 and around 30 seconds for M2 Chip
        try {
            BufferedReader reader = new BufferedReader(new FileReader("tags.csv"));
            reader.readLine(); // skip first line
            String line = reader.readLine();
            while (line != null) {
                String[] entry = line.split(",");
                sort(movieList, entry[2].trim()); // perform insertion sort as items come in
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: file not found");
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Movies> copy = new ArrayList<>(movieList);
        // copy is a generic name for the list that will be used for sorting via tag count
        // all methods that utilize copy will take the same name as a param to avoid confusion from movieList
        mergesort(copy);
        leaderboard(copy);

        // section for handling user inputs
        try {
            Scanner scan = new Scanner(System.in);
            System.out.print("Search by Tag or Tag Count? (Enter T or C... or EXIT to exit): ");
            while (true) {
                String input = scan.nextLine();
                if (input.equalsIgnoreCase("T")) { // ignore case so program doesn't break if user enters "t" instead of "T"
                    System.out.print("Tag to search for: ");
                    input = scan.nextLine();
                    int res = BinarySearchTags(movieList, input, 0, movieList.size() - 1); // binary search through tags for target input
                    if (res == -1) {
                        System.out.println("Tag \"" + input + "\" does not exist");
                    } else {
                        System.out.println("Tag \"" + input + "\" occurred " + res + " times." );
                    }
                } else if (input.equalsIgnoreCase("C")) {
                    System.out.print("Count to search for: ");
                    input = scan.nextLine();
                    if (!input.matches("\\d+")) { // assistance with regex from chat.openai.com
                        System.out.println("Is \"" + input + "\" even a number? C'mon, man!"); // check to make sure tag count is numeric
                        System.out.print("Search by Tag or Tag Count? (Enter T or C... or EXIT to exit): ");
                        continue;
                    }
                    ArrayList<String> matchingTags = BinarySearchCount(copy, Integer.parseInt(input), 0, copy.size() - 1);
                    System.out.println("Tags with " + input + " occurrences: ");
                    if (matchingTags.isEmpty()) { // isEmpty checks to see if the array list returned by the count search is empty
                        System.out.println("no tags match this input...");
                    } else {
                        for (String tag : matchingTags) {
                            System.out.println("* " + tag);
                        }
                    }
                } else if (input.equalsIgnoreCase("EXIT")) {
                    System.out.println("Bye!");
                    break; // while stop condition
                } else {
                    System.out.println("Invalid option. Please try again...");
                }
                System.out.print("Search by Tag or Tag Count? (Enter T or C... or EXIT to exit): ");
            }
        } catch (java.util.NoSuchElementException e) {
            System.out.println("\nEarly program termination.");
        }
    } // end main

    /**
     * this method will print out the highest and lowest movies by tag count
     * @param copy the copy of movieList array list that is sorted by tags using merge sort
     */
    private static void leaderboard(ArrayList<Movies> copy) {
        System.out.println("==========================================");
        System.out.println("*** Highest 3 movies by count ***");
        System.out.println(copy.get(copy.size() - 1).getFreq() + ": " + copy.get(copy.size() - 1).getGenre());
        System.out.println(copy.get(copy.size() - 2).getFreq() + ": " + copy.get(copy.size() - 2).getGenre());
        System.out.println(copy.get(copy.size() - 3).getFreq() + ": " + copy.get(copy.size() - 3).getGenre());
        System.out.println("*** Lowest 3 movies by count ***");
        System.out.println(copy.get(0).getFreq() + ": " + copy.get(0).getGenre());
        System.out.println(copy.get(1).getFreq() + ": " + copy.get(1).getGenre());
        System.out.println(copy.get(2).getFreq() + ": " + copy.get(2).getGenre());
        System.out.println("==========================================");
    }


    // Linear Search and Insertion Sort
    // runtime: O(n)
    // space: O(n)

    /**
     * this sort method utilizes an insertion sort and linear search to add elements to add and sort movieList elements
     * @param movieList the array list sorted alphabetically by tags
     * @param genre the tag component of a movieList element stored as a String
     */
    private static void sort(ArrayList<Movies> movieList, String genre) {
        for (Movies movie : movieList) { // iterate through arraylist to find duplicates
            if (movie.getGenre().equals(genre)) { // if duplicate is found don't add to list just increase freq
                movie.setFreq(movie.getFreq() + 1);
                return;
            }
        }

        Movies movie = new Movies(genre, 1); // otherwise create a new movie object
        int i = 0;
        while ((i < movieList.size()) && (movieList.get(i).getGenre().compareTo(genre) < 0)) {
            i++;
        }
        // while loop used to search for insertion position
        movieList.add(i, movie);

        // assistance with insertion sort algorithm came from chat.openai.com for updating the frequency
    }


    // Merge Sort
    // runtime: O(nlgn)
    // space: O(n)

    /**
     * merge sort algorithm utilizes the copy array list to sort by tag count
     * @param list the copy array list that will be sorted by tag count
     */
    private static void mergesort(ArrayList<Movies> list) {
        if (list.size() > 1) {
            ArrayList<Movies> left = get_left(list);
            ArrayList<Movies> right = get_right(list);

            mergesort(left);
            mergesort(right);
            merge(list, left, right);
        }
    }

    private static ArrayList<Movies> get_left(ArrayList<Movies> list) {
        int size = list.size() / 2;
        ArrayList<Movies> left = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            left.add(i, list.get(i));
        }
        return left;
    }

    private static ArrayList<Movies> get_right(ArrayList<Movies> list) {
        int mid = list.size() / 2;
        int size = list.size() - mid;
        ArrayList<Movies> right = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            right.add(i, list.get(i+mid));
        }
        return right;
    }

    private static void merge(ArrayList<Movies> res, ArrayList<Movies> left, ArrayList<Movies> right) {
        int i = 0, l = 0, r = 0;
        while (l < left.size() && r < right.size()) {
            if (left.get(l).getFreq() < right.get(r).getFreq()) {
                res.set(i++, left.get(l++));
            } else {
                res.set(i++, right.get(r++));
            }
        }
        while (l < left.size()) {
            res.set(i++, left.get(l++));
        }
        while (r < right.size()) {
            res.set(i++, right.get(r++));
        }
    }

    // Binary Search
    // runtime: O(lgn)
    // space: O(1)

    /**
     * this method utilizes binary search to search for a desired tag count given a tag value
     * @param movieList the array sorted alphabetically by tags
     * @param target string that is being searched for
     * @param low index value of 0 for binary search
     * @param high index value of movieList.size() - 1
     * @return the tag count for the desired tag if it is found otherwise return -1
     */
    private static int BinarySearchTags(ArrayList<Movies> movieList, String target, int low, int high) {
        while (low <= high) {
            int mid = (low + high) / 2;
            if (movieList.get(mid).getGenre().equals(target)) {
                return movieList.get(mid).getFreq();
            } else if (movieList.get(mid).getGenre().compareTo(target) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    // Modified Binary Search
    // runtime: O(lgn)
    // space: O(1)

    /**
     * this method utilizes a modified binary search to search for potentially multiple tags given a count value
     * @param copy array list sorted by tag count
     * @param target integer tag count that is being searched for
     * @param low index value of 0 for binary search
     * @param high index value of copy.size() - 1
     * @return the tag(s) that match the desired tag count otherwise return an empty list
     */
    private static ArrayList<String> BinarySearchCount(ArrayList<Movies> copy, int target, int low, int high) {
        ArrayList<String> res = new ArrayList<>();
        int foundIndex = 0;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (copy.get(mid).getFreq() == target) {
                res.add(copy.get(mid).getGenre());
                foundIndex = mid;
                break;
            } else if (copy.get(mid).getFreq() < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        // typical binary search to begin
        if (foundIndex == 0) { // if an index for the first found element is not found then return an empty list
            return res;
        }

        int left = foundIndex - 1; // otherwise initialize left and right search indices
        int right = foundIndex + 1;

        while (left >= 0 && copy.get(left).getFreq() == target) { // iterate left and right to find remaining matches if they exist
            res.add(copy.get(left).getGenre());
            left--;
        }
        while (right < copy.size() && copy.get(right).getFreq() == target) {
            res.add(copy.get(right).getGenre());
            right++;
        }
        return res;
    }
} // end MovieTags
