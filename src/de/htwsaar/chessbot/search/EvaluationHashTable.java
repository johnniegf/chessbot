package de.htwsaar.chessbot.search;

import static de.htwsaar.chessbot.search.eval.EvaluationFunction.INFINITE;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;

/**
 * Hashtabelle für Bewertungen.
 * 
 * Speichert einmal berechnete Bewertungen für Stellungen. Dabei werden bei
 * Kollisionen alte Einträge überschrieben.
 * 
 * @author Johannes Haupt
 */
public class EvaluationHashTable implements HashTable {

    public static final int UNDEFINED = -INFINITE;

    private Entry[] mEntries;
    private int mCount;

    public EvaluationHashTable() {
        this(DEFAULT_CAPACITY);
    }

    public EvaluationHashTable(final int capacity) {
        checkInBounds(capacity, 1, Integer.MAX_VALUE);
        mEntries = new Entry[capacity];
    }

    public int get(final long hash) {
        Entry e = mEntries[index(hash)];
        if (e == null || e.hash != hash)
            return UNDEFINED;
        return e.score;
    }

    public void put(final long hash, final int score) {
        Entry e = mEntries[index(hash)];
        if (e == null || e.hash != hash) {
            e = new Entry(hash, score);
            mEntries[index(hash)] = e;
        } else {
            // Sollte nie passieren.
            e.score = score;
        }
    }

    private int index(final long hash) {
        return (int) ((hash & Long.MAX_VALUE) % capacity());
    }

    @Override
    public int size() {
        return mCount;
    }

    @Override
    public int capacity() {
        return mEntries.length;
    }
    
    @Override
    public void setCapacity(final int sizeInKB) {
        checkInBounds(sizeInKB, 0, Integer.MAX_VALUE);
        int capacity = sizeInKB / Entry.SIZE_IN_KB;
        if (mEntries == null || capacity != capacity()) {
            mEntries = new Entry[capacity];
            mCount = 0;
        }
    }
    
    @Override
    public void clear() {
        mEntries = new Entry[capacity()];
        mCount = 0;
    }

    private static final int DEFAULT_CAPACITY = 1024 * 1024;

    private static class Entry {
        
        public static final int SIZE_IN_KB = 24;
        public long hash;
        public int score;

        public Entry(final long hash, final int score) {
            this.hash  = hash;
            this.score = score;
        }
    }
}
