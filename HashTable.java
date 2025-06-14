package com.srkr.project;

import java.util.*;

public class HashTable<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private List<Entry<K, V>>[] buckets;
    private int size;

    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int capacity) {
        buckets = new List[capacity];
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode() % buckets.length);
    }

    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (List<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    keys.add(entry.key);
                }
            }
        }
        return keys;
    }

    public boolean containsKey(K key) {
        int bucketIndex = hash(key);
        List<Entry<K, V>> bucket = buckets[bucketIndex];
        if (bucket != null) {
            for (Entry<K, V> entry : bucket) {
                if (entry.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void put(K key, V value) {
        int bucketIndex = hash(key);
        List<Entry<K, V>> bucket = buckets[bucketIndex];
        if (bucket == null) {
            bucket = new LinkedList<>();
            buckets[bucketIndex] = bucket;
        }

        // Check if the key already exists in the bucket, update the value if found
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        // Key not found in the bucket, add a new entry
        bucket.add(new Entry<>(key, value));
        size++;

        // Check if resizing is needed
        if ((double) size / buckets.length >= LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        int bucketIndex = hash(key);
        List<Entry<K, V>> bucket = buckets[bucketIndex];
        if (bucket != null) {
            for (Entry<K, V> entry : bucket) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
        }
        return null;
    }

    public void remove(K key) {
        int bucketIndex = hash(key);
        List<Entry<K, V>> bucket = buckets[bucketIndex];
        if (bucket != null) {
            Iterator<Entry<K, V>> iterator = bucket.iterator();
            while (iterator.hasNext()) {
                Entry<K, V> entry = iterator.next();
                if (entry.key.equals(key)) {
                    iterator.remove();
                    size--;
                    return;
                }
            }
        }
    }

    private void resize() {
        int newCapacity = buckets.length * 2;
        List<Entry<K, V>>[] newBuckets = new List[newCapacity];

        for (List<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    int newBucketIndex = Math.abs(entry.key.hashCode() % newCapacity);
                    if (newBuckets[newBucketIndex] == null) {
                        newBuckets[newBucketIndex] = new LinkedList<>();
                    }
                    newBuckets[newBucketIndex].add(entry);
                }
            }
        }

        buckets = newBuckets;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}