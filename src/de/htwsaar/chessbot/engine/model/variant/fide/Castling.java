package de.htwsaar.chessbot.engine.model.variant.fide;

import static de.htwsaar.chessbot.engine.model.Position.P;
import de.htwsaar.chessbot.engine.model.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Castling extends Move {

    public static final char FLAG = 'C';

    /**
    * Standardkonstruktor.
    */ 
    public Castling() {

    }

    public Castling(final Position start, final Position target) {
        super(start, target);
    }

    public Board tryExecute(final Board onBoard) {
        Board result = super.tryExecute(onBoard);
        if (result != null) {
            Rook r = getRook(result);
            if ( r == null )
                return null;
            if ( !(result.getPieceAt(getTarget()) instanceof King) )
                return null;
            int direction = getStart().compareTo(getTarget());
            Piece rm = r.move(getTarget().transpose(direction,0));
            result.removePieceAt(r.getPosition());
            result.putPiece(rm);
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

        Piece k = context.getPieceAt(kingPos);
        Piece r = context.getPieceAt(rookPos);
        if (r == null || !(r instanceof Rook))
            return null;
        if (r.hasMoved() || r.isWhite() != k.isWhite())
            return null;

        return (Rook) r;
    }

    public final char flag() {
    	return FLAG;
    }
    
}
