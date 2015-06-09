package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Beschreibung.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Rook extends Piece {
    
    /**
    *
    */ 
    public Rook(Position position) {
        super(position);
    }

    public Rook(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Rook(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite, hasMoved);
    }

    public Collection<Position> getValidMoves(Board context) {
        return new ArrayList<Position>();
    }

    public String getName() {
        return "Turm";
    }

    public String getShortName() {
        return "R";
    }
}
