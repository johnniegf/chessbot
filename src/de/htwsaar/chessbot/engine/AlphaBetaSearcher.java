/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine;

import static de.htwsaar.chessbot.engine.HashTable.FLAG_BETA;
import static de.htwsaar.chessbot.engine.HashTable.FLAG_PV;
import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.util.Exceptions.checkCondition;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class AlphaBetaSearcher 
//implements MoveSearcher 
{
    
    private static final String EXN_INVALID_BOARD =
        "Ungültige Ausgangsstellung!";
    
    private final HashTable mHashTable = new HashTable();
    private final Board     mInitial;
    private final EvaluationFunction mEvaluator;
    private int mDepth;
    private Move mBestMove;
    
    
    public AlphaBetaSearcher(final Board fromPosition,
                             final EvaluationFunction eval) 
    {
        checkNull(fromPosition, "Ausgangsstellung");
        checkNull(eval, "Evaluator");
        checkCondition(fromPosition.isValid(), EXN_INVALID_BOARD);
        
        mInitial = fromPosition;
        mEvaluator = eval;
    }
    
    private HashTable getHashTable() {
        return mHashTable;
    }

    public final void setDepth(final int depth) {
        checkInBounds(depth, 1, 100);
        mDepth = depth;
    }
    
    public final void go() {
        
    }
    
    private int search(final Board board,
                       final int depth,
                       int alpha,
                       int beta) 
    {
        int score = 0;
        
        // HashTable lookup
        score = getHashTable().get(board, depth, alpha, beta);
        int flag = HashTable.FLAG_ALPHA;
        if (HashTable.isDefined(score))
            return score;

        if (depth == 0) {
            score = mEvaluator.evaluate(board);
            getHashTable().put(board, depth, score, FLAG_PV);
            return score;

        }
        for (Board childPosition : board.getResultingPositions()) {
            if (!Move.isValidResult(childPosition))
                continue;
            
            score = -search(childPosition, depth-1, -beta, -alpha);
            if (score >= beta) {
                getHashTable().put(childPosition, depth, beta, FLAG_BETA);
                return beta;
            }
            if (score > alpha) {
                flag = FLAG_PV;
                alpha = score;
            }
        }

        getHashTable().put(board, depth, alpha, flag);
        return alpha;
    }
    
    private void setBestMove(final Move bestMove) {
        checkNull(bestMove);
        mBestMove = bestMove;
    }
}
