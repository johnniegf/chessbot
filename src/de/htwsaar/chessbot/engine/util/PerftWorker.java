package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.engine.model.Board.*;
import static de.htwsaar.chessbot.util.Exceptions.*;
import de.htwsaar.chessbot.engine.model.*;

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

    private int mDepth;
    private Board mInitial;
    private Collection<Move> mMoves;
    private boolean done = false;
    private Result mResult = null;


    public PerftWorker(final Board initial, 
                       final Collection<Move> movesToSearch, 
                       int depth) {
        mDepth = depth;
        mMoves = movesToSearch;
        mInitial = initial;
    }

    public boolean isDone() {
        return done;
    }

    public void run() {
        if (!done) {
            long result = 0;
            Board b;
            for (Move m : mMoves) {
                b = m.execute(mInitial);
                result += calculate(b, mDepth-1);
            }
            mResult = new Result(result, -1);
            done = true;
        }
    }

    private long calculate(final Board board, final int depth) {
        long result = 0;
        if ( depth > 0 ) {
            Board b;
            for ( Move m : board.getMoveList() ) {
                b = m.execute(board);
                result += calculate(b, depth-1);
            }
        } else return 1;
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
