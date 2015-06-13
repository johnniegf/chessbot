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
    
    public Bishop() {
        super();
    }

    public Bishop(Position position) {
        super(position);
    }

    public Bishop(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Collection<Position> getValidMoves(Board context) {
        Collection<Position> result =  new ArrayList<Position>();
        Position p; 
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                int c = 1;
                p = getPosition().translate(c*i, c*j);
                while(p.existsOn(context)) {
                    result.add(p);
                    c += 1;
                    p = getPosition().translate(c*i, c*j);
               } 
            }
        }
        return result;
    }

    public String getName() {
        return "Läufer";
    }

    public String getShortName() {
        return "B";
    }

    public boolean equals(Object other) {
        if (other == null) 
            return false;
        if (other == this)
            return true;

        try {
            Bishop b = (Bishop) other;
            return b.getPosition().equals(getPosition())
                && b.isWhite() == isWhite();
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public Bishop clone() {
        return new Bishop(getPosition().clone(), isWhite());
    }
}
