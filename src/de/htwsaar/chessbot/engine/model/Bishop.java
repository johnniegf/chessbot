package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Läufer.
*
* Der Läufer kann sich über beide Diagonalen, die sich in dem von ihm
* besetzten Schachfeld kreuzen bewegen, dabei aber keine Figuren überspringen.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Bishop extends Piece {
    
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

    public Bishop clone() {
        return new Bishop(getPosition().clone(), isWhite());
    }
}
