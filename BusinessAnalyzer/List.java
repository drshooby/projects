import java.util.Iterator;

public interface List<T> {
     int size();

     void add(T element);

     T get(int pos) throws Exception;

     Literator<T> getIterator();
}
