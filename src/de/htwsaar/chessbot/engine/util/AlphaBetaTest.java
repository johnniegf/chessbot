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

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class AlphaBetaTest {
    
    public static void main(String[] args) {
        Board b = Board.B();
        EvaluationFunction ev = new Evaluator();
        AlphaBetaSearcher abs = new AlphaBetaSearcher(b,ev);
        abs.setDepth(5);
        
        
        
    }
    
}
