package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Die Dame.
*
* <ul>
* <li>Die Dame darf in horizontaler, vertikaler und diagonaler Richtung 
* beliebig weit ziehen, ohne jedoch über andere Figuren zu springen. Sie 
* vereint somit die Zugmöglichkeiten eines Turms und eines Läufers in sich.
* </li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Queen extends Piece {
   
    public Queen() {
        super();
    }

    public Queen(Position position) {
        super(position);
    }

    public Queen(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Collection<Position> getValidMoves(Board context) {
        Collection<Position> result = new ArrayList<Position>();
        Bishop b = new Bishop(getPosition(), isWhite());
        Rook   r = new Rook(getPosition(), isWhite(), true);
        result.addAll(b.getValidMoves(context));
        result.addAll(r.getValidMoves(context));
        return result;
    }

    public String getName() {
        return "Dame";
    }

    public String getShortName() {
        return "Q";
    }
    
    public boolean equals(Object other) {
        if (other == null) 
            return false;
        if (other == this)
            return true;

        try {
            Queen q = (Queen) other;
            return q.getPosition().equals(getPosition())
                && q.isWhite() == isWhite();
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public Queen clone() {
        return new Queen(getPosition().clone(), isWhite());
    }
}
