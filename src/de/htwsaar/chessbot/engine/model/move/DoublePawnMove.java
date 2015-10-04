package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BoardUtils.North;
import static de.htwsaar.chessbot.engine.model.BoardUtils.South;
import static de.htwsaar.chessbot.engine.model.BoardUtils.shift;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import de.htwsaar.chessbot.util.Unused;
/**
* Doppelzug eines Bauern von seinem Startfeld.
*
* @author Johannes Haupt
*/
public class DoublePawnMove extends Move {
    
    public static final byte TYPE = 9;
    
    private final Move mMove;

    public byte type() {
        return TYPE;
    }
    
    public DoublePawnMove(final Position startingSquare) {
        super(startingSquare, Position.INVALID);
        mMove = Move.MV(getStart(), getTarget());
    }

    public void setStart(final Position start) {
        checkNull(start, "start");
        int d;
        if (start.rank() == 2) {
        	d = 1;
        } else if (start.rank() == 7) {
            d = -1;
        } else {
            throw new MoveException("Invalid move!" + start);
        }
        super.setStart(start);
        super.setTarget(start.transpose(0,d*2));
    }

    public void setTarget(@Unused final Position unused) {
        // We do nothing here, because target is derived from start
    }

    public Board tryExecute(final Board onBoard) {
        Piece pc = onBoard.getPieceAt(getStart());
        if ( pc == null || pc.id() != Pawn.ID ) 
            return null;

        Board result = mMove.tryExecute(onBoard);
        if (result != null) {
            result.setEnPassant(getTarget().transpose(0, pc.isWhite() ? -1 : 1));
        }
        return result;
    }

}
