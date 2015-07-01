package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class DoublePawnMove extends Move {
    
    /**
    * Standardkonstruktor.
    */ 
    public DoublePawnMove(final Piece pawn, final Position targetPosition) {
        super(pawn, targetPosition);
    }

    public boolean isPossible(Board context) {
        return super.isPossible(context) && getPiece() instanceof Pawn;
    }

    public Board execute(final Board onBoard) {
        Board result = super.execute(onBoard);
        if (result != null) {
            int inc = getPiece().isWhite() ? -1 : 1;
            Position enPassant = getTarget().transpose(0, inc);
            result.setEnPassant(enPassant);
        }
        return result;
    }

}
