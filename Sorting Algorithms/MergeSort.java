import java.util.Arrays;
import java.util.Random;

public class MergeSort {
    
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private void Sort(int[] L) {
        if (L.length > 1) {
            int[] left = LeftHalf(L);
            int[] right = RightHalf(L);
            Sort(left);
            Sort(right);
            merge(L, left, right);
        }
    }

    private int[] LeftHalf(int[] L) {
        int size1 = L.length / 2;
        int[] left = new int[size1];
        for (int i = 0; i < size1; i++) {
            left[i] = L[i];
        }
        return left;
    }

    private int[] RightHalf(int[] L) {
        int size1 = L.length / 2;
        int size2 = L.length - size1;
        int[] right = new int[size2];
        for (int i = 0; i < size2; i++) {
            right[i] = L[i+size1];
        }
        return right;
    }

    private void merge(int[] result, int[] left, int[] right) {
        int i1 = 0;
        int i2 = 0;
        for (int i = 0; i < result.length; i++) {
            if (i2 >= right.length || (i1 < left.length && left[i1] <= right[i2])) {
                result[i] = left[i1];
                i1++;
            } else {
                result[i] = right[i2];
                i2++;
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
    
    // Note: print statements add a significant amount of time 
    public static void main(String[] args) {
        MergeSort toSort = new MergeSort();
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
