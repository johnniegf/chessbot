/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.perft;

import de.htwsaar.chessbot.core.Board;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;

/**
 * Transpositionstabelle.
 * 
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public final class PerftHashTable {
    
    public static final int UNDEFINED = -1;
    private static final int DEFAULT_CAPACITY = 0x10_0000;
    private static final long MASK = ~Long.MIN_VALUE;

    private final Entry[] mEntries;
    private final int mDepth;
    
    public PerftHashTable(final int maxDepth) {
        checkInBounds(maxDepth, 1, DEPTH_HARD_LIMIT);
        mDepth = maxDepth;
        mEntries = new Entry[DEFAULT_CAPACITY];
    }
    
    public  long get(final long zobristHash, 
                     final int perftDepth)
    {
        checkInBounds(perftDepth, 1, mDepth);
        int index = makeIndex(zobristHash);
        int idx = d(perftDepth);
        Entry entry = mEntries[index];
        if ( entry == null
          || entry.zobristHash != zobristHash
          || entry.positions[idx] < 0)
        {
            return UNDEFINED;
        }
        
        return entry.positions[idx];
    }
    private static final int DEPTH_HARD_LIMIT = 100;
    
    public  void put(final Board b, 
                     final int perftDepth, 
                     final long positions)
    {
        checkInBounds(perftDepth, 1, mDepth);
        long hash = b.hash();
        int idx = d(perftDepth);
        int index = makeIndex(hash);
        Entry e = mEntries[index];
        if (e == null) {
            e = new Entry(hash, mDepth);
            e.positions[idx] = positions;
            mEntries[index] = e;
        }
        if (e.zobristHash != hash)
            return;
        if (e.positions[idx] < 0)
            e.positions[idx] = positions;
    }
    
    private int makeIndex(long zobristHash) {
        return (int) ((zobristHash >>> 1) % capacity());
    }
    
    public int capacity() {
        return mEntries.length;
    }
    
    public static final boolean isDefined(final int result) {
        return result != UNDEFINED;
    }
    
    private static int d(int perftDepth) {
        return perftDepth-1;
    }
    
    public static final class Entry {
        
        public long zobristHash;
        public long[] positions;
        
        public Entry(final long zobristHash,
                     final int depth)
        {
            this.zobristHash = zobristHash;
            this.positions = new long[depth];
            for (int i = 0; i < depth; i++) {
                positions[i] = UNDEFINED;
            }
        }
        
    }
}
