package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Move;
import de.htwsaar.chessbot.engine.model.MoveException;
import de.htwsaar.chessbot.engine.model.Piece;
import de.htwsaar.chessbot.engine.model.Position;

/**
*Klasse zur Bauernumwandlung. 
*
*@author Dominik Becker
*/
public class MovePromotion extends Move {

	public static final char FLAG = 'P';
	
	private Piece promoted;

    public MovePromotion(final Piece promoted) {
        this.promoted = promoted;
    }

	public MovePromotion(final Position startposition, final Position endposition, Piece promoted){
		if(!(endposition.rank() == 8 || endposition.rank() == 1)) {
			throw new MoveException("kein gueltiger Zug!");

		}

		if(promoted instanceof King || promoted instanceof Pawn) {
			throw new MoveException("kann nicht zu Bauer oder Koenig umgewandelt werden");
		}

		setTarget(endposition);
		this.promoted = promoted;

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
    	return super.toString()+promoted.fenShort();
    }

    public final char flag() {
    	return FLAG;
    }
}
