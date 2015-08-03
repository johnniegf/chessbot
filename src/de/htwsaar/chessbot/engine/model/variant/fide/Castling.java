package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Castling extends Move {

    public static final char flag = 'C';

    /**
    * Standardkonstruktor.
    */ 
    public Castling() {

    }

    public Castling(final Position start, final Position target) {
        super(king, target);
    }

    public boolean isPossible(final Board context) {
        if ( !super.isPossible(context) )
            return false;

        Rook r = getRook(context);
        if (r == null || r.hasMoved())
            return false;
        
        if (getPiece().hasMoved())
            return false;

        return true;
    }

    public Board tryExecute(final Board onBoard) {
        Board result = super.execute(onBoard);
        if (result != null) {
            Rook r = getRook(result);
            int direction = getPiece().getPosition().compareTo(getTarget());
            Piece rm = r.move(getTarget().transpose(direction,0));
            result.removePiece(r);
            result.addPiece(rm);
        }
        return result;
    }

    private Rook getRook(final Board context) {
            Position kingPos = getStart();
            int direction = getTarget().compareTo(kingPos);
            Position rookPos;
            
            if (direction < 0) 
                rookPos = P(1,kingPos.rank());
            else
                rookPos = P(8,kingPos.rank()); 
            
            Piece r = context.getPieceAt(rookPos);
            if (r == null || !(r instanceof Rook))
                return null;
    }

}
