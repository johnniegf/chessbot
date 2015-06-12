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
        Collection<Position> possibleMoves = new ArrayList<Position>(14);
        Position p = getPosition();
        
        for (int i = 1; i <= 8; i++) {  
            if ( i != getPosition().getColumn() ) 
            {
                possibleMoves.add( p.setColumn(i) );
            }
            if ( i != getPosition().getRow() ) 
            {
                possibleMoves.add( p.setRow(i) );
            }
        }
        return possibleMoves;
    }

    public String getName() {
        return "Turm";
    }

    public String getShortName() {
        return "R";
    }

    public Rook clone() {
        return new Rook(getPosition().clone(), isWhite(), hasMoved());
    }
}
