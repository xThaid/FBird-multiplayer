package fbird.client;

public interface Event<T> {
    public void handle(T obj);
}
