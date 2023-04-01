
public class ArrayList<T> implements List<T>{

    int size;
    T[] arr;
    public ArrayList () {
        arr = (T[]) new Object[500]; // Will generate a warning
        size = 0;
    }

    public class ListIterator implements Literator<T> {

        private int nextIndex = 0;

        public boolean hasNext() {
            return nextIndex < size && nextIndex >= 0;
        }

        public T next() {
            return arr[nextIndex++];
        }
    }

    public Literator<T> getIterator() {
        return new ListIterator();
    }
    public int size () {
        return size;
    } // O(1)

    public T get (int pos) throws Exception { // O(1)
        if (pos < 0 || pos >= size)
            throw new Exception(" Invalid position ");
        return arr[pos];
    }

    public void add (T item) { // O(1)
        if (size == arr.length)
            grow_array();
        arr[size++] = item;
    }

    public void grow_array () {
        T [] new_arr = (T[]) new Object[arr.length * 2]; // Will generate a warning
        for (int i = 0; i < arr.length; i++)
            new_arr[i] = arr[i];
        arr = new_arr;
    }
}
