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
        Collection<Position> validPositions = new ArrayList<Position>(8);
        Position p = getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if ( x == 0 && y == 0 )
                    continue;

                try {
                    validPositions.add( p.translate(x,y) );
                } catch (RuntimeException rte) {}
            }
        }
        if ( !hasMoved() ) {
            validPositions.add( p.translate(0,2) );
            validPositions.add( p.translate(0,-2) );
        }

        return validPositions; 
    }

    public String getName() {
        return "König";
    }

    public String getShortName() {
        return "K";
    }

    public King clone() {
        return new King(getPosition().clone(), isWhite(), hasMoved());
    }
}
