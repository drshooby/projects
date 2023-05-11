import java.util.*;

/**
 * @author David Shubov
 * Graph.java
 */
public class Graph {
    public final Map<String, FriendsInAScandal.GraphNode> adjMap;

    public Graph() {
        this.adjMap = new HashMap<>();
    }

    /**
     * addVertex adds a vertex to the graph
     * @param name email name
     * Runtime: O(1)
     * Space: O(1)
     */
    public void addVertex(String name) {
        adjMap.putIfAbsent(name, new FriendsInAScandal.GraphNode(name));
    }

    /**
     * addEdge creates an edge between two email vertices
     * @param name1 first email name
     * @param name2 second email name
     * Runtime: O(1)
     * Space: O(1)
     */
    public void addEdge(String name1, String name2) {
        FriendsInAScandal.GraphNode node1 = adjMap.get(name1);
        FriendsInAScandal.GraphNode node2 = adjMap.get(name2);
        if (node1 == null || node2 == null) {
            throw new IllegalArgumentException("One or both nodes not found in graph");
        }
        node1.addNeighbor(node2);
        node2.addNeighbor(node1);
    }

    /**
     * getReceivedEmails checks an email address to see how many people have sent them emails
     * @param email the email being searched for
     * @return the amount found
     * Runtime: O(n^2)
     * Space: O(1)
     */
    public int getReceivedEmails(String email) { // its slow, I know, but it works unlike my initial hashmap method
        int received = 0;
        // some time should be saved with iterators and data structures used (maps/sets)
        for (FriendsInAScandal.GraphNode V : this.adjMap.values()) {
            for (FriendsInAScandal.GraphNode node : V.neighbors) {
                if (node.name.equalsIgnoreCase(email)) {
                    received++;
                }
            }
        }
        return received;
    }
} // end Graph
