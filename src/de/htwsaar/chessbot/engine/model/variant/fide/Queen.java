package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.Piece;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.Board;

import java.util.Collection;
import java.util.ArrayList;

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
public final class Queen extends Piece {
   
    public Queen() {
        super();
    }

    public Queen(final Position position) {
        super(position);
    }

    public Queen(final Position position, 
                 final boolean isWhite) 
    {
        super(position, isWhite);
    }

    public Queen(final Position position, 
                 final boolean isWhite,
                 final boolean hasMoved) 
    {
        super(position, isWhite);
    }

    public final Collection<Position> getValidMoves(final Board context) {
        Collection<Position> result = new ArrayList<Position>();
        Bishop b = new Bishop(getPosition(), isWhite());
        Rook   r = new Rook(getPosition(), isWhite(), true);
        result.addAll(b.getValidMoves(context));
        result.addAll(r.getValidMoves(context));
        return result;
    }

    public final String getName() {
        return "Dame";
    }

    public final String toSAN() {
        return "Q";
    }
    
    public boolean equals(final Object other) {
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

    public final Queen clone() {
        return new Queen(getPosition().clone(), isWhite());
    }
}
