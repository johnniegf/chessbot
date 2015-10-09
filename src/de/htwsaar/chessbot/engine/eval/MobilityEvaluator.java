/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class MobilityEvaluator extends EvaluationFunction {
    
    public MobilityEvaluator() {
        
    }
    
    public int evaluate(final Board board) {
        int myMoves = board.getResultingPositions().length;
        Board alt = board.clone();
        alt.togglePlayer();
        int theirMoves = alt.getResultingPositions().length;
//        return myMoves / theirMoves;
        return 1;
    }
}
