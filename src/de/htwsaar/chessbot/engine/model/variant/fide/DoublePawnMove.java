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
        if (start.rank() == 2) {
            super.setStart(start);
            super.setTarget(start.transpose(0,2));
        } else if (start.rank() == 7) {
            super.setStart(start);
            super.setTarget(start.transpose(0,-2));
        } else {
            throw new MoveException("Invalid move!");
        }
    }

    public void setTarget(final Position unused) {
        // We do nothing here, because target is derived from start
    }

    public boolean isPossible(final Board context) {
        
        Piece pc = context.getPieceAt(getStart());
        if ( !(pc instanceof Pawn) ) 
            return false;
        if ( pc.hasMoved() )
            return false;
        return super.isPossible(context);
    }

    public Board tryExecute(final Board onBoard) {
        Board result = super.tryExecute(onBoard);
        if (result != null) {
            int inc = result.getPieceAt(getTarget()).isWhite() ? -1 : 1;
            Position enPassant = getTarget().transpose(0, inc);
            result.setEnPassant(enPassant);
        }
        return result;
    }

}
