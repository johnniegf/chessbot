package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.search.eval.EvaluationFunction;
import de.htwsaar.chessbot.uci.UCISender;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.BitBoardUtils;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.core.moves.Move.NOMOVE;
import static de.htwsaar.chessbot.search.TranspositionHashTable.FLAG_ALPHA;
import static de.htwsaar.chessbot.search.TranspositionHashTable.FLAG_BETA;
import static de.htwsaar.chessbot.search.TranspositionHashTable.FLAG_PV;
import de.htwsaar.chessbot.search.TranspositionHashTable.MoveInfo;
import de.htwsaar.chessbot.util.Output;
import java.util.LinkedList;
import java.util.List;

/**
 * Alpha-Beta-Suchalgorithmus mit Principal Variation Search.
 * 
 * <p> Implementierung des Alpha-Beta-Algorithmus im Negamax-Stil mit 
 * folgenden Erweiterungen:</p>
 * <ul>
 *  <li><a href="https://en.wikipedia.org/wiki/Principal_variation_search">
 *      Principal Variation Search</a></li>
 *  <li><a href="http://chessprogramming.wikispaces.com/Transposition+Table">
 *      Transposition Hash Table</a></li>
 *  <li><a href="http://chessprogramming.wikispaces.com/Aspiration+Windows">
 *      Aspiration Windows</a></li>
 *  <li><a href="http://chessprogramming.wikispaces.com/Iterative+Deepening">
 *      Iterative Deepening</a></li>
 * </ul>
 * 
 * @author Johannes Haupt
 * @author David Holzapfel
 * @see TranspositionHashTable
 */
public class PrincipalVariationSearcher
        extends AbstractMoveSearcher
        implements MoveSearcher {

    private static final String NAME = "pv-searcher";
    private static int ID = 1;
    private static final int MATE_THRESHOLD = INFINITE - 2000;
    private static final boolean IS_PV = true;
    private static final boolean NO_PV = false;
    

    private List<Move> mPvLine;
    private int mCurrentDepth;
    private long mNodes;
    private final String mName;

    /**
     * Erzeuge einen neuen PV-Sucher mit der übergebenen Bewertungsfunktion.
     * @param evaluator Bewertungsfunktion.
     */
    public PrincipalVariationSearcher(final EvaluationFunction evaluator) {
        super(evaluator);
        mPvLine = new LinkedList<>();
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
    }

    @Override
    public void go() {
        prepareSearch();
        start();
        iterativeSearch();
        stop();
    }

    /**
     * Iterative deepening.
     * 
     * Suche aufsteigend alle Suchtiefen ab. Wird der Sucher von außen gestoppt,
     * bricht der Algorithmus umgehend ab.
     */
    private void iterativeSearch() {
        int score = searchRoot(mCurrentDepth, -INFINITE, INFINITE);
        while (isSearching()) {
            
            infoDepth(mCurrentDepth);
            infoHash();
            score = widenSearch(mCurrentDepth, score);
            if (mPvLine.size() > 1)
                setPonderMove(mPvLine.get(1));
            mCurrentDepth += 1;
            if (getBoardList().length < 2 && mCurrentDepth > 4)
                stop();
            
            // Check timeout
            if (shouldStop(mCurrentDepth, mNodes))
                stop();
        }
    }
    
    /**
     * Führe die Suche mit möglichst kleinem Fenster aus.
     * 
     * Zunächst wird um die übergebene Bewertung ein Fenster der Größe
     * 100 gebildet. Falls die Suche ein Ergebnis außerhalb dieses Fensters
     * produziert, muss mit vollem Suchfenster neu gesucht werden.
     * 
     * @param depth Suchtiefe
     * @param score Bewertung um die das Fenster gebildet werden soll.
     * @return Ergebnis der Suche.
     */
    private int widenSearch(int depth, int score) {
        int alpha = score - ASPIRATION_WINDOW;
        int beta  = score + ASPIRATION_WINDOW;
 
        // Suche mit kleinem Fenster durchführen.
        int temp = searchRoot(depth, alpha, beta);
        
        // Falls das fehlschlägt, muss eine neue Suche mit großem Suchfenster
        // durchgeführt werden
        if (temp <= alpha || temp >= beta)
            temp = searchRoot(depth, -INFINITE, INFINITE);
        return temp;
    }
    private static final int ASPIRATION_WINDOW = 50;

    private Board pickNextMove(final Board current,
            final Board[] positions,
            final int moveNumber,
            final int depth,
            final int alpha,
            final int beta) 
    {
        MoveInfo mi = new MoveInfo();
        if (getHashTable().get(current, depth, alpha, beta, mi)) {
            Move next = mi.move();
            for (int i = moveNumber; i < positions.length; i++) {
                if (next.equals(positions[i].getLastMove())) {
                    swap(positions, moveNumber, i);
                    break;
                }
            }
        } else {
            int maxScore = -INFINITE;
            int maxindex = -1;
            for (int i = moveNumber+1; i < positions.length; i++) {
                if (positions[i].score() > maxScore) {
                    maxScore = positions[i].score();
                    maxindex = i;
                }
            }
            if (maxindex >= 0)
                swap(positions, moveNumber, maxindex);
        }
        return positions[moveNumber];
    }
    
    
    @Override
    public Move getBestMove() {
        return mPvLine.get(0);
    }
    
    /**
     * Algorithmus für die Wurzel.
     * @param depth gewünschte Suchtiefe.
     * @param alpha untere Schranke der Bewertung.
     * @param beta  obere Schranke der Bewertung.
     * @return Bewertung des besten Zugs.
     */
    private int searchRoot(int depth, int alpha, int beta) {
        int score = -INFINITE;
        Move bestMove = NOMOVE;

        // Wir erweitern die Suchtiefe, falls wir im Schach stehen
        if (BitBoardUtils.isInCheck(getBoard())) {
            depth += 1;
        }

        int moveCount = getBoardList().length;
            

        Board childPos = null;
        String currmove;
        for (int moveNumber = 0; moveNumber < moveCount; moveNumber++) {
            List<Move> line = new LinkedList<>();
            childPos = pickNextMove(getBoard(), getBoardList(), moveNumber, depth, alpha, beta);
            currmove = childPos.getLastMove().toString();

            // Falls wir den ersten Zug der Liste untersuchen oder
            // die Zero-Window-Suche fehlschlägt, führen wir die Suche mit dem
            // vollen Fenster erneut durch.
            if (moveNumber == 0
             || -searchInner(childPos, depth - 1, 0, -alpha - 1, -alpha, NO_PV, line) > alpha) 
            {
                score = -searchInner(childPos, depth - 1, 0, -beta, -alpha, IS_PV, line);
                childPos.setScore(score);
            }

            if (shouldStop(depth, mNodes)) {
                stop();
            }
            if (!isSearching()) {
                break;
            }

            if (score > alpha) {
                bestMove = childPos.getLastMove();
                
                if (score >= beta) {
                    getHashTable().put(getBoard(), bestMove, depth, beta, FLAG_BETA);
                    infoBeta(currmove,beta);
                    break;
                }
                
                if (bestMove != NOMOVE) {
                    line.add(0, bestMove);
                    mPvLine = line;
                }
                getHashTable().put(getBoard(), bestMove, depth, score, FLAG_ALPHA);
                infoPv(currmove,score, line);
                alpha = score;
            }
        }
        
        getHashTable().put(getBoard(), bestMove, depth, alpha, FLAG_PV);
        return alpha;
    }

    /**
     * Innerer Suchalgorithmus.
     * 
     * Der wesentliche Ablauf des Algorithmus ist derselbe wie bei der Wurzel.
     * Jedoch werden einige weitere Optimierungen in diesem Algorithmus nicht
     * in der Wurzel durchgeführt, weil dies z.T. unsinnig ist.
     * 
     * @param board zu bewertende Stellung.
     * @param depth verbleibende Suchtiefe.
     * @param ply   Halbzüge seit der Wurzel.
     * @param alpha Untere Schranke der Bewertung.
     * @param beta  Obere Schranke der Bewertung.
     * @param isPV  true, wenn die aktuelle Stellung ein PV-Knoten ist
     * @param pvLine Akkumulator für die PV-Zugfolge
     * @return Bewertung der aktuellen Stellung.
     */
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


        // Tritt eine Stellungswiederholung auf, oder nähern wir uns einem
        // Remis nach der 50-Züge-Regel, prüfen wir, ob sich ein Remis lohnt.
        if (board.isRepetition()) {
            return contempt(board);
        }

        // Suche der Stellung in der Transpositionstabelle
        if (getHashTable().get(board, depth, alpha, beta, hashMove)) {
            score = hashMove.score();
            Move move = hashMove.move();
            if (!isPV) {
                return score;
            }
            if  (score > alpha && score < beta) {
                Board child = board;
                while (move != NOMOVE) {
                    child = move.tryExecute(child);
                    if (!Move.isValidResult(child))
                        break;
                    pvLine.add(move);
                    if (getHashTable().get(child, 0, 0, 0, hashMove)) {
                        move = hashMove.move();
                    } else {
                        break;
                    }
                }
                return score;
            }
        }

        // Zugliste absuchen
        Board[] moveList = board.getResultingPositions();

        int reductionDepth = 0;
        int newDepth = depth - 1;
        int legalMoves = 0;

        int oldAlpha = alpha;

        for (int moveNum = 0; moveNum < moveList.length; moveNum++) {
            // Zeitlimit prüfen
            if (shouldStop(depth, mNodes)) {
                stop();
            }
            if (!isSearching()) {
                return 0;
            }
            
            List<Move> line = new LinkedList<Move>();
            Board currPos = moveList[moveNum];
//            Board currPos = pickNextMove(board, moveList, moveNum, depth, alpha, beta);
            Move currMove = currPos.getLastMove();

            // Late move reduction
            // Wegen der Sortierung der Stellungsliste gehen wir davon aus,
            // dass sich die Knoten der Principal Variation nicht am Ende der
            // Zugliste befinden. Dort suchen wir nicht so tief.
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
                    // fenster erneut. In diesem Fall müssen wir annehmen, dass
                    // wir uns in einem Knoten der PV befinden.
                    score = -searchInner(currPos, newDepth, ply + 1, -beta, -alpha, IS_PV, line);
                }
                
                // Falls wir die Suchtiefe reduziert haben und unsere Suche mit
                // einem Alpha-Cutoff fehlschlägt
                if (reductionDepth > 0 && score > alpha) {
                    newDepth += reductionDepth;
                    reductionDepth = 0;
                } else {
                    break;
                }
            } while (true);

            currPos.setScore(score);
            legalMoves += 1;

            
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

        } // Fertig mit der Rekursion

        // Matt- und Patterkennung
        if (legalMoves == 0) {
            bestMove = NOMOVE;
            if (inCheck) {
                alpha = -INFINITE + ply;
            } else {
                alpha = contempt(board);
            }
        }

        getHashTable().put(board, bestMove, depth, alpha, hashFlag);
        return alpha;
    }
    
    /**
     * Quiescence search.
     * @param position
     * @param alpha
     * @param beta
     * @return 
     */
    private int quiescenceSearch(final Board position, int alpha, int beta) {
        // TODO: Quiescence search implementieren. Blattknoten sollten nur
        //       bewertet werden, wenn der am Zug befindliche Spieler keine
        //       ungedeckten Figuren hat. Erreicht werden kann dies dadurch,
        //       dass alle Züge, in denen eine Figur geschlagen wird, ab der
        //       übergebenen Stellung generiert und untersucht werden.
        //       Diese können dann aufgrund von SEE (Static Exchange Evaluation)
        //       bewertet werden.
        return evaluate(position);
    }


    /**
     * Evaluation für Patt- und Remissituationen.
     * 
     * @return Bewertung einer Pattsituation.
     */
    private int contempt(final Board board) {
        // TODO: Tatsächlich bewerten. Ein Remis ist am Anfang der Partie
        //       weniger wünschenswert als im Endspiel, selbst wenn die
        //       Engine im Nachteil ist.
        return 0;
    }
    
    // =======================================================================
    // === AUSGABEN 
    // =======================================================================
    
    private static void infoDepth(int depth) {
        UCISender.getInstance().sendToGUI("info depth " + depth);
    }
    
    private void infoBeta(String currmove, int beta) {
        info(
            currmove,
            String.format(SCORE_BETA, beta),
            ""
        );
    }
    
    private void infoPv(String currmove, int score, List<Move> pvline) {
        String scoreString = null;
        if (Math.abs(score) < MATE_THRESHOLD) {
            scoreString = "cp " + score;
        } else {
            if (score > 0) {
                scoreString = "mate " + ((INFINITE-score) / 2 + 1);
            } else {
                scoreString = "mate " + (-(INFINITE+score) / 2 - 1);
            }
        }
        String pvLine = pvLineToString(pvline);
        info(currmove, scoreString, pvLine);
    }
    
    private void infoHash() {
        UCISender.getInstance().sendToGUI("info hashfull " + getHashTable().usage());
    }
    
    private void info(String currmove, String score, String pvline) {
        long time = getConfiguration().getElapsedTime();
        UCISender.getInstance().sendToGUI(
            String.format(INFO,
                currmove,
                mCurrentDepth, 
                score, 
                time, 
                mNodes, 
                countNps(mNodes, time),
                pvline
            )
        );
    }
    
    private static long countNps(long nodes, long time) {
        if (time == 0L) return 0L;
        
        return nodes * 1000 / time;
    }
    
    private String pvLineToString(final List<Move> pvline) {
        if (pvline == null || pvline.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder("pv");
        for (Move m : pvline) {
            sb.append(Output.SPACE);
            sb.append(m);
        }
        return sb.toString();
    }

    private static void swap(Board[] positions, int moveNumber, int i) {
        Board temp = positions[moveNumber];
        positions[moveNumber] = positions[i];
        positions[i] = temp;
    }
    
    private static final String INFO = 
        "info currmove %s depth %d score %s time %d nodes %d nps %d %s";
    private static final String SCORE_BETA = "upperbound %d";
}
