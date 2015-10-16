package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.core.moves.Move.NOMOVE;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
 * Transpositionstabelle.
 *
 * Die Transpositionstabelle speichert Art und Höhe der Bewertungen für 
 * Stellungen. Besonders im Endspiel können Suchalgorithmen dadurch an 
 * Geschwindigkeit gewinnen.
 * 
 * @author Johannes Haupt
 */
public final class TranspositionHashTable implements HashTable {


    public static final int FLAG_ALPHA = 0;
    public static final int FLAG_BETA  = 1;
    public static final int FLAG_PV    = 2;

    private static final int UNDEFINED = Integer.MIN_VALUE;
    private static final int DEFAULT_CAPACITY = 1024;

    private Entry[] mEntries;
    private int mSize;
    private int mTableHits;

    public TranspositionHashTable() {
        this(DEFAULT_CAPACITY);
    }

    public TranspositionHashTable(final int maxCapacity) {
        setCapacity(maxCapacity);
        mTableHits = 0;
    }
    
    public final int usage() {
        return (1000 * size()) / capacity();
    }
    
    public final int size() {
        return mSize;
    }

    public final int hits() {
        return mTableHits;
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
            if (entry.bestMove.type() == NOMOVE.type())
                return false;
            moveInfo.setMove(entry.bestMove);
            mTableHits++;
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

        // Falls kein Eintrag vergeben ist oder ein Eintrag mit verschiedenem
        // Hashwert existiert, der keine exakte Bewertung enthält,
        // wird ein neuer Eintrag angelegt
        if (entry == null
         || entry.flags != FLAG_PV && entry.zobristHash != zobristHash) {
            mSize += (entry == null ? 1 : 0);
            entry = new Entry(zobristHash, bestMove, depth, score, flags);
            mEntries[makeIndex(zobristHash)] = entry;
            return;
        }

        // Hash stimmt überein
        if (entry.depth <= depth)
        {
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
    
    @Override
    public void setCapacity(final int sizeInKB) {
        checkInBounds(sizeInKB, 0, Integer.MAX_VALUE);
        int capacity = sizeInKB * 1024 / Entry.SIZE_IN_BYTES;
        if (mEntries == null || capacity != capacity()) {
            mEntries = new Entry[capacity];
            mSize = 0;
        }
    }

    private int makeIndex(final long zobristHash) {
        return (int) ((zobristHash & Long.MAX_VALUE) % capacity());
    }

    public static final boolean isDefined(final int result) {
        return result != UNDEFINED;
    }

    private static final class Entry {
        
        public static final int SIZE_IN_BYTES = 40;

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
