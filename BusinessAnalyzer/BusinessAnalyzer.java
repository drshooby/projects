import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * BusinessAnalyzer.java
 * @author David Shubov
 * Description: Parses the registered business locations in San Francisco csv and handles various user inqueries
 */
public class BusinessAnalyzer {

    int closedBusinesses;
    int newBusinesses;
    List<DataObject> businessList;
    ArrayDeque<String> historyQ = new ArrayDeque<>();

    public BusinessAnalyzer(String listType) {
        if (listType.equalsIgnoreCase("LL")) {
            businessList = new LinkedList<>(); // add object type
            return;
        }
        if (listType.equalsIgnoreCase("AL")) {
            businessList = new ArrayList<>();
            return;
        }
        throw new RuntimeException("invalid option");
    }

    private static class DataObject {
        String zip;
        String NAICS;
        String BAN; // business account number
        String neighborhood;

        public DataObject(String zip, String NAICS, String BAN, String neighborhood) {
            this.zip = zip;
            this.NAICS = NAICS;
            this.BAN = BAN;
            this.neighborhood = neighborhood;
        }
    } // end DataObject


    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Invalid command-line arguments! First argument is the SF Registered Businesses file name" + "\n" + "and second should be the desired list implementation.");
        }
        BusinessAnalyzer program = new BusinessAnalyzer(args[1]);
        program.ReadData(args[0]);
        program.InputHandler();
    } // end main

    /**
     * ReadData(): Parses data from the SF Registered Business Locations CSV and stores in selected list option
     * Runtime: O(n)
     * Space: O(n)
     */
    private void ReadData(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            System.out.println("Reading file...");
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] entry = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // regex for ignoring commas within quotes
                DataObject data = new DataObject(entry[14], entry[16], entry[1], entry[23]);
                businessList.add(data);
                if (entry[12].equals("***Administratively Closed")) {
                    closedBusinesses++;
                } else {
                    String[] startDate = entry[8].split("/");
                    if (startDate[2].equals("2022")) {
                        newBusinesses++;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("problem with reading the file");
            System.exit(0);
        } catch (Exception e) {
            throw new RuntimeException("problem with adding element to list");
        }
    } // end ReadData

    /**
     * InputHandler() handles user input requests
     * Runtime and space depend on executed commands
     */
    private void InputHandler() {
        Scanner scan = new Scanner(System.in);
        String input = "";
        while (!input.equalsIgnoreCase("quit")) {
            System.out.print("Command: ");
            input = scan.nextLine();

            String[] inputTokens = input.toLowerCase().split(" ");

            switch (inputTokens[0]) { // idea help with switch cases from chat.openai.com
                case "zip":
                    ZipHandler(inputTokens);
                    break;
                case "naics":
                    NaicsHandler(inputTokens);
                    break;
                case "summary":
                    SummaryHandler();
                    break;
                case "history":
                    HistoryHandler();
                    break;
                case "quit":
                    break;
                default:
                    System.out.println("Invalid command");
            }
            historyQ.add(input);
        }
    } // end InputHandler

    /**
     * HistoryHandler() prints out the user's command history
     * Runtime: O(n)
     * Space: O(1)
     */
    private void HistoryHandler() {
        System.out.println();
        for (String item : historyQ) {
            System.out.println(item + "\n");
        }
    }

    /**
     * SummaryHandler() prints the total amount of businesses and businesses closed
     * Runtime: O(1)
     * Space: O(1)
     */
    private void SummaryHandler() {
        System.out.println("\n" + "Total Businesses: " + businessList.size() + "\n");
        System.out.println("Closed Businesses: " + closedBusinesses + "\n");
        // businesses in last year (2022)
        System.out.println("New Business in last year: "+ newBusinesses + "\n");
    }

    /**
     * NaicsHandler() handles and prints information related to the naics code input
     * @param inputTokens are the tokens from the user input
     * Runtime: O(n^2) (the second n value is going to be very small so it may be more fitting to say its O(n))
     * Space: O(n)
     */
    private void NaicsHandler(String[] inputTokens) {

        if (inputTokens.length != 3) {
            System.out.println("Invalid command. Please enter naics command as: NAICS *NAICS Code* Summary\n");
            return;
        }

        Literator<DataObject> busIterator = businessList.getIterator();

        Set<String> zips = new HashSet<>(); // Prof said sets were okay to use
        Set<String> neighborhoods = new HashSet<>();
        int totalBusinesses = 0;

        int naicsInput = Integer.parseInt(inputTokens[1]);

        while (busIterator.hasNext()) {
            DataObject curr = busIterator.next();
            String[] currNAICSvalues = curr.NAICS.split(" ");
            boolean naicsInRange = false; // assistance with boolean flag idea from chat.openai.com
            for (String naics : currNAICSvalues) {
                // if only one code range
                if (naics.equals("")) {
                    continue;
                }
                String[] codeRange = naics.split("-");
                // ranges
                int low = Integer.parseInt(codeRange[0]); // adds runtime, but space should be the same and it's easier to compare with integers
                int high = Integer.parseInt(codeRange[1]);
                // if greater than or equal to low range or less than or equal to high range its valid
                if (naicsInput >= low && naicsInput <= high) {
                    naicsInRange = true;
                    break;
                }
            }
            if (naicsInRange) {
                if (curr.zip.length() == 5) { // a case has been noted where a value under zipcodes is titled "TAT", this is to filter that case.
                    zips.add(curr.zip);
                }
                if (!curr.neighborhood.isEmpty()) {
                    neighborhoods.add(curr.neighborhood);
                }
                totalBusinesses = totalBusinesses + 1;
            }
        }
        System.out.println("\n" + "Total Businesses: " + totalBusinesses + "\n");
        System.out.println("Zip Codes: " + zips.size() + "\n");
        System.out.println("Neighborhood: " + neighborhoods.size() + "\n");
    } // end NaicsHandler

    /**
     * ZipHandler() handles and prints information related to the zip code input
     * @param inputTokens are the tokens from the user input
     * Runtime: O(n)
     * Space: O(n)
     */
    private void ZipHandler(String[] inputTokens) {

        if (inputTokens.length != 3) {
            System.out.println("Invalid command. Please enter zip command as: Zip *Zip Code* Summary");
            return;
        }

        if (inputTokens[1].length() != 5) {
            System.out.println("Invalid Zip");
            return;
        }

        Set<String> neighborhoods = new HashSet<>();
        Set<String> businessTypes = new HashSet<>();
        int totalBusinesses = 0;


        Literator<DataObject> busIterator = businessList.getIterator();

        while (busIterator.hasNext()) { // iterates through main list and checks zips
            DataObject curr = busIterator.next();
            if (curr.zip.equals(inputTokens[1])) { // only add matching zips
                // Implementation changed to better fit output examples from assignment doc
                totalBusinesses = totalBusinesses + 1;
                if (curr.neighborhood.length() != 0) {
                    neighborhoods.add(curr.neighborhood);
                }
                if (!curr.NAICS.isEmpty()) {
                    businessTypes.add(curr.NAICS);
                }
            }
        }
        System.out.println("\n" + inputTokens[1] + " Business Summary" + "\n");
        System.out.println("Total Businesses: " + totalBusinesses + "\n");
        System.out.println("Business Types: " + businessTypes.size() + "\n");
        System.out.println("Neighborhoods: " + neighborhoods.size() + "\n");
    } // end ZipHandler
} // end BusinessAnalyzer


