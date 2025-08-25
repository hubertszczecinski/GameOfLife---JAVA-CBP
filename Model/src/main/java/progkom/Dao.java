package progkom;

public interface Dao<T> extends AutoCloseable {

    T read() throws BaseApplicationException;

    void write(T obj) throws BaseApplicationException;
}
