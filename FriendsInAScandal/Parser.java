import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author David Shubov
 * Parser.java
 */

public class Parser {

    /**
     * parseFiles for parsing each file in the Enron maildir directory
     * @param directoryPath path to maildir
     * @param graph graph to be created
     * Runtime: O(n * m) where n is the number of files in a directory and m is avg. lines per file
     * Space: O(n)
     */
    public void parseFiles(String directoryPath, Graph graph) {
        // assistance with figuring out how to work with multiple subdirectories from chat.openai.com
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        String fromEmail = null; // String because there is typically only one sender of an email
                        List<String> toEmails = new ArrayList<>(); // but there can be multiple recipients
                        while ((line = reader.readLine()) != null) { // read through the file
                            // check sender
                            if (line.startsWith("From:") && !line.substring(5).trim().isEmpty()) {
                                String email = extractEmail(line); // make sure email is in valid format
                                if (email != null) {
                                    fromEmail = email;
                                }
                            // check recipient(s)
                            } else if (line.startsWith("To:") || line.startsWith("Cc:") && !line.substring(3).trim().isEmpty()) {
                                String[] emails = line.substring(3).trim().split("[,\\\\s]+");
                                for (String email : emails) {
                                    email = extractEmail(email);
                                    if (email != null) {
                                        toEmails.add(email);
                                    }
                                }
                                // check for additional lines with email addresses
                                while ((line = reader.readLine()) != null && line.matches("^[,\\\\s].*")) { // check next lines for trailing emails
                                    emails = line.trim().split("[,\\\\s]+");
                                    for (String email : emails) {
                                        email = extractEmail(email);
                                        if (email != null) {
                                            toEmails.add(email);
                                        }
                                    }
                                }
                                // check recipient(s)
                            } else if (line.startsWith("Bcc") && !line.substring(4).trim().isEmpty()) {
                                String[] emails = line.substring(4).trim().split("[,\\\\s]+");
                                for (String email : emails) {
                                    email = extractEmail(email);
                                    if (email != null) {
                                        toEmails.add(email);
                                    }
                                }
                                while ((line = reader.readLine()) != null && line.matches("^[,\\\\s].*")) {
                                    emails = line.trim().split("[,\\\\s]+");
                                    for (String email : emails) {
                                        email = extractEmail(email);
                                        if (email != null) {
                                            toEmails.add(email);
                                        }
                                    }
                                }
                            }
                        }
                        // if sender is found and there is at least one recipient
                        if (fromEmail != null && !toEmails.isEmpty()) {
                            graph.addVertex(fromEmail); // add sender
                            for (String toEmail : toEmails) {
                                graph.addVertex(toEmail); // add recipient(s)
                                graph.addEdge(fromEmail, toEmail); // edge between them
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Issue with reading file: \n" + e);
                    }
                } else if (file.isDirectory()) {
                    // assistance with recursive traversal from chat.openai.com
                    parseFiles(file.getAbsolutePath(), graph); // recursively traverse directories
                }
            }
        }
    } // end ParseFiles

    /**
     * extractEmail for determining if an email is valid using regex
     * @param input is the email to be checked
     * @return the email if its found or null if it's not
     * Runtime: O(n)
     * Space: O(1)
     */
    private static String extractEmail(String input) { // assistance with regex from chat.openai.com
        Matcher matcher = Pattern.compile("([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+)").matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
} // end parseFiles

