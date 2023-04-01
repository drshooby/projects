
public class LinkedList<T> implements List<T>{
    Node<T> head;
    int size;
    public LinkedList() {
        head = null;
        size = 0;
    }
    private static class Node<T> {

        T data;
        Node<T> next;
        Node(T data) {
            this.data = data;
            next = null;
        }
    } // end Node

    public class ListIterator implements Literator<T> {

        private Node<T> node = head;

        public boolean hasNext() {
            return node.next != null;
        }

        public T next() {
            Node<T> prev = node;
            node = node.next;
            return prev.data;
        }
    }

    public Literator<T> getIterator() {
        return new ListIterator();
    }
    public int size() {
        return size;
    } // O(1)

    public void add(T data) { // O(1) adds to head instead of end (reverse order)
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public T get(int pos) throws Exception{ // O(n)
        if (pos < 0 || pos >= size)
            throw new Exception(" Invalid position ");
        Node<T> curr = head;
        for (int i = 0; i < pos; i++) {
            curr = curr.next;
        }
        return curr.data;
    }

}
