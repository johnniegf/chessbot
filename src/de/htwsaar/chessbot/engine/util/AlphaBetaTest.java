/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.AlphaBetaSearcher;
import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.eval.Evaluator;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardUtils;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class AlphaBetaTest {
    
    public static void main(String[] args) {
//        Board b = Board.B("8/8/8/8/2P1BP2/P1K1B3/4k3/8 w - - 69 93");
//        Board b = Board.B("8/8/7Q/8/7q/P2B3k/2PB1P2/5K2 w - - 5 43");
//        Board b = Board.B("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        Board b = Board.B();
        EvaluationFunction ev = new Evaluator();
        AlphaBetaSearcher abs = null;
        while (true) {
            System.out.println("-----------------------------------------------");
            System.out.println(b);
            if (BoardUtils.isMate(b)) {
                System.out.println("Mate");
                return;
            }
            if (BoardUtils.isStalemate(b)) {
                System.out.println("Stalemate");
                return;
            }
                
            for (int i = DEPTH_LIMIT; i <= DEPTH_LIMIT; i++) {
                abs = new AlphaBetaSearcher(b,ev);
                abs.setDepth(i);
                long time = System.currentTimeMillis();
                abs.go();
                time = System.currentTimeMillis() - time;
                System.out.println("info depth " + i 
                                 + " bestmove " + abs.bestMove() 
                                 + " score " + score(b, abs.bestScore())
                                 + " time " + time + " ms");
            }
            b = abs.bestMove().execute(b);
            System.out.println();
        }
    }
    
    private static int score(Board b, int score) {
        return (b.isWhiteAtMove() ? 1 : -1) * (int) Math.abs(score);
    }
    private static final int DEPTH_LIMIT = 5;
    
}
