import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author David Shubov
 * ConnectorFinder.java
 */
public class ConnectorFinder { // help with most of ConnectorFinder from chat.openai.com and Professor's guidance

    /**
     * findConnectors find connector nodes in the graph
     * @param graph the graph
     * @param writeFile whether to write to an outfile or not
     * @param outPath the outfile path to write to
     * @return a set containing each connector
     */

    public Set<String> findConnectors(Graph graph, boolean writeFile, String outPath) {
        Set<String> connectors = new HashSet<>();
        Map<String, Integer> dfsNum = new HashMap<>();
        Map<String, Integer> back = new HashMap<>();
        Set<String> visited = new HashSet<>();
        String startVertex = graph.adjMap.keySet().iterator().next();
        int count = 0;
        DFS(graph, startVertex, startVertex, dfsNum, back, visited, connectors, count);
        if (writeFile) {
            try {
                FileWriter fw = new FileWriter(outPath);
                for (String connector : connectors) {
                    fw.write(connector + "\n");
                }
                fw.close();
            } catch (IOException e) {
                System.out.println("Error writing to file path " + outPath + "." + e);
            }
        }
        return connectors;
    }

    /**
     * DFS helper function performs a custom depth-first traversal to locate connectors in the graph
     * @param graph the graph
     * @param vertex current vertex
     * @param parent vertex that was visited just before visiting the current vertex
     * @param dfsNum keeps track of DFS number for each vertex
     * @param back the smallest DFS number of any vertex that can be reached from a given vertex
     * @param visited visited vertices
     * @param connectors the connectors
     * @param count assigns DFS numbers to vertices during traversal
     */

    // assistance with custom DFS from chat.openai.com
    private static void DFS(Graph graph, String vertex, String parent, Map<String, Integer> dfsNum, Map<String, Integer> back, Set<String> visited, Set<String> connectors, int count) {
        visited.add(vertex);
        dfsNum.put(vertex, count);
        back.put(vertex, count);
        count++;

        int children = 0;
        for (FriendsInAScandal.GraphNode neighbor : graph.adjMap.get(vertex).getNeighbors()) {
            String neighborName = neighbor.name;
            if (!visited.contains(neighborName)) {
                children++;
                DFS(graph, neighborName, vertex, dfsNum, back, visited, connectors, count);
                back.put(vertex, Math.min(back.get(vertex), back.get(neighborName)));
                if ((parent != null && dfsNum.get(vertex) <= back.get(neighborName)) || (parent == null && children > 1)) {
                    connectors.add(vertex);
                }
            } else if (!neighborName.equals(parent)) {
                back.put(vertex, Math.min(back.get(vertex), dfsNum.get(neighborName)));
            }
        }
    }
} // end ConnectorFinder
