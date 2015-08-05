package de.htwsaar.chessbot.engine.model.variant.fide;

import static de.htwsaar.chessbot.engine.model.ChessVariant.*;
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

	public static final char FLAG_ROOK = 'R';
	public static final char FLAG_QUEEN = 'Q';
	public static final char FLAG_KNIGHT = 'N';
	public static final char FLAG_BISHOP = 'B';
	
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

		setStart(startposition);
		setTarget(endposition);
		this.promoted = promoted;

	}

	
    @Override
	protected Board tryExecute(Board onBoard) {
    	System.out.println("MovePromotion.tryExecute("+onBoard+")");
    	System.out.println("start = " + getStart());
    	if(!(onBoard.getPieceAt(getStart())instanceof Pawn)) {
    		return null;
    	}
        Board target = super.tryExecute(onBoard);
        System.out.println("Escaped my parent alive");
        if (target != null) {
        	System.out.println("and with loot!");
        	Piece pawn = target.getPieceAt(getTarget());
        	target.removePieceAt(getTarget());
        	System.out.println(pawn);

        	this.promoted.setIsWhite(pawn.isWhite());
        	Piece movedPiece = PC(promoted.fenShort(), getTarget());
        	System.out.println(movedPiece);
        	target.putPiece(movedPiece);
        }
        System.out.println(target);
        return target;
    }
    
    public String toString() {
    	return super.toString()+flag();
    }

    public final char flag() {
    	if (promoted instanceof Queen) return FLAG_QUEEN;
    	if (promoted instanceof Knight) return FLAG_KNIGHT;
    	if (promoted instanceof Bishop) return FLAG_BISHOP;
    	if (promoted instanceof Rook) return FLAG_ROOK;
    	return 'P';
    }
}
