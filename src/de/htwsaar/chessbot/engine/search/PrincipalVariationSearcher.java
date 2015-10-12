/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BitBoardUtils;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.engine.model.move.Move.NOMOVE;
import static de.htwsaar.chessbot.engine.search.EvaluationHashTable.UNDEFINED;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_ALPHA;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_BETA;
import static de.htwsaar.chessbot.engine.search.HashTable.FLAG_PV;
import de.htwsaar.chessbot.engine.search.HashTable.MoveInfo;
import de.htwsaar.chessbot.util.Output;
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
    private static final int MATE_THRESHOLD = INFINITE - 2000;
    private static final boolean IS_PV = true;
    private static final boolean NO_PV = false;
    

    private final EvaluationHashTable mEvalTable;
    private List<Move> mPvLine;
    private int mCurrentDepth;
    private long mNodes;
    private final String mName;
    private int mBestScore;

    public PrincipalVariationSearcher(EvaluationFunction evaluator) {
        super(evaluator);
        mPvLine = new LinkedList<>();
        mEvalTable = new EvaluationHashTable();
        mName = NAME + "#" + ID++;
    }

    @Override
    public String name() {
        return mName;
    }

    @Override
    protected void prepareSearch() {
        super.prepareSearch();
        mPvLine.clear();
        mCurrentDepth = 1;
        mNodes = 0;
        mBestScore = -INFINITE;
    }

    @Override
    public void go() {
        prepareSearch();
        start();
        iterativeSearch();
        stop();
    }

    private int iterativeSearch() {
        int score = searchRoot(mCurrentDepth, -INFINITE, INFINITE);
        while (isSearching()) {
            
            infoDepth(mCurrentDepth);
            score = widenSearch(mCurrentDepth, score);
            if (mPvLine.size() > 1)
                setPonderMove(mPvLine.get(1));
            if (score > MATE_THRESHOLD)
                stop();
            infoHash();
            mCurrentDepth += 1;
            
            // Check timeout
            if (shouldStop(mCurrentDepth, mNodes))
                stop();
        }
        return score;
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
//        if (moveNumber > 0) {
//            return positions[moveNumber];
//        }

        MoveInfo mi = new MoveInfo();
        if (getHashTable().get(current, depth, alpha, beta, mi)) {
            Move next = mi.move();
            for (int i = moveNumber; i < positions.length; i++) {
                if (next.equals(positions[i].getLastMove())) {
                    swap(positions, moveNumber, i);
                    break;
                }
            }
        }
        return positions[moveNumber];
    }
    
    private static void infoDepth(int depth) {
        UCISender.getInstance().sendToGUI("info depth " + depth);
    }
    
    private void infoPv(int score) {
        String scoreString = null;
        if (Math.abs(score) < INFINITE - 2000) {
            scoreString = "cp " + score;
        } else {
            if (score > 0) {
                scoreString = "mate " + ((INFINITE-score) / 2 + 1);
            } else {
                scoreString = "mate " + (-(INFINITE+score) / 2 - 1);
            }
        }
        long time = System.currentTimeMillis() - getConfiguration().getTimeStarted();
        String pvLine = pvLineToString();
        String info = "info depth %d score %s time %d nodes %d nps %d %s";
        UCISender.getInstance().sendToGUI(
            String.format(
                info, mCurrentDepth, scoreString, time,
                mNodes, countNps(mNodes, time), pvLine
            )
        );
    }
    
    private void infoHash() {
        UCISender.getInstance().sendToGUI("info hashfull " + getHashTable().usage());
    }
    
    private static long countNps(long nodes, long time) {
        if (time == 0L) return 0L;
        
        return nodes * 1000 / time;
    }
    
    private String pvLineToString() {
        if (mPvLine.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder("pv");
        for (Move m : mPvLine) {
            sb.append(Output.SPACE);
            sb.append(m);
        }
        return sb.toString();
    }
    
    public void setBestMove(final Move move, final int score) {
        if (score > mBestScore) {
            super.setBestMove(move);
            mBestScore = score;
        }
    }
    
    public Move getBestMove() {
//        findPvLine();
        if (mPvLine.isEmpty())
            return super.getBestMove();
        else
            return mPvLine.get(0);
    }
    
    private List<Move> findPvLine() {
        List<Move> pvLine = new LinkedList<Move>();
        Board current = getBoard();
        MoveInfo mi = new MoveInfo();
        for (int i = 1; i <= mCurrentDepth; i++) {
            if (!getHashTable().get(current, 0, 0, 0, mi)) {
                break;
            }
            current = mi.move().tryExecute(current);
            pvLine.add(mi.move());
        }
        if (pvLine.size() > mPvLine.size())
            mPvLine = pvLine;
        return pvLine;
    }

    private int searchRoot(int depth, int alpha, int beta) {
        int score = -INFINITE;
        Move bestMove = NOMOVE;
        List<Move> pvLine = new LinkedList<Move>();

        // Wir erweitern die Suchtiefe, falls wir im Schach stehen
        if (BitBoardUtils.isInCheck(getBoard())) {
            depth += 1;
        }

        int moveCount = getBoardList().length;
            

        Board childPos = null;
        for (int moveNumber = 0; moveNumber < moveCount; moveNumber++) {

            childPos = pickNextMove(getBoard(), getBoardList(), moveNumber, depth, alpha, beta);
//            if (NOMOVE == getBestMove())
//                setBestMove(childPos.getLastMove());

            // Falls wir den ersten Zug der Liste untersuchen oder
            // die Zero-Window-Suche fehlschlägt, führen wir die Suche mit dem
            // vollen Fenster erneut durch.
            if (moveNumber == 0
                    || -searchInner(childPos, depth - 1, 0, -alpha - 1, -alpha, NO_PV, pvLine) > alpha) {
                score = -searchInner(childPos, depth - 1, 0, -beta, -alpha, IS_PV, pvLine);
            }

            if (shouldStop(depth, mNodes)) {
                stop();
            }
            if (!isSearching()) {
                break;
            }

            if (score > alpha) {
                bestMove = childPos.getLastMove();
                setBestMove(bestMove, score);
                if (score >= beta) {
                    getHashTable().put(getBoard(), bestMove, depth, beta, FLAG_BETA);
                    infoPv(beta);
                    break;
                }
                getHashTable().put(getBoard(), bestMove, depth, score, FLAG_ALPHA);
                infoPv(score);
                alpha = score;
            }
        }
        
        if (bestMove != NOMOVE) {
            pvLine.add(0, bestMove);
            if (pvLine.size() > mPvLine.size())
                mPvLine = pvLine;
        }
        getHashTable().put(getBoard(), bestMove, depth, alpha, FLAG_PV);
        return alpha;
    }

    private int searchInner(final Board board,
            int depth,
            final int ply,
            int alpha,
            int beta,
            final boolean isPV,
            final List<Move> pvLine) 
    {
        int score = -INFINITE;
        Move bestMove = NOMOVE;
        MoveInfo hashMove = new MoveInfo();
        int hashFlag = FLAG_ALPHA;
        int mateThreshold = INFINITE - ply;
        boolean inCheck = BitBoardUtils.isInCheck(board);

        // Check timeout
        if (shouldStop(depth, mNodes)) {
            stop();
        }
        if (!isSearching()) {
            return 0;
        }
        
        // Mate pruning
        if (alpha < -mateThreshold) 
            alpha = -mateThreshold;
        if (beta > mateThreshold-1)
            beta = mateThreshold-1;
        if (alpha >= beta)
            return alpha;
        
        
        // Check extension
        if (inCheck) {
            depth += 1;
        }

        mNodes += 1;
        
        // Leaf evaluation / quiescence search
        if (depth == 0) {
            return quiescenceSearch(board, alpha, beta);
        }


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
            List<Move> line = new LinkedList<Move>();
            //Board currPos = moveList[moveNum];
            Board currPos = pickNextMove(board, moveList, moveNum, depth, alpha, beta);
            Move currMove = currPos.getLastMove();

            if (!isPV && newDepth > 3
                    && moveNum > 3
                    && BitBoardUtils.isInCheck(currPos)
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
                    score = -searchInner(currPos, newDepth, ply + 1, -beta, -alpha, isPV, line);
                } else if (-searchInner(currPos, newDepth, ply + 1, -alpha - 1, -alpha, NO_PV, line) > alpha) {
                    // Andernfalls führen wir eine ZWS durch. Wenn diese einen
                    // Alpha-Cutoff verursacht, suchen wir mit vollem Such-
                    // fenster erneut.
                    score = -searchInner(currPos, newDepth, ply + 1, -beta, -alpha, IS_PV, line);
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
            if (shouldStop(depth, mNodes)) {
                stop();
            }
            if (!isSearching()) {
                return 0;
            }
            
            // Cutoffs
            if (score > alpha) {
                bestMove = currMove;
                if (score >= beta) {
                    hashFlag = FLAG_BETA;
                    alpha = beta;
                    break;
                }
                pvLine.clear();
                pvLine.add(0, currMove);
                pvLine.addAll(line);
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
