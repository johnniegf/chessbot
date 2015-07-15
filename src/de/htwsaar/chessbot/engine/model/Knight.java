package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Springer.
*
* <ul>
* <li>Der Springer darf auf eines der Felder ziehen, die seinem Standfeld am 
* nächsten, aber nicht auf gleicher Reihe, Linie oder Diagonale mit 
* diesem liegen.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public final class Knight extends Piece {

    public Knight() {
        super();
    }

    public Knight(Position position) {
        super(position);
    }

    public Knight(final Position position, 
                  final boolean isWhite) 
    {
        super(position, isWhite);
    }
    
    public Knight(final Position position, 
                  final boolean isWhite,
                  final boolean hasMoved) 
    {
        super(position, isWhite);
    }

    public final Collection<Move> getValidMoves(final Board context) {
        Collection<Move> possibleMoves = new ArrayList<Move>();
        Position p = getPosition();
        for (int d = -2; d <= 2; d += 4) {
            for (int e = -1; e <= 1; e += 2) {
                for ( Position pn : new Position[] {p.transpose(d,e), p.transpose(e,d)} )
                    if (pn.existsOn(context)) 
                        if (context.isFree(pn) || 
                            context.pieceAt(pn).isWhite() != isWhite() )
                        {
                            possibleMoves.add(new Move(this, pn));
                        }
            }
        }
        return possibleMoves; 
    }

    public final String getName() {
        return "Springer";
    }

    public final String toSAN() {
        return "N";
    }
    
    public boolean equals(final Object other) {
        if (other == null) 
            return false;
        if (other == this)
            return true;

        try {
            Knight k = (Knight) other;
            return k.getPosition().equals(getPosition())
                && k.isWhite() == isWhite();
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public final Knight clone() {
        return new Knight(getPosition().clone(), isWhite());
    }
}

