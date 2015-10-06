/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;

/**
 * Transpositionstabelle.
 * 
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public final class HashTable {
    
    public static final int FLAG_ALPHA = 0;
    public static final int FLAG_BETA  = 1;
    public static final int FLAG_PV    = 2;
    
    private static final int UNDEFINED = Integer.MIN_VALUE;
    private static final int DEFAULT_CAPACITY = 1 << 15;

    private final Entry[] mEntries;
    
    public HashTable() {
        this(DEFAULT_CAPACITY);
    }
    
    public HashTable(final int maxCapacity) {
        checkInBounds(maxCapacity, 0, Integer.MAX_VALUE);
        mEntries = new Entry[maxCapacity];
    }
    
    public int get(final long zobristHash,
                   final int depth,
                   final int alpha,
                   final int beta) 
    {
        Entry entry = mEntries[(int) (zobristHash % capacity())];
        if (entry == null)
            return UNDEFINED;
        
        if (entry.zobristHash == zobristHash) {
            if (entry.depth >= depth) {
                if (entry.flags == FLAG_PV)
                    return entry.score;
                if ((entry.flags == FLAG_ALPHA) &&
                    (entry.score <= alpha))
                    return alpha;
                if ((entry.flags == FLAG_BETA) &&
                    (entry.score >= beta))
                    return beta;
            }
            //RememberBestMove();
        }
        return UNDEFINED;
    }
    
    public void put(final Board b, 
                    final int depth, 
                    final int score, 
                    final int flags)
    {
        
    }
    
    public int capacity() {
        return mEntries.length;
    }
    
    public static final boolean isDefined(final int result) {
        return result != UNDEFINED;
    }
    
    public static final class Entry {
        
        public long zobristHash;
        public Move bestMove;
        public int depth;
        public int score;
        public int flags;
        
        public Entry(final long zobristHash,
                     final int depth,
                     final int score,
                     final int flags) 
        {
            this.zobristHash = zobristHash;
            this.depth = depth;
            this.score = score;
            this.flags = flags;
            this.bestMove = null;
            
            
        }
        
    }
}
