package de.htwsaar.chessbot.engine.model;

public class MoveEnPassant extends Move {

	public static final char FLAG = 'E';

    public MoveEnPassant() {

    }

	public MoveEnPassant(final Position startPosition, final Position targetPosition) {
		super(startPosition, targetPosition);
		
		
		// Zeile 4: Zug von schwarz
		if ( getStart().rank() == 4 ) {
			if (getTarget().rank() != 3)
				throw new MoveException();
		} else if ( getStart().rank() == 5 ) {
			if (getTarget().rank() != 6)
				throw new MoveException();
		} else {
			throw new MoveException();
		}
		if ( Math.abs(getTarget().file() - getStart().file()) != 1 )
			throw new MoveException();
	}

	@Override
	public Board tryExecute(Board onBoard) {

        
        int direction = getStart().compareTo(getTarget()) < 0 ? -1 : 1;
        if (onBoard.isFree(getStart())) return null;
        if (!getTarget().equals(onBoard.getEnPassant())) return null;
        boolean isWhite = onBoard.getPieceAt(getStart()).isWhite();
        if(direction > 0 && isWhite ) return null;
        if(direction < 0 && !isWhite ) return null;
        
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
