package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Beschreibung.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class King extends Piece {
    
    /**
    *
    */ 
    public King(Position position) {
        super(position);
    }

    public King(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public King(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite, hasMoved);
    }

    public Collection<Position> getValidMoves(Board context) {
        return new ArrayList<Position>();
    }

    public String getName() {
        return "König";
    }

    public String getShortName() {
        return "K";
    }
}
