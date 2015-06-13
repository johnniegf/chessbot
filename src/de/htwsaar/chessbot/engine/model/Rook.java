package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Turm.
*
* <ul>
* <li>Ein Turm darf auf Linien und Reihen, also horizontal und vertikal, 
* beliebig weit ziehen, ohne jedoch über andere Figuren zu springen. 
* Die einzige Ausnahme davon ist die Rochade, bei der Turm und König 
* bewegt werden. Ein Turm hat, wie Dame und Läufer, eine nur durch 
* den Spielfeldrand begrenzte Reichweite.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public final class Rook extends Piece {

    public Rook() {
        super();
    }

    public Rook(final Position position) {
        super(position);
    }

    public Rook(final Position position, 
                final boolean isWhite) 
    {
        super(position, isWhite);
    }

    public Rook(final Position position, 
                final boolean isWhite, 
                final boolean hasMoved) 
    {
        super(position, isWhite, hasMoved);
    }

    public final Collection<Position> getValidMoves(final Board context) {
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

    public final String getName() {
        return "Turm";
    }

    public final String getShortName() {
        return "R";
    }

    public final Rook clone() {
        return new Rook(getPosition().clone(), isWhite(), hasMoved());
    }
}

