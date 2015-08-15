package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.P;
/**
* Rochade.
*
* @author Johannes Haupt
*/
public class Castling extends Move {

    public static final char FLAG = 'C';

    private static final Position e1 = P("e1");
    private static final Position e8 = P("e8");

    private static final String EXN_INVALID_START =
        "Das Startfeld der Rochade muss e1 oder e8 sein!";
    
    private static final String EXN_INVALID_TARGET =
        "Das Zielfeld der Rochade muss c1/8 oder g1/8 sein!";

    /**
    * Standardkonstruktor.
    */ 
    public Castling() {

    }

    public Castling(final Position start, final Position target) {
        setStart(start);
        setTarget(target);
    }

    public void setStart(final Position start) {
        if (start != P("e1") && start != P("e8"))
            throw new MoveException(EXN_INVALID_START);

        super.setStart(start);
    }
    
    public void setTarget(final Position target) {
        if ( target == null || !target.isValid() )
            throw new MoveException(EXN_INVALID_TARGET);

        if ( target.rank() != getStart().rank() )
            throw new MoveException(EXN_INVALID_TARGET);

        if ( Math.abs(target.file() - getStart().file()) != 2 )
            throw new MoveException(EXN_INVALID_TARGET);

        super.setTarget(target);   
    }

    @Override
    protected Board tryExecute(final Board onBoard) {
        Board result = super.tryExecute(onBoard);
        if (result != null) {
            Piece r = getRook(result);
            if ( r == null )
                return null;
            if ( !(result.getPieceAt(getTarget()) instanceof King) )
                return null;
            int direction = getStart().compareTo(getTarget()) < 0 ? -1 : 1;
            Position pr = r.getPosition().transpose(direction,0);
            while(pr != getTarget()) {
                if ( !result.isFree(pr) ) {
                    return null;
                }
                pr = pr.transpose(direction,0);
            }
            Piece rm = r.move( getTarget().transpose(direction,0) );
            result.removePieceAt(r.getPosition());
            result.putPiece(rm);
        }
        return result;
    }
    
    private Piece getRook(final Board context) {
        Position kingPos = getTarget();
        int direction = getStart().compareTo(getTarget());
        Position rookPos;
        
        if (direction > 0) 
            rookPos = P(1,kingPos.rank());
        else
            rookPos = P(8,kingPos.rank()); 

        Piece k = context.getPieceAt(kingPos);
        Piece r = context.getPieceAt(rookPos);
        if (!(r instanceof Rook))
            return null;
        if (r.hasMoved() || r.isWhite() != k.isWhite())
            return null;

        return r;
    }

    public final char flag() {
    	return FLAG;
    }

    protected final Move create() {
        return new Castling();
    }
    
}
