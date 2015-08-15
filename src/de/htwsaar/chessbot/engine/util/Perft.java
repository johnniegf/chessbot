package de.htwsaar.chessbot.engine.util;

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
public class Perft {

    public static void main(final String[] args) {
        int argc = args.length;
        String fen;
        int depth;
        int numWorkers = DEF_WORKERS;
        switch (argc) {
            case 3:
                numWorkers = Integer.valueOf(args[2]);
            case 2:
                fen = args[0];
                depth = Integer.valueOf(args[1]);
                break;
                
            default:
                msg(USAGE_STRING);
                return;
        }
        Result r = run(fen,depth,numWorkers);
        msg(r.nodes+";"+r.time);
    }

    public static Result run(final String fenString,
                             final int searchDepth,
                             final int numWorkers)
    {
        try {
            Perft perft = new Perft(fenString, searchDepth, numWorkers);
            perft.run();
            while (!perft.isDone()) continue;
            return perft.result();
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return new Perft.Result(-1,-1);
        }
    }


    private static void debug(String str) {
        if (VERBOSE) {
            msg(str);
        }
    }

    private static void msg(String str) {
        System.out.println(str);
    }

    private static int min(final int a, final int b) {
        return (a < b ? a : b);
    }

    private static int max(final int a, final int b) {
        return (a > b ? a : b);
    }

    private static boolean VERBOSE = false;
    private static final String USAGE_STRING =
        "Usage: Perft <fen_string> <search_depth> [<num_worker_threads>]";
    
    private static final int MAX_WORKERS = 
            Runtime.getRuntime().availableProcessors();
    private static final int DEF_WORKERS = MAX_WORKERS - 1; 

// ==========================================================================

    private PerftWorker[] mWorkers;
    private int mNumWorkers;
    
    private Board mInitial;
    private int mDepth;
    
    private Result mResult;
    private boolean mDone;


    public Perft(final String fenString,
                 final int searchDepth)
    {
        
    }

    public Perft(final String fenString,
                 final int searchDepth,
                 final int numWorkers)
    {
        checkNull(fenString,"fenString");
        mInitial = B(fenString);
        mDepth = searchDepth;
        mNumWorkers = numWorkers;
        mResult = new Result(-1,-1);
        mDone = false;

        setUpWorkers();
    }

    public Perft.Result result() {
        return mResult;
    }

    public long nodes() {
        return mResult.nodes;
    }

    public long time() {
        return mResult.time;
    }

    public boolean isDone() {
        return mDone;
    }

    public void run() {
        long time = System.currentTimeMillis();
        long nodes = calculate();
        time = System.currentTimeMillis() - time;
        mResult = new Result(nodes,time);
        mDone = true;
    }

    /**
    *
    */
    private long calculate() {
        Collection<Move> moveList = mInitial.getMoveList();
        if (moveList.isEmpty())
            return 0;

        if (mDepth == 0) {
            return 1;
        } else if (mDepth == 1) {
            return moveList.size();
        }
        
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

    private void setUpWorkers() {
        Collection<Move> moveList = mInitial.getMoveList();
        if (moveList.isEmpty()) {
            mWorkers = new PerftWorker[0];
            return;
        }

        int numWorkers = mNumWorkers;
        numWorkers = max(1, numWorkers);
        numWorkers = min(numWorkers, MAX_WORKERS);
        if (moveList.size() < numWorkers) 
            numWorkers = moveList.size();

        Collection<Move>[] partialMoveLists
            = (Collection<Move>[]) new Collection[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            partialMoveLists[i] = new ArrayList<Move>();
        }

        int cnt = 0;
        for (Move m : moveList) {
            partialMoveLists[cnt % numWorkers].add(m);
            cnt += 1;
        }

        mWorkers = new PerftWorker[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            mWorkers[i] = new PerftWorker(mInitial, partialMoveLists[i], mDepth);
        }
    }

    public static final class Result {
        public final long nodes;
        public final long time;

        public Result(final long nodes, final long time) {
            this.nodes = nodes;
            this.time = time;
        }
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
