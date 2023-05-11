import java.util.*;

/**
 * @author David Shubov
 * DisjointSet.java
 */
class DisjointSet {
    Map<String, String> parent;
    Map<String, Integer> size;

    public DisjointSet(Set<String> emails) {
        this.parent = new HashMap<>();
        this.size = new HashMap<>();
        for (String email : emails) {
            parent.put(email, email);
            size.put(email, 1);
        }
    }

    /**
     * find is one of the main functions of a disjoint set
     * @param email name to find
     * @return the email
     * Runtime: O(n) however, the compression algorithm makes it almost O(1)
     * Space: O(1)
     */
    public String find(String email) {
        String p = parent.get(email);
        if (p.equals(email)) {
            return email;
        }
        p = find(p);
        parent.put(email, p);
        return p;
    }

    /**
     * union is the next main function of a disjoint set
     * @param email1 first email to create a union with
     * @param email2 second email to create a union with
     * Runtime: O(n) uses find()
     * Space: O(1)
     */
    public void union(String email1, String email2) {
        String p1 = find(email1);
        String p2 = find(email2);
        if (!p1.equals(p2)) {
            int s1 = size.get(p1);
            int s2 = size.get(p2);
            if (s1 < s2) {
                String temp = p1;
                p1 = p2;
                p2 = temp;
            }
            parent.put(p2, p1);
            size.put(p1, s1 + s2);
        }
    }

    /**
     * size for returning the size of a cluster in the disjoint set
     * @param email name to look for
     * @return the size of the cluster
     * Runtime: O(n) uses find()
     * Space: O(1)
     */
    public int size(String email) {
        String p = find(email);
        return size.get(p);
    }
}


