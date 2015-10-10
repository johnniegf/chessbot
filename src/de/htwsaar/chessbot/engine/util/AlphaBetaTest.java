/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.search.AlphaBetaSearcher;
import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.eval.Evaluator;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardUtils;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.engine.model.move.Move.NOMOVE;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class AlphaBetaTest {
    
    public static void main(String[] args) {
//        Board b = Board.B("8/8/8/8/2P1BP2/P1K1B3/4k3/8 w - - 69 93");
//        Board b = Board.B("8/8/7Q/8/7q/P2B3k/2PB1P2/5K2 w - - 5 43");
//        Board b = Board.B("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
//        Board b = Board.B("5r2/3b2bp/1ppk1np1/4p3/2P5/3P1B2/1BNPKPPP/8 w - - 6 33");
//        Board b = Board.B("5r2/3b2bp/1ppk1np1/4p3/2P5/3P1B2/1BNPKPPP/8 w - - 10 35");
        Board b = Board.B();
        EvaluationFunction ev = new Evaluator();
        AlphaBetaSearcher abs = new AlphaBetaSearcher(b, ev);
        Board[] moveList = b.getResultingPositions();
        Move bestMove;
        for (int c = 0; c < 2*MOVES_PER_POS; c++) {
            abs.setBoard(b);
            System.out.println("-----------------------------------------------");
            System.out.println(b);
            System.out.println("FEN = " + b.toFenString());
            if (BoardUtils.isMate(b)) {
                System.out.println("Mate");
                break;
            }
            if (BoardUtils.isStalemate(b)) {
                System.out.println("Stalemate");
                break;
            }
            if (BoardUtils.isDraw(b)) {
                System.out.println("Draw");
                break;
            }
            
            for (int i = 1; i <= DEPTH_LIMIT; i++) {
                abs.setDepth(i);
                long time = System.currentTimeMillis();
                abs.go();
                time = System.currentTimeMillis() - time;
                System.out.println("info depth " + i 
                                 + " bestmove " + abs.bestMove() 
                                 + " score " + abs.bestScore()
                                 + " time " + time + " ms"
                                 + " hash " + abs.getHashTable().size());
            }
            bestMove = abs.bestMove();
            checkNull(bestMove, "bestmove");
            if (bestMove.type() == NOMOVE.type()) {
                System.out.println("Didn't find a best move...");
                break;
            }
            b = abs.bestMove().execute(b);
//            abs = new AlphaBetaSearcher(b, ev);
            System.out.println();
        }
    }
    
    private static int score(Board b, int score) {
//        return score;
        return (b.isWhiteAtMove() ? 1 : -1) * (int) Math.abs(score);
    }
    
    private static final int MOVES_PER_POS = 300;
    private static final int DEPTH_LIMIT = 2;
}
