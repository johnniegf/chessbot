package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import static de.htwsaar.chessbot.util.Exceptions.*;

/**
* En passant-Sonderzug des Bauern.
*
* @author Dominik Becker
* @author Johannes Haupt
*/
public class EnPassantMove extends Move {
    
    public static final byte TYPE = 10;
    

    private static final String EXN_INVALID_START = 
        "Ungültiges Startfeld für en passant Zug %s";

    private static final String EXN_INVALID_TARGET =
        "Ungültiges Zielfeld für en passant Zug %s";

    public byte type() {
        return TYPE;
    }

	public EnPassantMove(final Position startPosition, 
                         final Position targetPosition) 
    {
        super(startPosition, targetPosition);
        
        
	}

    public void setStart(final Position start) {
        if (start == null || !start.isValid()) 
            throw new MoveException( 
                msg(EXN_INVALID_START, start)
            );
        if (start.rank() != 4 && start.rank() != 5)
            throw new MoveException( 
                msg(EXN_INVALID_START, start)
            );

        super.setStart(start);
    }

    public void setTarget(final Position target) {
        if (target == null || !target.isValid()) 
            throw new MoveException(EXN_INVALID_START);
        
        int direction = target.rank() - getStart().rank();
        switch(direction) {
            case -1:
                if (getStart().rank() != 4)
                    throw new MoveException(EXN_INVALID_TARGET);
                break;

            case 1:
                if (getStart().rank() != 5)
                    throw new MoveException(EXN_INVALID_TARGET);
                break;

            default:
                throw new MoveException(EXN_INVALID_TARGET);
        }

        if ( Math.abs(getStart().file() - target.file()) != 1 )
            throw new MoveException(EXN_INVALID_TARGET);

        super.setTarget(target);
    }

	@Override
	public Board tryExecute(final Board onBoard) {
        checkNull(onBoard);
        
        Board result = onBoard.clone();
        if (result != null) {
            int direction = getStart().compareTo(getTarget()) < 0 ? -1 : 1;
            if (getTarget() != onBoard.getEnPassant()) 
                return null;
            boolean isWhite = result.getPieceAt(getTarget()).isWhite();
            if(direction > 0 == isWhite ) 
                return null;
         
            Position tp = getTarget().transpose(0, direction);
            Piece attackedPiece = result.getPieceAt(tp);
            if(attackedPiece == null) {
            	throw new MoveException();
            }
        
            result.removePieceAt(tp);
        }
        return result;
	}

}
