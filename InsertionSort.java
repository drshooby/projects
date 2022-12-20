import java.util.Arrays;
import java.util.Random;

public class InsertionSort {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    
    private void Sort(int[] L) { 
        int n = L.length;
        for (int i = 1; i < n; i++) {
            int current = L[i];
            int j = i - 1;
            while (j >= 0 && L[j] > current) {
                L[j+1] = L[j];
                j--;
            }
            L[j+1] = current;
        }
    }

    private int[] CreateList() { 
        int[] L = new int[10000];
        Random randNum = new Random();
        for (int i = 0; i < L.length; i++) {
            L[i] = randNum.nextInt(100000);
        }
        return L;
    }

    private void check(int[] sorted) { 
        int i = 0;
        while (i != 9999) {
            if ((sorted[i] > sorted[i+1])) {
                System.out.println(ANSI_RED + "NOT SORTED" + ANSI_RESET);
                return;
            }
            i++;
        }
        System.out.println(ANSI_GREEN + "SORTED" + ANSI_RESET);
    }

    public static void main(String[] args) {
        InsertionSort toSort = new InsertionSort();
        System.out.println("\n Unsorted Array \n");
        int[] L = toSort.CreateList();
        System.out.println(Arrays.toString(L));
        System.out.println("\n Sorted Array \n");
        final long startTime = System.currentTimeMillis();
        toSort.Sort(L);
        final long endTime = System.currentTimeMillis();
        System.out.println(Arrays.toString(L));
        System.out.println("Total time in seconds: " + ((endTime - startTime) / 1000.0));
        toSort.check(L);
    }
}