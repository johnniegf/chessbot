/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import static de.htwsaar.chessbot.engine.eval.EvaluationFunction.INFINITE;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class EvaluationHashTable {

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
        }
    }

    private int index(final long hash) {
        return (int) ((hash & ~Long.MIN_VALUE) % capacity());
    }

    public int size() {
        return mCount;
    }

    public int capacity() {
        return mEntries.length;
    }

    private static final int DEFAULT_CAPACITY = 1 << 15;

    private static class Entry {
        public long hash;
        public int score;

        public Entry(final long hash, final int score) {
            this.hash  = hash;
            this.score = score;
        }
    }
}
