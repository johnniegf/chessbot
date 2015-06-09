package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Läufer.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Bishop extends Piece {
    
    /**
    *
    */ 
    public Bishop(Position position) {
        super(position);
    }

    public Bishop(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Collection<Position> getValidMoves(Board context) {
        return new ArrayList<Position>();
    }

    public String getName() {
        return "Läufer";
    }

    public String getShortName() {
        return "B";
    }
}
