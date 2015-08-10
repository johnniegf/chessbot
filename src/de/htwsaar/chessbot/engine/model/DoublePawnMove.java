package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class DoublePawnMove extends Move {
    
    public static final char FLAG = 'D';

    /**
    * Standardkonstruktor.
    */ 
    public DoublePawnMove() {

    }

    public DoublePawnMove(final Position startingSquare, 
                          final Position targetSquare) 
    {
        setStart(startingSquare);
    }

    public void setStart(final Position start) {
        if (start == null)
            throw new NullPointerException("start");
        int d;
        if (start.rank() == 2) {
        	d = 1;
        } else if (start.rank() == 7) {
            d = -1;
        } else {
            throw new MoveException("Invalid move!");
        }
        super.setStart(start);
        super.setTarget(start.transpose(0,d*2));
    }

    public void setTarget(final Position unused) {
        // We do nothing here, because target is derived from start
    }

    public Board tryExecute(final Board onBoard) {
        Piece pc = onBoard.getPieceAt(getStart());
        if ( !(pc instanceof Pawn) ) 
            return null;
        if ( pc.hasMoved() )
            return null;
    
        Board result = super.tryExecute(onBoard);
        if (result != null) {
            int inc = result.getPieceAt(getTarget()).isWhite() ? -1 : 1;
            Position enPassant = getTarget().transpose(0, inc);
            result.setEnPassant(enPassant);
        }
        return result;
    }

    @Override
    protected char flag() {
        return FLAG;
    }

    protected final Move create() {
        return new DoublePawnMove();
    }

}
