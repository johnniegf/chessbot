/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine;

import de.htwsaar.chessbot.engine.model.Board;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public interface MoveSearcher {

    Board getBoard();
    
    void setBoard(final Board board);
    
    void stop();
    
    void go();
    
    void ponderHit();
    
    boolean isPonderingEnabled();
    
    void setPondering(final boolean ponderingEnabled);
    
}
