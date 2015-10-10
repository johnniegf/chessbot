/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.model.Board;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class NegaMaxSearcher
     extends AbstractMoveSearcher
  implements MoveSearcher 
{
    private static final String BASENAME = "negamax-searcher";
    private static int ID = 1;
    
    private final String mName;
    private long mNodes;
    
    public NegaMaxSearcher(EvaluationFunction eval) {
        super(eval);
        mName = BASENAME + "#" + ID++;
    }
    
    @Override
    protected void prepareSearch() {
        super.prepareSearch();
        mNodes = 0;
    }
    
    @Override
    public String name() {
        return mName;
    }
    
    @Override
    public void go() {
        prepareSearch();
        start();
        searchRoot(getConfiguration().getDepthLimit());
    }
    
    private int searchRoot(int depth) {
        if (depth == 0)
            depth = 3;
        int score;
        int max = -INFINITE;
        Board[] children = getBoardList();
        for (Board childPos : children) {
            score = -search(childPos, depth-1);
            if (score > max) {
                setBestMove(childPos.getLastMove());
                max = score;
            }
        }
        return max;
    }
    
    private int search(final Board b, final int depth) { 
        //Check timeout
        if (shouldStop(depth, mNodes))
            stop();
        
        if (depth == 0)
            return mEvaluator.evaluate(b);
        
        mNodes++;
        
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
}
