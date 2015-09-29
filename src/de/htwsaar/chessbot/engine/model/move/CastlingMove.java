package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.King;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import static de.htwsaar.chessbot.engine.model.Position.P;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
/**
* Rochade.
*
* @author Johannes Haupt
*/
public class CastlingMove extends Move {

    public static final byte W_KINGSIDE  = 0;
    public static final byte W_QUEENSIDE = 1;
    public static final byte B_KINGSIDE  = 2;
    public static final byte B_QUEENSIDE = 3;


    private final byte mCastlingType;

    public CastlingMove(final byte castlingType) {
        super(Position.INVALID, Position.INVALID);
        checkInBounds(castlingType, "castlingType", 0, 3);
        mCastlingType = castlingType;
        super.setStart(POSITIONS[mCastlingType][0]);
        super.setTarget(POSITIONS[mCastlingType][1]);
    }

    public void setStart(final Position start) {    }
    
    public void setTarget(final Position target) {    }

    public byte type() {
        return mCastlingType;
    }
    
    @Override
    public Board tryExecute(final Board onBoard) {

        if (!onBoard.canCastle(mCastlingType)) {
            return null;
        }
        boolean isWhite = (mCastlingType / 2) % 2 == 0;
        long freeSquares = POSITIONS[mCastlingType][1].toLong() 
                         | POSITIONS[mCastlingType][3].toLong(); 
        freeSquares = ~freeSquares & (isWhite ? WHITE_MASK : BLACK_MASK);
        long possibleAttacks = freeSquares & ATTACK_MASK << (isWhite ? 0 : 56);
        if ((freeSquares & onBoard.occupied()) != 0L) return null;
        if ( onBoard.isAttacked(possibleAttacks, (isWhite ? Board.BLACK : Board.WHITE)) )
            return null;
        
        Board result = onBoard.clone();
        Piece pc = onBoard.getPieceAt(getStart());
        
        movePiece(result, pc);
        moveRook(result, pc.isWhite());
        return result;
    }
    
    private boolean moveRook(final Board result, final boolean isWhite) {
        result.removePieceAt(POSITIONS[mCastlingType][2].toLong());
        result.putPiece(Rook.ID, 
                        (isWhite ? Board.WHITE : Board.BLACK), 
                        POSITIONS[mCastlingType][3].toLong());
        return true;
    }
    
    private static Position[][] POSITIONS = new Position[4][4];
    private static byte[] FLAGS = new byte[4];
    
    private static final long WHITE_MASK = 0x0000_0000_0000_00ffL;
    private static final long BLACK_MASK = 0xff00_0000_0000_0000L;
    private static final long ATTACK_MASK = 0x6cL;
    static {
        POSITIONS[W_KINGSIDE]  = new Position[] { P("e1"), P("g1"), P("h1"), P("f1") };
        POSITIONS[W_QUEENSIDE] = new Position[] { P("e1"), P("c1"), P("a1"), P("d1") };
        POSITIONS[B_KINGSIDE]  = new Position[] { P("e8"), P("g8"), P("h8"), P("f8") };
        POSITIONS[B_QUEENSIDE] = new Position[] { P("e8"), P("c8"), P("a8"), P("d8") };
        
        FLAGS[W_KINGSIDE] = Board.CASTLING_W_KING;
        FLAGS[W_QUEENSIDE] = Board.CASTLING_W_QUEEN;
        FLAGS[B_KINGSIDE] = Board.CASTLING_B_KING;
        FLAGS[B_QUEENSIDE] = Board.CASTLING_B_QUEEN;
    }
    
    

    
}
