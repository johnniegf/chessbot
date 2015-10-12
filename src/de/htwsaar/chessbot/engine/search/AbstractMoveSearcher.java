/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.Clock;
import de.htwsaar.chessbot.engine.Game;
import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.engine.model.move.Move.NOMOVE;
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
    private SearchConfiguration mConfig;
    private boolean mIsSearching;
    private Move mBestMove;
    protected final EvaluationFunction mEvaluator;
    private Move mPonderMove;
    private Game mGame;

    public AbstractMoveSearcher(final EvaluationFunction evaluator) {
        checkNull(evaluator);
        mHashTable = new HashTable();
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
        if (cl.movestogo == 0) {
            if (isWhiteAtMove)
                return cl.wtime / 20 + cl.winc;
            else
                return cl.btime / 20 + cl.binc;
        } else {
            if (isWhiteAtMove)
                return cl.wtime / cl.movestogo;
            else
                return cl.btime / cl.movestogo;
        }
    }

    @Override
    public void clearHashTable() {
        mHashTable.clear();
    }

    @Override
    public HashTable getHashTable() {
        return mHashTable;
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
