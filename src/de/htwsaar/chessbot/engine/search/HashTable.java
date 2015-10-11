/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
 * Transpositionstabelle.
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public final class HashTable {

    private int HITS = 0;
    private int AGE  = 0;

    public static final int FLAG_ALPHA = 0;
    public static final int FLAG_BETA  = 1;
    public static final int FLAG_PV    = 2;

    private static final int UNDEFINED = Integer.MIN_VALUE;
    private static final int DEFAULT_CAPACITY = 1 << 16;

    private final Entry[] mEntries;
    private int mSize;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(final int maxCapacity) {
        checkInBounds(maxCapacity, 0, Integer.MAX_VALUE);
        mEntries = new Entry[maxCapacity];
        mSize = 0;
    }
    
    public final int usage() {
        return (1000 * size()) / capacity();
    }

    public final int size() {
        return mSize;
    }

    private int ageThreshold() {
        return AGE - 100;
    }

    public final int hits() {
        return HITS;
    }

    public boolean get(final Board board,
                       final int depth,
                       final int alpha,
                       final int beta,
                       final MoveInfo moveInfo)
    {
        checkNull(moveInfo);
        long zobristHash = board.hash();
        Entry entry = mEntries[makeIndex(zobristHash)];
        if (entry == null)
            return false;

        if (entry.zobristHash == zobristHash) {
            moveInfo.setMove(entry.bestMove);
            HITS++;
            if (entry.depth >= depth) {
                moveInfo.setScore(entry.score);

                switch (entry.flags) {
                    case FLAG_PV:
                        return true;

                    case FLAG_ALPHA:
                        if (moveInfo.score() <= alpha) {
                            moveInfo.setScore(alpha);
                            return true;
                        }
                        break;

                    case FLAG_BETA:
                        if (moveInfo.score() >= beta) {
                            moveInfo.setScore(beta);
                            return true;
                        }
                        break;

                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
        return false;
    }

    public void put(final Board board,
                    final Move bestMove,
                    final int depth,
                    final int score,
                    final int flags)
    {
        long zobristHash = board.hash();
        Entry entry = mEntries[makeIndex(zobristHash)];

        // Falls kein Eintrag vergeben oder ein Eintrag mit verschiedenem
        // Hashwert existiert, wird ein neuer Eintrag angelegt
        if (entry == null
         || entry.flags != FLAG_PV && entry.zobristHash != zobristHash) {
            mSize += (entry == null ? 1 : 0);
            entry = new Entry(zobristHash, bestMove, depth, score, flags);
            mEntries[makeIndex(zobristHash)] = entry;
            return;
        }


        // Hash stimmt überein
//        if (entry.depth <= depth)
        if (entry.depth <= depth)
        {
//            entry.zobristHash = zobristHash;
            entry.bestMove = bestMove;
            entry.depth = depth;
            entry.score = score;
            entry.flags = flags;
        }
    }

    public void clear() {
        for (int i = 0; i < capacity(); i++) {
            mEntries[i] = null;
        }
    }

    public int capacity() {
        return mEntries.length;
    }

    private int makeIndex(final long zobristHash) {
        return (int) ((zobristHash >>> 1) % capacity());
    }

    public static final boolean isDefined(final int result) {
        return result != UNDEFINED;
    }

    private static final class Entry {

        public long zobristHash;
        public Move bestMove;
        public int depth;
        public int score;
        public int flags;

        public Entry(final long hash,
                     final Move bestMove,
                     final int depth,
                     final int score,
                     final int flags)
        {
            this.zobristHash = hash;
            this.bestMove = bestMove;
            this.depth = depth;
            this.score = score;
            this.flags = flags;
        }
    }

    public static final class MoveInfo {
        private Move mMove;
        private int mScore;

        public MoveInfo() {
            mMove = null;
            mScore = UNDEFINED;
        }

        public boolean isNull() {
            return mMove == null || mScore == UNDEFINED;
        }

        public Move move() {
            return mMove;
        }

        public void setMove(final Move move) {
            mMove = move;
        }

        public int score() {
            return mScore;
        }

        public void setScore(final int score) {
            mScore = score;
        }
        }
}
