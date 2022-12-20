import java.util.Arrays;
import java.util.Random;

public class QuickSort {
    
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private void Sort(int[] L, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(L, low, high);
            Sort(L, low, pivotIndex - 1);
            Sort(L, pivotIndex + 1, high);
        }
    }

    private int partition(int[] L, int low, int high) {
        int pivot = L[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (L[j] <= pivot) {
                i++;
                swap(L, i, j);
            }
        }
        swap(L, i + 1, high);
        return i + 1;
    }

    public void swap(int[] L, int i, int j) {
        int tmp = L[i];
        L[i] = L[j];
        L[j] = tmp;
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
                System.out.println(sorted[i] + " " + sorted[i+1]);
                System.out.println(ANSI_RED + "NOT SORTED" + ANSI_RESET);
                return;
            }
            i++;
        }
        System.out.println(ANSI_GREEN + "SORTED" + ANSI_RESET);
    }

    // Note: print statements add a significant amount of time 
    public static void main(String[] args) {
        QuickSort toSort = new QuickSort();
        System.out.println("\n Unsorted Array \n");
        int[] L = toSort.CreateList();
        System.out.println(Arrays.toString(L));
        System.out.println("\n Sorted Array \n");
        final long startTime = System.currentTimeMillis();
        toSort.Sort(L, 0, L.length - 1);
        final long endTime = System.currentTimeMillis();
        System.out.println(Arrays.toString(L));
        System.out.println("Total time in seconds: " + ((endTime - startTime) / 1000.0));
        toSort.check(L);
    }
}
