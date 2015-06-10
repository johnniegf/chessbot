package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Beschreibung.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Queen extends Piece {
    
    /**
    *
    */ 
    public Queen(Position position) {
        super(position);
    }

    public Queen(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Collection<Position> getValidMoves(Board context) {
        return new ArrayList<Position>();
    }

    public String getName() {
        return "Dame";
    }

    public String getShortName() {
        return "Q";
    }

    public Queen clone() {
        return new Queen(getPosition().clone(), isWhite());
    }
}
