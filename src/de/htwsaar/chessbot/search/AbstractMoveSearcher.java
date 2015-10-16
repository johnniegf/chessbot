/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.config.Config;
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
 * Implementierungshilfe für Suchalgorithmen.
 * @author Johannes Haupt
 * @author David Holzapfel
 * @author Timo Klein
 */
public abstract class AbstractMoveSearcher
           implements MoveSearcher
{
    
    private boolean mIsSearching;
    private SearchConfiguration mConfig;
    
    private Game mGame;
    private Board[] mPositionList;
    private Move mBestMove;
    private Move mPonderMove;
    
    private final TranspositionHashTable mHashTable;
    private final EvaluationHashTable mEvalTable;
    private final EvaluationFunction mEvaluator;
    private final TimeStrategy mTimeStrategy;

    public AbstractMoveSearcher(final EvaluationFunction evaluator) {
        checkNull(evaluator);
        mHashTable = new TranspositionHashTable();
        mEvalTable = new EvaluationHashTable();
        mConfig = new SearchConfiguration();
        mIsSearching = false;
        mEvaluator = evaluator;
        mTimeStrategy = new SimpleTimeStrategy();
    }

    protected void prepareSearch() {
        checkCondition(Move.isValidResult(getBoard()));
        EvaluationFunction.updatePieceValues();
        updateTableSizes();
        mBestMove = NOMOVE;
        mPositionList = getBoard().getResultingPositions();
        for (Board b : mPositionList) {
            b.setScore(evaluate(b));
        }
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
            long timeLimit = mTimeStrategy.getMoveTime(getGame());
            mConfig.setTimeLimit(timeLimit);
        }
        mConfig.prepareForSearch();
    }
    
    private void updateTableSizes() {
        int capacity = (int) Config.getInstance().getOption("HashTableSize").getValue();
        mHashTable.setCapacity(capacity);
        mEvalTable.setCapacity(capacity);
    }
    
    @Override
    public void clearHashTables() {
        mHashTable.clear();
        mEvalTable.clear();
    }

    @Override
    public TranspositionHashTable getHashTable() {
        return mHashTable;
    }
    
    protected int evaluate(final Board position) {
        int score = mEvalTable.get(position.hash());
        if (score == UNDEFINED) {
            if (mEvaluator.isAbsolute()) {
                score = mEvaluator.evaluate(position)
                      * (position.isWhiteAtMove() ? 1 : -1);
            } else {
                score = mEvaluator.evaluate(position);
            }
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
    public Game getGame() {
        return mGame;
    }
    
    @Override
    public void setGame(final Game game) {
        checkNull(game);
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
