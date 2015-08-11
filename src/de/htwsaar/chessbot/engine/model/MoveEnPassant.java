package de.htwsaar.chessbot.engine.model;

public class MoveEnPassant extends Move {

	public static final char FLAG = 'E';

    private static final String EXN_INVALID_START = 
        "";

    private static final String EXN_INVALID_TARGET =
        "";

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
            throw new MoveException(EXN_INVALID_START);
        if (start.rank() != 4 && start.rank() != 5)
            throw new MoveException(EXN_INVALID_START);

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

        
        int direction = getStart().compareTo(getTarget()) < 0 ? -1 : 1;
        if (onBoard.isFree(getStart())) return null;
        if (!getTarget().equals(onBoard.getEnPassant())) return null;
        boolean isWhite = onBoard.getPieceAt(getStart()).isWhite();
        if(direction > 0 != isWhite ) return null;
        
        Position tp = getTarget().transpose(0, direction);
        Piece attackedPiece = onBoard.getPieceAt(tp);
        if(attackedPiece == null) {
        	throw new MoveException();
        }
        
        Board target = super.tryExecute(onBoard);
        target.removePieceAt(tp);
       
        return target;
	}
	
	public final char flag() {
		return FLAG;
	}

    protected final Move create() {
        return new MoveEnPassant();
    }

}
