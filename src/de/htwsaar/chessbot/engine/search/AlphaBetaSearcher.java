/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_ALPHA;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_BETA;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_PV;
import de.htwsaar.chessbot.engine.search.HashTable.MoveInfo;
import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardUtils;
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
    private static final int DEPTH_LIMIT = 100;
    private static final int INFINITE = 1_000_000;
    private static final String EXN_INVALID_BOARD =
        "Ungültige Ausgangsstellung!";
    
    private final HashTable mHashTable = new HashTable();
    private Board     mInitial;
    private final EvaluationFunction mEvaluator;
    private int mDepth;
    private Move mBestMove;
    private int mBestScore;
    private transient int mCurrentDepth;
    
    private boolean stopSearching = true;
    
    
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
        checkInBounds(depth, 0, 100);
        mDepth = depth;
    }
    
    public boolean hasDepth() {
        return mDepth > 0;
    }
    
    public int getDepth() {
        return mDepth;
    }
    
    public final void stop() {
        stopSearching = true;
    }
    
    public final void go() {
        stopSearching = false;
        deepeningSearch(DEPTH_LIMIT);
    }
    
    public final void setBoard(final Board board) {
        mInitial = board;
    }
    
    private void deepeningSearch(int maxDepth) {
        mCurrentDepth = 1;
        int bound = INFINITE; // (mInitial.isWhiteAtMove() ? INFINITE : -INFINITE);
        while (maxDepth > 0 && mCurrentDepth <= maxDepth
           &&  !stopSearching) 
        {
            search(mInitial, mCurrentDepth, -bound, bound, true);
            mCurrentDepth++;
            if (hasDepth() && mCurrentDepth > mDepth)
                stop();
        }
    }
    
    private int quiescence(Board board, int alpha, int beta) {
        int sign = (board.isWhiteAtMove() ? 1 : -1);
        return sign * mEvaluator.evaluate(board);
    }
    
    private int search(final Board board,
                       final int depth,
                       int alpha,
                       int beta,
                       boolean isRoot) 
    {
        checkInBounds(beta, alpha, INFINITE);
        
        if (hasDepth() && getDepth() < depth)
            stopSearching = true;
        
        if (stopSearching)
            return 0;
        
        if (board.isRepetition())
            return 0;
        
        if (board.getHalfMoves() >= 100)
            return 0;
        
        
        MoveInfo moveInfo = new MoveInfo();
        moveInfo.setScore(-INFINITE-1);
        
        // Evaluation or quiescence search
        if (depth <= 0) {
            return -quiescence(board, alpha, beta);
        }
        
        // HashTable lookup
        if (getHashTable().get(board, depth, alpha, beta, moveInfo)) {
            if (isRoot)
                setBestMove(moveInfo);
            return moveInfo.score();
        }
        
        int legalMoves = 0;
        int oldAlpha = alpha;
        int score;
        Board[] children = probePVMove(board, depth, alpha, beta);
        
        for (Board childPosition : children) {
            legalMoves += 1;
            Move currentMove = childPosition.getLastMove();
            score = -search(childPosition, depth-1, -beta, -alpha, false);
            
            if (moveInfo.isNull())
                moveInfo.setMove(currentMove);
            
            if (score > moveInfo.score()) {
                moveInfo.setScore(score);
                moveInfo.setMove(currentMove);
                if (score >= beta) {
                    getHashTable().put(board, currentMove, depth, score, FLAG_BETA);
                    return beta;
                }
                if (score > alpha) {
                    alpha = score;
                }
            }
        }
        
        if (legalMoves == 0) {
            if (BoardUtils.isInCheck(board)) {
                return (board.isWhiteAtMove() ? INFINITE : -INFINITE);
            } else {
                return 0;
            }
        }
        
        if (alpha != oldAlpha) {
            getHashTable().put(board, moveInfo.move(), depth, moveInfo.score(), FLAG_PV);
        } else {
            getHashTable().put(board, moveInfo.move(), depth, alpha, FLAG_ALPHA);
        }
        
        if (isRoot)
            setBestMove(moveInfo);
        
        return alpha;
    }
    
    private Board[] probePVMove(final Board position, 
                                final int depth, 
                                final int alpha, 
                                final int beta) 
    {
        Board[] positions = position.getResultingPositions();
        MoveInfo mi = new MoveInfo();
        if (getHashTable().get(position, depth-1, alpha, beta, mi)) {
            Move pvMove = mi.move();
            for (int i = 0; i < positions.length; i++) {
                if ( pvMove.equals(positions[i].getLastMove()) ) {
                    swap(positions, 0, i);
                    break;
                }
            }
        }
        return positions;
    }
    
    private void setBestMove(final MoveInfo bestMove) {
        checkNull(bestMove);
        checkCondition(!bestMove.isNull(), "Move is null");
        mBestMove = bestMove.move();
        mBestScore = bestMove.score();
    }
    
    public Move bestMove() {
        return mBestMove;
    }
    
    public int bestScore() {
        return mBestScore;
    }
    
    private static <T> void swap(T[] arr, int idx1, int idx2) {
        checkInBounds(idx1, 0, arr.length);
        checkInBounds(idx1, 0, arr.length);
        T tmp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = tmp;
    }
 }
