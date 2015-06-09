package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Beschreibung.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Knight extends Piece {
    
    /**
    *
    */ 
    public Knight(Position position) {
        super(position);
    }

    public Knight(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Collection<Position> getValidMoves(Board context) {
        return new ArrayList<Position>();
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }
}
