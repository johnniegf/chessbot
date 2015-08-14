package de.htwsaar.chessbot.engine.util;

import static de.htwsaar.chessbot.engine.model.Board.*;
import de.htwsaar.chessbot.engine.model.*;

import java.util.*;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Perft {

    private static boolean verbose = false;

    private static void debug(String str) {
        if (verbose) {
            msg(str);
        }
    }

    private static void msg(String str) {
        System.out.println(str);
    }

    private static int DEF_WORKERS = Runtime.getRuntime().availableProcessors() - 1;
    private static int NUM_WORKERS;

    private static int max(int... vals) {
        if (vals == null || vals.length < 1)
            return -1;
        int min = vals[0];
        for (int i = 1; i < vals.length; i++) {
            min = (vals[i] > min ? vals[i] : min);
        }
        return min;
    }

    public static void main(final String[] args) {
        int argc = args.length;
        if (argc < 2 || argc > 3)
            return;
        String fen = args[0];
        int depth = Integer.valueOf(args[1]);
        int workers = (args.length == 3 ? Integer.valueOf(args[2]) : DEF_WORKERS);
        NUM_WORKERS = max(1, workers);
        Perft pf = new Perft();
        long time = System.currentTimeMillis();
        long result = pf.calculate(fen,depth);
        time = System.currentTimeMillis() - time;
        debug("Tiefe " + depth + ": " + result 
            + " Knoten in " + (time / 1000.0) 
            + "s gefunden. (" + (result / (time / 1000.0)) 
            + " Knoten/s)");
        debug("Cache-Status: " + Pieces.getInstance().size() 
            + " Figuren, " + Move.CACHE_SIZE() + " Züge");
        debug("Ergebnis für Ausgangsstellung " + fen);
        msg(result+"");
    }

    private PerftWorker[] mWorkers = new PerftWorker[NUM_WORKERS];

    /**
    * Standardkonstruktor.
    */ 
    public long calculate(final String fenString, final int depth) {
        if (fenString == null)
            throw new NullPointerException("fenString");
        final Board initial = B(fenString);
        if (depth == 0) {
            return 1;
        } else if (depth == 1) {
            return initial.getMoveList().size();
        }
        setUpWorkers(initial, depth);
        for (PerftWorker t : mWorkers)
            t.start();
        long result = 0;
        for (PerftWorker t : mWorkers) { 
            try {
                t.join();
                result += t.result();
            } catch (InterruptedException ire) {
                System.err.println(ire);
            }
        }
        return result; 
    }

    private void setUpWorkers(final Board initial, int depth) {
        Collection<Move> moveList = initial.getMoveList();
        Collection<Move>[] partialMoveLists = (Collection<Move>[]) new Collection[NUM_WORKERS];
        for (int i = 0; i < NUM_WORKERS; i++) {
            partialMoveLists[i] = new ArrayList<Move>();
        }
        int cnt = 0;
        for (Move m : moveList) {
            partialMoveLists[cnt % NUM_WORKERS].add(m);
            cnt += 1;
        }
        for (int i = 0; i < NUM_WORKERS; i++) {
            mWorkers[i] = new PerftWorker(initial, partialMoveLists[i], depth);
        }

    }

    private long calculateR(final Board board, final int depth) {
        if (board == null) 
            throw new NullPointerException("board");
        long result = 0;
        if ( depth > 0 ) {
            Board b;
            //System.out.println(board);
            for ( Move m : board.getMoveList() ) {
                //System.out.println(depth + " " + m.getClass().getName() + "(" + board.getPieceAt(m.getStart()).fenShort() +  m + ")");
                if ( !m.isPossible(board) )
                    throw new MoveException("Zug " + m + " ist nicht möglich in " + board);
                b = m.execute(board);
                result += calculateR(b, depth-1);
            }
        } else return 1;
        return result;
    }

}

class PerftWorker extends Thread {

    private int mDepth;
    private Board mInitial;
    private Collection<Move> mMoves;
    private boolean done = false;
    private long mResult = 0;


    public PerftWorker(final Board initial, final Collection<Move> movesToSearch, int depth) {
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
                result += calculateR(b, mDepth-1);
            }
            mResult = result;
            done = true;
        }
    }

    private long calculateR(final Board board, final int depth) {
        if (board == null) 
            throw new NullPointerException("board");
        long result = 0;
        if ( depth > 0 ) {
            Board b;
            //System.out.println(board);
            for ( Move m : board.getMoveList() ) {
                //System.out.println(depth + " " + m.getClass().getName() + "(" + board.getPieceAt(m.getStart()).fenShort() +  m + ")");
                if ( !m.isPossible(board) )
                    throw new MoveException("Zug " + m + " ist nicht möglich in " + board);
                b = m.execute(board);
                result += calculateR(b, depth-1);
            }
        } else return 1;
        return result;
    }
    public long result() {
        return mResult;
    }

}
