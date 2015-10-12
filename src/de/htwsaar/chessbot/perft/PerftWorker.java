package de.htwsaar.chessbot.perft;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.core.Board.*;
import static de.htwsaar.chessbot.util.Exceptions.*;
import static de.htwsaar.chessbot.perft.PerftHashTable.UNDEFINED;

import java.util.*;
/**
* Testwerkzeug für Performance- und Korrektheit des Zuggenerators.
*
* Perft zählt alle Züge, die der Zuggenerator für eine Ausgangstellung und 
* eine Suchtiefe erzeugt. Die Ergebnisse der Generierung lassen sich 
* @author Johannes Haupt
*/

public class PerftWorker 
     extends Perft.Worker 
{
    private static int ID = 1;
    
    private final int mDepth;
    private final Board mInitial;
    private final Collection<Move> mMoves;
    private boolean done = false;
    private Result mResult = null;
    private volatile Perft mParent;
    private PerftHashTable mHashTable;

    public PerftWorker(final Board initial, 
                       final PerftHashTable hashTable,
                       final Collection<Move> movesToSearch, 
                       final int depth) {
        super("PerftWorker#" + ID);
        checkNull(initial, "Initial Position");
        checkCondition(initial.isValid(), "Initial Position is invalid");
        checkNull(hashTable, "Transposition Table");
        checkNull(movesToSearch, "Move list");
        checkInBounds(depth, 0, Perft.DEPTH_LIMIT);
        mDepth = depth;
        mMoves = movesToSearch;
        mInitial = initial;
        mHashTable = hashTable;
//        mHashTable = new PerftHashTable(mDepth);
        ID += 1;
    }

    public boolean isDone() {
        return done;
    }

    public void run() {
        if (!done) {
            long result = 0L;
            for (Move m : mMoves) {
                result += calculate(m.execute(mInitial), mDepth-1);
            }
            mResult = new Result(result, -1);
            done = true;
        }
    }

    private long calculate(final Board board, final int depth) {
        long result = 0L;
        long posPerMove = 0L;
        long entry = 0L;
        if ( depth == 0 ) {
            return 1;
        } else if ( depth == 1 ) {
            int moveCount = board.getResultingPositions().length;
                mHashTable.put(board, depth, moveCount);
            return moveCount;
        }
        
        for ( Board b : board.getResultingPositions() ) {
            entry = mHashTable.get(b.hash(), depth);
            if (entry == UNDEFINED) {
                posPerMove = calculate(b, depth-1);
                mHashTable.put(b, depth, posPerMove);
                result += posPerMove;
            } else {
                result += entry;
            }
        }
        return result;
    }
    
    public Result result() {
        return mResult;
    }

    public static class Result implements Perft.Result {
        
        public final long nodes;
        public long time;

        public Result(final long nodes, final long time) {
            this.nodes = nodes;
            this.time  = time;
        }


        public String toString() {
            return nodes+";"+time;
        }

        public void setTime(final long time) {
            this.time = time;
        }

        public Result join(Perft.Result other) {
            if (other instanceof Result) {
                Result r = (Result) other;
                return new Result(nodes + r.nodes, time + r.time);
            } else {
                return new Result(-1,-1);
            }
        }
    }

}
