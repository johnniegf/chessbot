/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.core.BitBoardUtils;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.util.Bitwise;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class MobilityEvaluator extends EvaluationFunction {
    
    private static final int MOBILITY_WEIGHT = 5;
    private static final int ATTACK_WEIGHT = -10;
    private static final int IN_CHECK_WEIGHT = -30;
    
    public MobilityEvaluator() {
        
    }
    
    public int evaluate(final Board board) {
        boolean isWhiteAtMove = board.isWhiteAtMove();
        int score = 0;
        if (BitBoardUtils.isInCheck(board))
            score += IN_CHECK_WEIGHT;
//        int myMoves = board.getResultingPositions().length;
        long myAttacks = board.getAttacked(
            board.getPieceBitsForColor(!isWhiteAtMove), 
            isWhiteAtMove
        );
        long theirAttacks = board.getAttacked(
            board.getPieceBitsForColor(isWhiteAtMove), 
            !isWhiteAtMove
        );
        int myAttackedPieceCount = Bitwise.count(myAttacks);
        int theirAttackedPieceCount = Bitwise.count(theirAttacks);
        score = (myAttackedPieceCount - theirAttackedPieceCount) * ATTACK_WEIGHT;
        return score;
    }
}
