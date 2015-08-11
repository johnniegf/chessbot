package de.htwsaar.chessbot.engine.model;

/**
*Klasse zur Bauernumwandlung. 
*
*@author Dominik Becker
*/
public class MovePromotion extends Move {

	public static final char TO_QUEEN  = 'Q';
	public static final char TO_ROOK   = 'R';
	public static final char TO_KNIGHT = 'N';
	public static final char TO_BISHOP = 'B';

    private static final String EXN_INVALID_START = 
        "Ungültiges Startfeld, muss in 2. oder 7. Reihe liegen";

    private static final String EXN_INVALID_TARGET =
        "Ungültiges Zielfeld, muss für Bauer auf Startfeld erreichbar sein.";
    private static final String EXN_INVALID_CONV_TARGET =
        "kann nicht zu Bauer oder Koenig umgewandelt werden";


	private Piece promoted;

    public MovePromotion(final Piece promoted) {
        this.promoted = promoted;
    }

	public MovePromotion(final Position start, 
                         final Position target, 
                         Piece promoted){
		if(promoted instanceof King || promoted instanceof Pawn) {
			throw new MoveException(EXN_INVALID_CONV_TARGET);
		}

		setStart(start);
        setTarget(target);
		this.promoted = promoted;

	}

    public void setStart(final Position start) {
        if (start == null || !start.isValid())
            throw new MoveException(EXN_INVALID_START);

        if (start.rank() == 7 || start.rank() == 2) {
            super.setStart(start);
        } else {
            throw new MoveException(EXN_INVALID_START);
        }
    }

    public void setTarget(final Position target) {
        if (target == null || !target.isValid()) 
            throw new MoveException(EXN_INVALID_TARGET);

        if ( Math.abs(target.rank() - getStart().rank()) != 1 )
            throw new MoveException(EXN_INVALID_TARGET);
        if ( Math.abs(target.file() - getStart().file()) > 1)
            throw new MoveException(EXN_INVALID_TARGET);
    
        super.setTarget(target);
    }

	
    @Override
	protected Board tryExecute(Board onBoard) {

    	if(!(onBoard.getPieceAt(getStart())instanceof Pawn)) {
    		return null;
    	}
        Board target = super.tryExecute(onBoard);
        if (target != null) {
        	Piece pawn = target.getPieceAt(getTarget());
        	target.removePieceAt(getTarget());

        	Piece movedPiece = this.promoted;
        	movedPiece.setPosition(getTarget());
        	movedPiece.setIsWhite(pawn.isWhite());
        	movedPiece.setHasMoved(true);
        	target.putPiece(movedPiece);
        }
        return target;
    }
    
    public String toString() {
    	return super.toString()+Character.toLowerCase(promoted.fenShort());
    }

    public final char flag() {
    	return Character.toUpperCase(promoted.fenShort());
    }

    protected final Move create() {
        return new MovePromotion(promoted);
    }
}
