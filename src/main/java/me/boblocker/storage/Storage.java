package me.boblocker.storage;

public interface Storage<K, V> {
    void save(V v);

    V findById(K k);

    void update(V v);

    void delete(K k);
}
