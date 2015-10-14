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
 * Mobilitätsbewertung.
 * 
 * Die Mobilität eines Spielers ergibt sich aus der Anzahl der Züge, die er
 * durchführen kann. Da die Erzeugung der Zugliste 
 * @author Johannes Haupt
 */
public class MobilityEvaluator extends EvaluationFunction {
    
    private static final int ATTACK_WEIGHT = -5;
    private static final int IN_CHECK_WEIGHT = -30;
    
    public MobilityEvaluator() {
        
    }
    
    public int evaluate(final Board board) {
        boolean isWhiteAtMove = board.isWhiteAtMove();
        int score = 0;
        if (BitBoardUtils.isInCheck(board))
            score += IN_CHECK_WEIGHT;
        long myAttacks = board.getAttackedBits(
            board.getPieceBitsForColor(!isWhiteAtMove), 
            isWhiteAtMove
        );
        long theirAttacks = board.getAttackedBits(
            board.getPieceBitsForColor(isWhiteAtMove), 
            !isWhiteAtMove
        );
        int myAttackedPieceCount = Bitwise.count(theirAttacks);
        int theirAttackedPieceCount = Bitwise.count(myAttacks);
        score += (myAttackedPieceCount - theirAttackedPieceCount) * ATTACK_WEIGHT;
        return score;
    }
    
    @Override
    public boolean isAbsolute() {
        return false;
    }
}
