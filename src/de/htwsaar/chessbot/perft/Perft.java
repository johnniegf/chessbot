package de.htwsaar.chessbot.perft;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.core.Board.*;
import static de.htwsaar.chessbot.util.Exceptions.*;

import java.util.*;
/**
* Testwerkzeug für Performance- und Korrektheit des Zuggenerators.
*
* Perft zählt alle Züge, die der Zuggenerator für eine Ausgangstellung und 
* eine Suchtiefe erzeugt. Die Ergebnisse der Generierung lassen sich 
* @author Johannes Haupt
*/
public class Perft {

    public static Result run(final RunType runType,
                             final String fenString,
                             final int searchDepth,
                             final int numWorkers)
    {
        try {
            Perft perft = new Perft(runType, 
                                    fenString, 
                                    searchDepth, 
                                    numWorkers);
            perft.run();
            return perft.result();
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return null;
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
    
    protected static final int MAX_WORKERS = 
            Runtime.getRuntime().availableProcessors();
    protected static final int DEF_WORKERS = MAX_WORKERS - 1; 
    public static final int DEPTH_LIMIT = 100;

// ==========================================================================

    private Worker[] mWorkers;
    private final int mNumWorkers;
//    private volatile PerftHashTable mHashTable;
    private PerftHashTable mHashTable;
    
    private final Board mInitial;
    private final int mDepth;
    
    private Result mResult;
    private final RunType mRunType;
    private boolean mDone;

    public Perft(final RunType runType,
                 final String fenString,
                 final int searchDepth,
                 final int numWorkers)
    {
        checkNull(fenString,"fenString");
        mRunType = runType;
        mInitial = B(fenString);
        mDepth = searchDepth;
        mNumWorkers = numWorkers;
        mResult = null;
        mHashTable = new PerftHashTable(mDepth);
        setUpWorkers();
    }
    
    public synchronized PerftHashTable getHashTable() {
        return mHashTable;
    }

    public Perft.Result result() {
        return mResult;
    }
    
    public void run() {
        long time = System.currentTimeMillis();
        mResult = calculate(); 
        if (mResult == null)
            return;
        time = System.currentTimeMillis() - time;
        mResult.setTime(time);
        mDone = true;
    }

    /**
    *
    */
    private Result calculate() {
        Collection<Move> moveList = Arrays.asList(mInitial.getMoveList());
        if (moveList.isEmpty())
            return null;

        if (mDepth == 0) 
            return null;
        
        for (Perft.Worker t : mWorkers)
            t.start();
        
        Perft.Result result = null;
        for (Perft.Worker t : mWorkers) { 
            try {
                t.join();
                if (result == null)
                    result = t.result();
                else
                    result = result.join(t.result());
            } catch (InterruptedException ire) {
                System.err.println(ire);
            }
        }
        return result; 
    }

    private void setUpWorkers() {
        List<Move> moveList = Arrays.asList(mInitial.getMoveList());
        Collections.shuffle( moveList );
        if (moveList.isEmpty()) {
            mWorkers = new Perft.Worker[0];
            return;
        }

        int numWorkers = mNumWorkers;
        numWorkers = max(1, numWorkers);
        //numWorkers = min(numWorkers, MAX_WORKERS);
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

        mWorkers = new Perft.Worker[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            mWorkers[i] = createThread(mRunType, mInitial, partialMoveLists[i], mDepth);
        }
    }

    private Perft.Worker createThread(RunType runType,
                                      final Board initial,
                                      final Collection<Move> moveList,
                                      final int depth) 
    {
        switch(runType) {
            case DIVIDE:
                return new DivideWorker(initial, moveList, depth);

            case PERFT:
                return new PerftWorker(initial, mHashTable, moveList, depth);
            
            default:
                return null;
        }
    }

    public static interface Result {
        Result join(final Result other);

        void setTime(final long timeInMSec);

        String toString();
    }

    public static abstract class Worker extends Thread {
        
        protected Worker(final String name) {
            super(name);
        }
        
        public abstract Perft.Result result();
    }

    public static enum RunType {
        PERFT,
        DIVIDE
    }

}
