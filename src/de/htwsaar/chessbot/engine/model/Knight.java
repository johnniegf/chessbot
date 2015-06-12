package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Springer.
*
* Der Springer darf sich auf alle Felder bewegen, die ihm am nächsten sind
* aber nicht auf einer Zeile, Spalte oder Diagonalen, die sich in dem von
* ihm besetzten Feld kreuzen.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Knight extends Piece {
    
    /**
    * Erzeuge einen neuen weißen Springer an übergebener Position.
    */ 
    public Knight(Position position) {
        super(position);
    }

    /**
    * Erzeuge einen neuen Springer an übergebener Position.
    */ 
    public Knight(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Collection<Position> getValidMoves(Board context) {
        Collection<Position> possibleMoves = new ArrayList<Position>(8);
        Position pt, p = getPosition();
        for (int d = -2; d <= 2; d += 4) {
            for (int e = -1; e <= 1; e += 2) {
                pt = p.translate(d,e);
                if (pt.existsOn(context)) possibleMoves.add( pt );
                pt = p.translate(e,d);
                if (pt.existsOn(context)) possibleMoves.add( pt );
            }
        }
        return possibleMoves; 
    }

    public String getName() {
        return "Springer";
    }

    public String getShortName() {
        return "N";
    }

    public Knight clone() {
        return new Knight(getPosition().clone(), isWhite());
    }
}
