import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * Your implementation of HashMap.
 * 
 * @author YOUR NAME HERE
 * @version 1.3
 */
public class HashMap<K, V> implements HashMapInterface<K, V> {

    // Do not make any new instance variables.
    private MapEntry<K, V>[] table;
    private int size;

    /**
     * Create a hash map with no entries. The backing array has an initial
     * capacity of {@code STARTING_SIZE}.
     *
     * Do not use magic numbers!
     *
     * Use constructor chaining.
     */
    public HashMap() {
        this(STARTING_SIZE);
    }

    /**
     * Create a hash map with no entries. The backing array has an initial
     * capacity of {@code initialCapacity}.
     *
     * You may assume {@code initialCapacity} will always be positive.
     *
     * @param initialCapacity initial capacity of the backing array
     */
    public HashMap(int initialCapacity) {
        this.table = (MapEntry<K, V>[]) new MapEntry[initialCapacity];
        this.size = 0;
    }

    @Override
    public V add(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Cannot add null entries to"
                    + " the hashmap");
        }
        MapEntry<K, V> map = new MapEntry<K, V>(key, value);
        //find proper array index
        int hash = key.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        hash %= table.length;
        //regrow
        if ((size + 1) > (MAX_LOAD_FACTOR * table.length)) {
            System.out.println("Resize");
            MapEntry<K, V>[] array = (MapEntry<K, V>[])
                    new MapEntry[table.length * 2 + 1];
            for (int i = 0; i < table.length; i++) {
                if (table[i] != null && !table[i].isRemoved()) {
                    int newHash = table[i].hashCode();
                    if (newHash < 0) {
                        newHash *= -1;
                    }
                    newHash %= array.length;
                    array[newHash] = table[i];
                }
            }
            table = array;
        }
        //System.out.println("Key: " + table[hash].getKey());
        if (table[hash] != null && table[hash].getKey().equals(key)) {
            V oldValue = table[hash].getValue();
            table[hash] = map;
            return oldValue;
        } else {
            for (int i = hash; i < table.length + hash - 1; i++) {
                if (table[i % table.length] != null
                        && table[i % table.length].getKey().equals(key)) {
                    V oldValue = table[i % table.length].getValue();
                    table[i % table.length] = map;
                    return oldValue;
                }
            }
        }
        if (table[hash] == null || table[hash].isRemoved()) {
            table[hash] = map;
            size++;
            return null;
        } else {
            for (int i = hash; i < table.length + hash; i++) {
                if (table[i % table.length] == null
                        || table[i % table.length].isRemoved()) {
                    table[i % table.length] = map;
                    size++;
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot add null data to the"
                    + " hashmap.");
        }
        int hash = key.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        hash %= table.length;
        if (table[hash] != null) {
            if (table[hash].getKey().equals(key)) {
                table[hash].setRemoved(true);
                size--;
                return table[hash].getValue();
            } else {
                throw new NoSuchElementException("The key does not exist in"
                        + " the hashmap and cannot be removed");
            }
        } else {
            throw new NoSuchElementException("The key does not exist in the"
                    + " hashmap and cannot be removed");
        }
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot add null data "
                    + "to the hashmap.");
        }
        int hash = key.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        hash %= table.length;
        if (table[hash] != null) {
            if (table[hash].getKey().equals(key)) {
                return table[hash].getValue();
            } else {
                throw new NoSuchElementException("The key does not exist in"
                        + " the hashmap and cannot be removed");
            }
        } else {
            throw new NoSuchElementException("The key does not exist in the"
                    + " hashmap and cannot be removed");
        }
    }

    @Override
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot add null data to"
                    + " the hashmap.");
        }
        int hash = key.hashCode();
        if (hash < 0) {
            hash *= -1;
        }
        hash %= table.length;
        if (table[hash] != null) {
            return table[hash].getKey().equals(key);
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        table = (MapEntry<K, V>[]) new MapEntry[STARTING_SIZE];
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> hashset = new HashSet<K>(size);
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && !table[i].isRemoved()) {
                hashset.add(table[i].getKey());
            }
        }
        return hashset;
    }

    @Override
    public List<V> values() {
        ArrayList<V> arraylist = new ArrayList<V>(size);
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && !table[i].isRemoved()) {
                arraylist.add(table[i].getValue());
            }
        }
        return arraylist;
    }

    @Override
    public void resizeBackingTable(int length) {
        if (length <= 0 || length < size) {
            throw new IllegalArgumentException("The length must be positive"
                    + " and greater than the size of the hashmap.");
        }
        MapEntry<K, V>[] array = (MapEntry<K, V>[])
                new MapEntry[table.length * 2 + 1];
        if (size <= (MAX_LOAD_FACTOR * length)) {
            for (int i = 0; i < table.length; i++) {
                if (table[i] != null && !table[i].isRemoved()) {
                    int newHash = table[i].hashCode();
                    if (newHash < 0) {
                        newHash *= -1;
                    }
                    newHash %= array.length;
                    array[newHash] = table[i];
                }
            }
            table = array;
        }
    }
    
    @Override
    public MapEntry<K, V>[] getTable() {
        // DO NOT EDIT THIS METHOD!
        return table;
    }

}
