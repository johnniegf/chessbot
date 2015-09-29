package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.Bishop;
import de.htwsaar.chessbot.engine.model.piece.King;
import de.htwsaar.chessbot.engine.model.piece.Knight;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Queen;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
* Bauernumwandlung.
*
* @author Dominik Becker
*/
public class PromotionMove extends Move {

    private static final int MASK   = 0x003c_0000;
    public static final byte TO_QUEEN  = 7;
    public static final byte TO_ROOK   = 6;
    public static final byte TO_KNIGHT = 5;
    public static final byte TO_BISHOP = 4; 

    private static final String EXN_INVALID_START = 
        "Ungültiges Startfeld, muss in 2. oder 7. Reihe liegen";

    private static final String EXN_INVALID_TARGET =
        "Ungültiges Zielfeld, muss für Bauer auf Startfeld erreichbar sein.";
    private static final String EXN_INVALID_CONV_TARGET =
        "kann nicht zu Bauer oder Koenig umgewandelt werden";


	private byte mPromotionType;

	public PromotionMove(final Position start, 
                         final Position target, 
                         final byte promotionType){
        super(start,target);
        checkInBounds(promotionType, "promotedType", TO_BISHOP, TO_QUEEN);

		setStart(start);
        setTarget(target);
		this.mPromotionType = promotionType;

	}
    
    public void setStart(final Position pos) {
        checkNull(pos, "startingSquare");
        if (pos.rank() == 7 || pos.rank() == 2) {
            super.setStart(pos);
        } else {
            throw new MoveException();
        }
            
    }
    
    public void setTarget(final Position pos) {
        checkNull(pos, "targetSquare");
        if ( abs(pos.file() - getStart().file()) > 1) {
            throw new MoveException();
        }
        if ( (pos.rank() - 1) != ((getStart().rank()-1) ^ 1)) {
            throw new MoveException();
        }
        super.setTarget(pos);
    }
	
    @Override
	public Board tryExecute(Board onBoard) {
        return onBoard;
        
    }
    
    public byte type() {
        return mPromotionType;
    }
    
    public String toString() {
    	return super.toString() + getFenCharacter(mPromotionType);
    }
    
    private static int abs(final int value) {
        return (value < 0 ? -value : value);
    }

    private char getFenCharacter(final int promotedType) {
        switch (promotedType) {
            case TO_BISHOP:
                return 'b';
            case TO_KNIGHT:
                return 'n';
            case TO_ROOK:
                return 'r';
            case TO_QUEEN:
                return 'q';
            default:
                throw new MoveException();
        }
    }
    
    private byte getPieceType(final byte promotionType) {
        switch (promotionType) {
            case TO_BISHOP:
                return Bishop.ID;
            case TO_KNIGHT:
                return Knight.ID;
            case TO_ROOK:
                return Rook.ID;
            case TO_QUEEN:
                return Queen.ID;
            default:
                throw new MoveException();
        }
    }
}
