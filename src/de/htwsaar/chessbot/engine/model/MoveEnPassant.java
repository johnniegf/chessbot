package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.util.Exceptions.*;

public class MoveEnPassant extends Move {

	public static final char FLAG = 'E';

    private static final String EXN_INVALID_START = 
        "Ungültiges Startfeld für en passant Zug %s";

    private static final String EXN_INVALID_TARGET =
        "Ungültiges Zielfeld für en passant Zug %s";

    public MoveEnPassant() {

    }

	public MoveEnPassant(final Position startPosition, 
                         final Position targetPosition) 
    {
        setStart(startPosition);
        setTarget(targetPosition);
        
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
        
        Board target = super.tryExecute(onBoard);
        if (target != null) {
            int direction = getStart().compareTo(getTarget()) < 0 ? -1 : 1;
            if (getTarget() != onBoard.getEnPassant()) 
                return null;
            boolean isWhite = target.getPieceAt(getTarget()).isWhite();
            if(direction > 0 == isWhite ) 
                return null;
         
            Position tp = getTarget().transpose(0, direction);
            Piece attackedPiece = target.getPieceAt(tp);
            if(attackedPiece == null) {
            	throw new MoveException();
            }
        
            target.removePieceAt(tp);
        } else {
        }
        return target;
	}
	
	public final char flag() {
		return FLAG;
	}

    protected final Move create() {
        return new MoveEnPassant();
    }

}
