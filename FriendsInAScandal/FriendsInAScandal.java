import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author David Shubov
 * FriendsInAScandal.java
 */

public class FriendsInAScandal {

    static boolean printConnectors = false;
    static String connectorDir = "";
    static String inputEmail = "";
    public static class GraphNode {
        public final String name; // individual's name/email
        public final Set<FriendsInAScandal.GraphNode> neighbors; // graph neighbors or connections

        /**
         * GraphNode are nodes in the graph with a name (email) and neighbors represented as a set
         * @param name email
         */
        public GraphNode(String name) {
            this.name = name;
            this.neighbors = new HashSet<>();
        }

        public void addNeighbor(FriendsInAScandal.GraphNode neighbor) {
            neighbors.add(neighbor);
        }

        public Set<FriendsInAScandal.GraphNode> getNeighbors() {
            return neighbors;
        }

        @Override
        public String toString() {
            return name;
        }
    } // end GraphNode


    public static class InputHandler {

        /**
         * handler is the public function for handling user input
         * @param g graph
         * @param emailClusters disjoint set for email clusters
         * Runtime and Space dependent on helper function
         */

        public void handler(Graph g, DisjointSet emailClusters) {
            handlerHelper(g, emailClusters);
        }

        /**
         * handlerHelper is the private helper function
         * @param g graph
         * @param emailClusters disjoint set for email clusters
         * Runtime: O(n^2) reading user input and finding email cluster size
         * Space: O(1)
         */
        private static void handlerHelper(Graph g, DisjointSet emailClusters) {
            Scanner scan = new Scanner(System.in);
            // I added "quit" as a command because I think it's nice for users to have options :)
            // otherwise this method is a generic input handler
            while (!(inputEmail.equalsIgnoreCase("exit") || inputEmail.equalsIgnoreCase("quit"))) {
                System.out.print("Email address of the individual (or EXIT to quit): ");
                inputEmail = scan.nextLine();
                if (inputEmail.equalsIgnoreCase("exit") || inputEmail.equalsIgnoreCase("quit")) {
                    break;
                } else {
                    try {
                        System.out.println("* " + inputEmail + " has sent messages to " + g.adjMap.get(inputEmail).neighbors.size() + " other(s)");
                        System.out.println("* " + inputEmail + " has received messages from " + g.getReceivedEmails(inputEmail) + " other(s)");
                        int clusterSize = emailClusters.size(inputEmail);
                        System.out.println("* " + inputEmail + " is in a team with " + clusterSize + " individual(s)");
                    } catch (NullPointerException e) {
                        System.out.println("Email address (" + inputEmail + ") not found in the dataset.");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        // object types of each file
        Parser p = new Parser();
        Graph g = new Graph();
        ConnectorFinder cf = new ConnectorFinder();
        InputHandler h = new InputHandler();

        System.out.println("Please wait a moment for graph and clusters to be created...");

        try {
            if (args.length > 2 || args.length < 1) {
                throw new IllegalArgumentException("Incorrect number of arguments (max 2). Either only maildir path or an additional out-path for connectors.");
            }
            if (args.length == 2) {
                printConnectors = true;
                connectorDir = args[1];
            }
            p.parseFiles(args[0], g);
            cf.findConnectors(g, printConnectors, connectorDir);
        } catch (Exception e) {
            System.out.println("Issue with file inputs, maximum two arguments. Please try again: " + e);
            System.exit(0);
        }

        Set<String> emails = new HashSet<>(); // store all email vertices
        for (FriendsInAScandal.GraphNode node : g.adjMap.values()) {
            emails.add(node.name); // add each vertex to set of emails
        }

        DisjointSet ds = new DisjointSet(emails); // disjoint set for clusters

        for (FriendsInAScandal.GraphNode node : g.adjMap.values()) { // iterate through vertices
            String email = node.name; // get a vertex node's email
            for (FriendsInAScandal.GraphNode neighbor : node.getNeighbors()) { // iterate through its neighbors
                ds.union(email, neighbor.name); // create a union between the email and its neighbor
            }
        }

        h.handler(g, ds); // start the ui

    } // end main
} // end FriendsInAScandal
