package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.util.Unused;
/**
* Doppelzug eines Bauern von seinem Startfeld.
*
* @author Johannes Haupt
*/
public class DoublePawnMove extends Move {
    
    public static final byte TYPE = 9;

    public byte type() {
        return TYPE;
    }
    
    public DoublePawnMove(final Position startingSquare) {
        super(Position.INVALID, Position.INVALID);
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

    public void setTarget(@Unused final Position unused) {
        // We do nothing here, because target is derived from start
    }

    public Board tryExecute(final Board onBoard) {
        if (!onBoard.isFree(getTarget()) )
            return null;
        
        Piece pc = onBoard.getPieceAt(getStart());
        if (pc == null)
            return null;
        if ( pc.id() != Pawn.ID ) 
            return null;

        Board result = onBoard.clone();
        movePiece(result, pc);
        long epSquare = getTarget().toLong();
        if (pc.isWhite())
            epSquare >>= 8;
        else
            epSquare <<= 8;
        result.setEnPassant(epSquare);
        return result;
    }

}
