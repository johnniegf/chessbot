/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.core.Clock;
import de.htwsaar.chessbot.core.Game;
import de.htwsaar.chessbot.search.eval.EvaluationFunction;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.core.moves.Move.NOMOVE;
import static de.htwsaar.chessbot.search.EvaluationHashTable.UNDEFINED;
import static de.htwsaar.chessbot.util.Exceptions.checkCondition;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public abstract class AbstractMoveSearcher
           implements MoveSearcher
{

    private Board mCurrentPosition;
    private Board[] mPositionList;
    private final HashTable mHashTable;
    private final EvaluationHashTable mEvalTable;
    private SearchConfiguration mConfig;
    private boolean mIsSearching;
    private Move mBestMove;
    protected final EvaluationFunction mEvaluator;
    private Move mPonderMove;
    private Game mGame;

    public AbstractMoveSearcher(final EvaluationFunction evaluator) {
        checkNull(evaluator);
        mHashTable = new HashTable();
        mEvalTable = new EvaluationHashTable();
        mConfig = new SearchConfiguration();
        mIsSearching = false;
        mEvaluator = evaluator;
    }

    protected void prepareSearch() {
        checkCondition(Move.isValidResult(getBoard()));
        EvaluationFunction.updatePieceValues();
        mBestMove = NOMOVE;
        mPositionList = getBoard().getResultingPositions();
        if (!mConfig.getMoves().isEmpty()) {
            List<Board> positionsToSearch = new ArrayList<>();
            List<Board> childPositions = Arrays.asList(mPositionList);
            for (Board currPos : childPositions) {
                if (mConfig.getMoves().contains(currPos.getLastMove()))
                    positionsToSearch.add(currPos);
            }
            mPositionList = positionsToSearch.toArray(new Board[0]);
        }
        if (mConfig.getTimeLimit() == 0L) {
            long timeLimit = getTimeLimit();
            mConfig.setTimeLimit(timeLimit);
        }
        mConfig.prepareForSearch();
    }
    
    private long getTimeLimit() {
        Clock cl = getGame().getClock();
        boolean isWhiteAtMove = getGame().getCurrentBoard().isWhiteAtMove();
        long mytime = (isWhiteAtMove ? cl.wtime : cl.btime);
        long myinc  = (isWhiteAtMove ? cl.winc  : cl.binc);
        long myMoveTime = 0;
        int movestogo = cl.movestogo;
        int movenum = getBoard().getFullMoves();
        if (movestogo == 0) {
            movestogo = 30;
            if (mytime > TEN_MINUTES)
                movestogo += 20;
            if (mytime > TWENTY_MINUTES)
                movestogo += 10;
        }
        myMoveTime = mytime / movestogo + myinc;
        if ( movenum < 5 )
            myMoveTime *= OPENING_INCREMENT;
        return myMoveTime;
    }
    
    private static final long TEN_MINUTES = 600_000L;
    private static final long TWENTY_MINUTES = 2 * TEN_MINUTES;
    private static final double OPENING_INCREMENT = 1.20;

    @Override
    public void clearHashTable() {
        mHashTable.clear();
    }

    @Override
    public HashTable getHashTable() {
        return mHashTable;
    }
    
    protected int evaluate(final Board position) {
        int score = mEvalTable.get(position.hash());
        if (score == UNDEFINED) {
            score = mEvaluator.evaluate(position);
            mEvalTable.put(position.hash(), score);
        }
        return score;
    }

    @Override
    public Move getBestMove() {
        return mBestMove;
    }
    
    protected void setBestMove(final Move bestMove) {
        mBestMove = bestMove;
    }
    
    @Override
    public Move getPonderMove() {
        return mPonderMove;
    }
    
    protected void setPonderMove(final Move ponderMove) {
        mPonderMove = ponderMove;
    }

    @Override
    public Board getBoard() {
        return mGame.getCurrentBoard();
    }

    protected Board[] getBoardList() {
        return mPositionList;
    }

    @Override
    public void setBoard(Board board) {
        checkCondition(Move.isValidResult(board));
        mCurrentPosition = board;
    }
    
    @Override
    public Game getGame() {
        return mGame;
    }
    
    @Override
    public void setGame(final Game game) {
        mGame = game;
    }

    @Override
    public void resetConfiguration() {
        mConfig = new SearchConfiguration();
    }

    @Override
    public SearchConfiguration getConfiguration() {
        return mConfig;
    }

    @Override
    public void stop() {
        mIsSearching = false;
    }
    
    protected void start() {
        mIsSearching = true;
    }

    protected boolean shouldStop(int depth, long nodes) {
        return mConfig.shouldStop(depth, nodes);
    }

    @Override
    public boolean isSearching() {
        return mIsSearching;
    }

    @Override
    public abstract void go();
}
