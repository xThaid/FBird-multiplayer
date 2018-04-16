package fbird.server;

public interface Event<T> {
    public void handle(T obj);
}
