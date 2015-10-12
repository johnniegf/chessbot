/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.util.Bitwise;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class MobilityEvaluator extends EvaluationFunction {
    
    private static final int MOBILITY_WEIGHT = 5;
    private static final int ATTACK_WEIGHT = -10;
    
    public MobilityEvaluator() {
        
    }
    
    public int evaluate(final Board board) {
        boolean isWhiteAtMove = board.isWhiteAtMove();
        int score = 0;
//        int myMoves = board.getResultingPositions().length;
        int attacks = Bitwise.count(
            board.getAttacked(board.getPieceBitsForColor(!isWhiteAtMove), isWhiteAtMove)
        );
        int attacked = Bitwise.count(
            board.getAttacked(board.getPieceBitsForColor(isWhiteAtMove), !isWhiteAtMove)
        );
        score = (attacks - attacked) * ATTACK_WEIGHT;
        return (isWhiteAtMove ? 1 : -1) * score;
    }
}
