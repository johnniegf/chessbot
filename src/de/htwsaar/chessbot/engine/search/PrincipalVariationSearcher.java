/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardUtils;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.engine.model.move.Move.NOMOVE;
import static de.htwsaar.chessbot.engine.search.EvaluationHashTable.UNDEFINED;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_ALPHA;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_BETA;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_PV;
import de.htwsaar.chessbot.engine.search.HashTable.MoveInfo;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class PrincipalVariationSearcher
        extends AbstractMoveSearcher
        implements MoveSearcher {

    private static final String NAME = "pv-searcher";
    private static int ID = 1;
    private static final int INFINITE = 1_000_000;

    private final EvaluationHashTable mEvalTable;
    private final List<Move> mPvLine;
    private int mCurrentDepth;
    private long mNodes;
    private final String mName;

    public PrincipalVariationSearcher(EvaluationFunction evaluator) {
        super(evaluator);
        mPvLine = new LinkedList<Move>();
        mEvalTable = new EvaluationHashTable();
        mName = NAME + "#" + ID++;
    }

    public String name() {
        return mName;
    }

    protected void prepareSearch() {
        super.prepareSearch();
        mPvLine.clear();
        mCurrentDepth = 1;
        mNodes = 0;
    }

    public void go() {
        prepareSearch();
        start();
        iterativeSearch();
    }

    private void iterativeSearch() {
        mCurrentDepth = 1;
        int score = searchRoot(mCurrentDepth, -INFINITE, INFINITE);
        while (isSearching()) {
            
            // checkDepth
            score = widenSearch(mCurrentDepth, score);
            mCurrentDepth += 1;
            
            // Check timeout
            if (shouldStop(mCurrentDepth, mNodes))
                stop();
        }
        stop();
    }
    
    private int widenSearch(int depth, int score) {
        int temp = score;
        int alpha = score - 50;
        int beta  = score + 50;
 
        // Check the aspiration window.
        temp = searchRoot(depth, alpha, beta);
        
        // If the narrow window search fails, re-search with full window.
        if (temp <= alpha || temp >= beta)
            temp = searchRoot(depth, -INFINITE, INFINITE);
        return temp;
    }

    private Board pickNextMove(final Board current,
            final Board[] positions,
            final int moveNumber,
            final int depth,
            final int alpha,
            final int beta) 
    {
        if (moveNumber > 0) {
            return positions[moveNumber];
        }

        MoveInfo mi = new MoveInfo();
        if (getHashTable().get(current, depth, alpha, beta, mi)) {
            Move next = mi.move();
            for (int i = 0; i < positions.length; i++) {
                if (next.equals(positions[i].getLastMove())) {
                    swap(positions, moveNumber, i);
                    break;
                }
            }
        }
        return positions[moveNumber];
    }

    private int searchRoot(int depth, int alpha, int beta) {
        int score = -INFINITE;
        int hashFlag = FLAG_ALPHA;
        Move bestMove = NOMOVE;

        // Wir erweitern die Suchtiefe, falls wir im Schach stehen
        if (BoardUtils.isInCheck(getBoard())) {
            depth += 1;
        }

        int moveCount = getBoardList().length;

        Board childPos = null;
        for (int moveNumber = 0; moveNumber < moveCount; moveNumber++) {

            childPos = pickNextMove(getBoard(), getBoardList(), moveNumber, depth, alpha, beta);

            // Falls wir den ersten Zug der Liste untersuchen oder
            // die Zero-Window-Suche fehlschlägt, führen wir die Suche mit dem
            // vollen Fenster erneut durch.
            if (moveNumber == 0
                    || -searchInner(childPos, depth - 1, 0, -alpha - 1, -alpha, false) > alpha) {
                score = -searchInner(childPos, depth - 1, 0, -beta, -alpha, true);
            }

            if (shouldStop(depth, mNodes)) {
                stop();
                return 0;
            }

            if (score > alpha) {
                bestMove = childPos.getLastMove();
                setBestMove(bestMove);
                if (score >= beta) {
                    getHashTable().put(childPos, bestMove, depth, beta, FLAG_BETA);
                    break;
                }
                alpha = score;
                getHashTable().put(childPos, bestMove, depth, alpha, FLAG_ALPHA);
            }
        }

        getHashTable().put(childPos, bestMove, depth, alpha, FLAG_PV);
        return alpha;
    }

    private int searchInner(final Board board,
            int depth,
            final int ply,
            int alpha,
            int beta,
            final boolean isPV) {
        int score = -INFINITE;
        Move bestMove = NOMOVE;
        MoveInfo hashMove = new MoveInfo();
        int hashFlag = FLAG_ALPHA;
        int mateThreshold = INFINITE - ply;
        boolean inCheck = BoardUtils.isInCheck(board);

        // Check timeout
        
        
        // Mate pruning
        
        
        // Check extension
        if (inCheck) {
            depth += 1;
        }

        // Leaf evaluation / quiescence search
        if (depth == 0) {
            return quiescenceSearch(board, alpha, beta);
        }

        mNodes += 1;

        // Repetition checking
        if (board.isRepetition()) {
            return contempt();
        }

        // Probe hash table
        if (getHashTable().get(board, depth, alpha, beta, hashMove)) {
            score = hashMove.score();
            if (!isPV || (score > alpha && score < beta)) {
                return score;
            }
        }

        // Generate child positions
        Board[] moveList = board.getResultingPositions();

        int reductionDepth = 0;
        int newDepth = depth - 1;
        int legalMoves = 0;

        int oldAlpha = alpha;
        for (int moveNum = 0; moveNum < moveList.length; moveNum++) {
            Board currPos = moveList[moveNum];
            Move currMove = currPos.getLastMove();

            if (!isPV && newDepth > 3
                    && moveNum > 3
                    && !inCheck
                    && currMove.isQuiet(board))
            {
                reductionDepth = 1;
                if (moveNum > 8) {
                    reductionDepth += 1;
                }

                newDepth -= reductionDepth;
            }

            do {
                if (alpha == oldAlpha) {
                    // Falls alpha nicht erhöht wurde, suchen wir normal weiter
                    score = -searchInner(currPos, newDepth, ply + 1, -beta, -alpha, isPV);
                } else if (-searchInner(currPos, newDepth, ply + 1, -alpha - 1, -alpha, false) > alpha) {
                    // Andernfalls führen wir eine ZWS durch. Wenn diese einen
                    // Alpha-Cutoff verursacht, suchen wir mit vollem Such-
                    // fenster erneut.
                    score = -searchInner(currPos, newDepth, ply + 1, -beta, -alpha, true);
                }

                if (reductionDepth > 0 && score > alpha) {
                    newDepth += reductionDepth;
                    reductionDepth = 0;
                } else {
                    break;
                }
            } while (true);

            legalMoves += 1;

            // Check timeout
            if (getConfiguration().isTimeOut())
                stop();
            if (!isSearching())
                return 0;
            
            // Cutoffs
            if (score > alpha) {
                bestMove = currMove;
                if (score >= beta) {
                    hashFlag = FLAG_BETA;
                    alpha = beta;
                    break;
                }
                hashFlag = FLAG_PV;
                alpha = score;
            }

        } // End of recursion

        // Matt- und Patterkennung
        if (legalMoves == 0) {
            bestMove = NOMOVE;
            if (inCheck) {
                alpha = -INFINITE + ply;
            } else {
                alpha = contempt();
            }
        }

        getHashTable().put(board, bestMove, depth, alpha, hashFlag);
        return alpha;
    }

    private int quiescenceSearch(final Board position, int alpha, int beta) {
        int score = mEvalTable.get(position.hash());
        if (score == UNDEFINED) {
            score = mEvaluator.evaluate(position);
            mEvalTable.put(position.hash(), score);
        }
        return score;
    }

    private static void swap(Board[] positions, int moveNumber, int i) {
        Board temp = positions[moveNumber];
        positions[moveNumber] = positions[i];
        positions[i] = temp;
    }

    /**
     * Evaluation für Patt- und Remissituationen.
     * 
     * @return Bewertung einer Pattsituation.
     */
    private int contempt() {
        return 0;
    }

}
