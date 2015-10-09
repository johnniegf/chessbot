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
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardUtils;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.engine.model.move.Move.NOMOVE;
import static de.htwsaar.chessbot.util.Exceptions.checkCondition;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class AlphaBetaSearcher extends Thread
//implements MoveSearcher 
{
	private static int INSTANCE_COUNT = 0;
    private static final int DEPTH_LIMIT = 100;
    private static final int INFINITE = 1_000_000;
    private static final String EXN_INVALID_BOARD =
        "Ungültige Ausgangsstellung!";
    
    private final HashTable mHashTable = new HashTable();
    private Board     mInitial;
    private final EvaluationFunction mEvaluator;
    private Move mBestMove = NOMOVE;
    private int mBestScore;
    private long deadLine;
    private SearchInfo mCurrentSearch;
    private SearchInfo mLimits;
    
    private boolean stopSearching = true;
    
    
    public AlphaBetaSearcher(final Board fromPosition,
                             final EvaluationFunction eval) 
    {
        checkNull(fromPosition, "Ausgangsstellung");
        checkNull(eval, "Evaluator");
        checkCondition(fromPosition.isValid(), EXN_INVALID_BOARD);
        
        mInitial = fromPosition;
        mEvaluator = eval;
        mCurrentSearch = new SearchInfo();
        mLimits = new SearchInfo();
        setName("AlphaBeta-" + INSTANCE_COUNT++);
    }
    
    @Override
    public void run() {
    	while(true) {
    		while(stopSearching) {
    			try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		
    		setPriority(6);
    		deepeningSearch(mLimits.depth);
    		stopSearching = true;
    		sendBestMove();
    		setPriority(3);
    	}
    }
    
    private HashTable getHashTable() {
        return mHashTable;
    }

    public final void setDepth(final int depth) {
        mLimits.depth = depth;
        //checkInBounds(depth, 0, 100);
    }
    
    public boolean hasDepth() {
        return mLimits.depth > 0;
    }
    
    public int getDepth() {
        return mLimits.depth;
    }
    
    public final void stopSearch() {
        stopSearching = true;
    }
    
    private final void clearForSearch() {
        stopSearching = false;
        mCurrentSearch = new SearchInfo();
    }
    
    public final void go() {
        clearForSearch();
    }
    
    public final void setBoard(final Board board) {
        mInitial = board;
    }
    
    public void setDeadLine(long deadLine) {
    	this.deadLine = deadLine;
    }
    
    private boolean isSearchStopped() {
    	return stopSearching
    			|| this.deadLine >= System.currentTimeMillis();
    }
    
    private void deepeningSearch(int maxDepth) {
        mCurrentSearch.depth = 1;
        int bound = INFINITE; // (mInitial.isWhiteAtMove() ? INFINITE : -INFINITE);
        while (maxDepth > 0 && mCurrentSearch.depth <= maxDepth
           &&  !stopSearching) 
        {
            search(mInitial, mCurrentSearch.depth, 0, -bound, bound, true);
            mCurrentSearch.depth++;
            if (hasDepth() && mCurrentSearch.depth > mLimits.depth)
                stopSearch();
        }
    }
    
    private int quiescence(Board board, int alpha, int beta) {
        return mEvaluator.evaluate(board);
    }
    
    private int search(final Board board,
                       int depth,
                       final int ply,
                       int alpha,
                       int beta,
                       boolean isRoot) 
    {
        checkInBounds(beta, alpha, INFINITE);
        
        int hashTableFlag = FLAG_ALPHA;
        int score = -INFINITE;
        Move bestMove = NOMOVE;
        MoveInfo hashTableMove = new MoveInfo();
        int legalMoves = 0;
        int oldAlpha;
        int mateThreshold = INFINITE - ply;
        
        /* *********************************************
         * TIMEOUT CHECK
         ***********************************************/
        if (hasDepth() && getDepth() < depth)
            stopSearching = true;
        if (stopSearching)
            return 0;
        
        /* *********************************************
        *   MATE DISTANCE PRUNING
        *************************************************/
        if (alpha < -mateThreshold)
            alpha = -mateThreshold;
        if (beta > mateThreshold - 1)
            beta = mateThreshold - 1;
        if (alpha >= beta) {
            sendInfo(depth, score);
            return alpha;
        }
        
        if (BoardUtils.isInCheck(board))
            depth += 1;
        
        // Evaluation or quiescence search
        if (depth <= 0) {
            return -quiescence(board, alpha, beta);
        }
        
        mCurrentSearch.nodes++;
        
        if (board.isRepetition())
            return 0;
        if (board.getHalfMoves() >= 100)
            return 0;
        
        // HashTable lookup
        if (getHashTable().get(board, depth, alpha, beta, hashTableMove)) {
            score = hashTableMove.score();
            if (score > alpha && score < beta)
                return score;
        }
        
        /* ================================================================== *\
        |* === ITERATE THROUGH CHILD POSITIONS ============================== *|
        \* ================================================================== */
        
        Board[] children = probePVMove(board, depth, alpha, beta);
        
        if (children.length > 0) {
            bestMove = children[0].getLastMove();
        }
//        sendInfo("Entering child position from depth " + (mCurrentSearch.depth-depth));
        for (Board childPosition : children) {
            legalMoves += 1;
            Move currentMove = childPosition.getLastMove();
            
//            sendInfo("Before recursion, searching move " + currentMove);
//            sendInfo(depth, ply, score, alpha, beta, hashTableFlag);
            score = -search(childPosition, depth-1, ply+1, -beta, -alpha, false);
            sendInfo(depth, score);
            if (score > alpha) {
                bestMove = currentMove;
                if (isRoot)
                    setBestMoveInfo(bestMove, score);
                if (score >= beta) {
                    hashTableFlag = FLAG_BETA;
                    alpha = beta;
                    break;
                }
                hashTableFlag = FLAG_PV;
                alpha = score;
            }
        }
        
        if (legalMoves == 0) {
            bestMove = NOMOVE;
            if (BoardUtils.isInCheck(board)) {
                alpha = -INFINITE + ply;
            } else {
                alpha = 0;
            }
        }
        
        getHashTable().put(board, bestMove, depth, alpha, hashTableFlag);
        
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
    
    private void setBestMoveInfo(final Move bestMove, final int score) {
        checkNull(bestMove);
        mBestMove = bestMove;
        mBestScore = score;
//        sendInfo("Set best move " + mBestMove + " with score " + mBestScore);
    }
    
    public Move bestMove() {
        return mBestMove;
    }
    
    public int bestScore() {
        return mBestScore;
    }
    
    public void sendBestMove() {
    	String bestMove = "bestmove %s";
    	UCISender.getInstance().sendToGUI(String.format(bestMove, bestMove()));
    }
    
    private static <T> void swap(T[] arr, int idx1, int idx2) {
        checkInBounds(idx1, 0, arr.length);
        checkInBounds(idx1, 0, arr.length);
        T tmp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = tmp;
    }
    
    private static final class SearchInfo {
        public long nodes;
        public long msec;
        public int depth;
        
        public SearchInfo() {
            nodes = 0;
            msec  = 0;
            depth = 1;
        }
    }
    
    private static void sendInfo(String info) {
        if (DEBUG)
            System.out.println("info string " + info);
    }
    
    private static void sendInfo(int depth, int score) {
    	String info = "info depth %d score cp %d";
    	UCISender.getInstance().sendToGUI(String.format(info, depth, score));
    	
    }
    
    private static final boolean DEBUG = false;
 }
