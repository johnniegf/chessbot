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
//        Board b = Board.B("1rbqkbnr/pppppppp/n7/8/8/N7/PPPPPPPP/1RBQKBNR w Kk - 12 7");
        Board b = Board.B();
        EvaluationFunction ev = new Evaluator();
        AlphaBetaSearcher abs = new AlphaBetaSearcher(b, ev);
        Board[] moveList = b.getResultingPositions();
        for (Board current : moveList) {
            b = current;
            for (int c = 0; c < MOVES_PER_POS; c++) {
                abs.setBoard(b);
                System.out.println("-----------------------------------------------");
                System.out.println(b);
                System.out.println("FEN = " + b.toFenString());
                if (BoardUtils.isMate(b)) {
                    System.out.println("Mate");
                    return;
                }
                if (BoardUtils.isStalemate(b)) {
                    System.out.println("Stalemate");
                    return;
                }

                for (int i = DEPTH_LIMIT; i <= DEPTH_LIMIT; i++) {
                    abs.setDepth(i);
                    long time = System.currentTimeMillis();
                    abs.go();
                    time = System.currentTimeMillis() - time;
                    System.out.println("info depth " + i 
                                     + " bestmove " + abs.bestMove() 
                                     + " score " + score(b, abs.bestScore())
                                     + " time " + time + " ms");
                }
                checkNull(abs.bestMove(), "bestmove");
                b = abs.bestMove().execute(b);
    //            abs = new AlphaBetaSearcher(b, ev);
                System.out.println();
            }
        }
    }
    
    private static int score(Board b, int score) {
        return (b.isWhiteAtMove() ? 1 : -1) * (int) Math.abs(score);
    }
    
    private static final int MOVES_PER_POS = 30;
    private static final int DEPTH_LIMIT = 3;
}
