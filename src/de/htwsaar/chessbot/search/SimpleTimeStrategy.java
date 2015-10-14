/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.Clock;
import de.htwsaar.chessbot.core.Game;

/**
 * Simples Zeitmanagement.
 * @author Johannes Haupt
 */
public class SimpleTimeStrategy implements TimeStrategy {
    private static final long TEN_MINUTES = 10 * MINUTE;
    private static final long TWENTY_MINUTES = 20 * MINUTE;
    private static final double OPENING_INCREMENT = 1.25;

    @Override
    public long getMoveTime(Game gameState) {
        Clock cl = gameState.getClock();
        Board currentPosition = gameState.getCurrentBoard();
        boolean isWhiteAtMove = currentPosition.isWhiteAtMove();
        int movenum = currentPosition.getFullMoves();
        long mytime = (isWhiteAtMove ? cl.wtime : cl.btime);
        long myinc  = (isWhiteAtMove ? cl.winc  : cl.binc);
        long myMoveTime = 0;
        int movestogo = cl.movestogo;
        if (movestogo == 0) {
            movestogo = 30;
            if (mytime > TEN_MINUTES)
                movestogo += 20;
            if (mytime > TWENTY_MINUTES)
                movestogo += 10;
        }
        myMoveTime = mytime / movestogo + myinc;
        if ( movenum < 5 )
            myMoveTime *= OPENING_INCREMENT;
        return myMoveTime;
    }
    
}
