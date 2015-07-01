package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class EnPassant extends Move {
    
    /**
    * Standardkonstruktor.
    */ 
    public EnPassant(final Piece piece, final Position targetPosition) {
        super(piece, targetPosition); 
    }

    public boolean isPossible(Board context) {
        if (!super.isPossible(context))
            return false;

        if (!getTarget().equals(context.enPassant()))
            return false;

        return true;
    }

    public Board execute(Board context) {
        Board result = super.execute(context);
        if (result != null) {
            int inc = (getPiece().isWhite() ? -1 : 1);
            Position enemyPawnPosition = getTarget().transpose(0, inc);
            result.removePieceAt(enemyPawnPosition);
            result.setEnPassant(Position.INVALID);
        }
        return result;
    }

    public String toSAN(Board context) {
        String san = getPiece().getPosition().toSAN();
        san += "X";
        san += getTarget().toSAN();
        return san;
    }

}
