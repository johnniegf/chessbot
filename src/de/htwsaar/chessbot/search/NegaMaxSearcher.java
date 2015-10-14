package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.search.eval.EvaluationFunction;
import de.htwsaar.chessbot.uci.UCISender;
import de.htwsaar.chessbot.core.Board;

/**
 * Implementierung des primitiven Negamax-Algorithmus.
 * @author Johannes Haupt
 * @see <a href="https://en.wikipedia.org/wiki/Negamax">Wikipedia zu Negamax</a>
 */
public class NegaMaxSearcher
     extends AbstractMoveSearcher
  implements MoveSearcher 
{
    private static final String BASENAME = "negamax-searcher";
    private static int ID = 1;
    
    private final String mName;
    private long mNodes;
    private int mCurrentDepth;
    
    public NegaMaxSearcher(EvaluationFunction eval) {
        super(eval);
        mName = BASENAME + "#" + ID++;
    }
    
    @Override
    protected void prepareSearch() {
        super.prepareSearch();
        mNodes = 0;
        mCurrentDepth = 1;
    }
    
    @Override
    public String name() {
        return mName;
    }
    
    @Override
    public void go() {
        prepareSearch();
        start();
        iterativeSearch();
        stop();
    }
    
    private int iterativeSearch() {
        int score = 0;
        while (isSearching()) {
            
            infoDepth(mCurrentDepth);
            score = searchRoot(mCurrentDepth);
            mCurrentDepth += 1;
            
            if (shouldStop(mCurrentDepth, mNodes))
                stop();
        }
        return score;
    }
    
    private int searchRoot(int depth) {
        //Check timeout
        if (shouldStop(depth, mNodes))
            stop();
        if (!isSearching())
            return 0;

        int score;
        int max = -INFINITE;
        int moveNum = 1;
        Board[] children = getBoardList();
        for (Board childPos : children) {
            
            score = -search(childPos, depth-1);
            
            //Check timeout
            if (shouldStop(depth, mNodes))
                stop();
            if (!isSearching())
                return 0;
            
            if (score > max) {
                setBestMove(childPos.getLastMove());
                info(getBestMove().toString(), score);
                max = score;
            }
            moveNum += 1;
        }
        return max;
    }
    
    private int search(final Board b, final int depth) { 
        //Check timeout
        if (shouldStop(depth, mNodes))
            stop();
        if (!isSearching())
            return 0;
        
        mNodes++;
        
        if (depth == 0)
            return evaluate(b);
        
        int score;
        int max = -INFINITE;
        Board[] children = b.getResultingPositions();
        for (Board childPos : children) {
            score = -search(childPos, depth-1);
            if (score > max) {
                max = score;
            }
        }
        return max;
    }
    
    private void info(final String currmove, final int score) {
        long time = getConfiguration().getElapsedTime();
        String message = String.format(INFO,
            currmove, mCurrentDepth, score, time, mNodes, getNps(mNodes, time)
        );
        UCISender.getInstance().sendToGUI(message);
    }
    
    private static void infoDepth(int depth) {
        UCISender.getInstance().sendToGUI(
            String.format(INFO_DEPTH, depth)
        );
    }
    
    private long getNps(final long nodes, final long time) {
        if (time == 0L) {
            return 0;
        }
        
        return 1000 * mNodes / time;
    }
    
    private static final String INFO =
        "info currmove %s depth %d score cp %d time %d nodes %d nps %d";
    private static final String INFO_DEPTH =
        "info depth %d";
}
