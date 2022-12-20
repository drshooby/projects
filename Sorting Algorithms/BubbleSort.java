import java.util.Random;
import java.util.Arrays;

public class BubbleSort {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private void Sort(int[] L) { 
        int n = L.length;
        boolean swapped = true;
        for (int i = 0; i < n && swapped; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (L[j] > L[j+1]) {
                    int tmp = L[j];
                    L[j] = L[j+1];
                    L[j+1] = tmp;
                    swapped = true;
                }
            }
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
        BubbleSort toSort = new BubbleSort();
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