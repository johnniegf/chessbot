package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BitBoardUtils.Color;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.toBool;
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

    private static final String line = "---------------------------------------";

    private final byte mCastlingType;
    private StandardMove mMove;

    public CastlingMove(final byte castlingType) {
        super(Position.INVALID, Position.INVALID);
        checkInBounds(castlingType, "castlingType", 0, 3);
        mCastlingType = castlingType;
        super.setStart(POSITIONS[mCastlingType][0]);
        super.setTarget(POSITIONS[mCastlingType][1]);
        mMove = (StandardMove) Move.MV(getStart(), getTarget());
    }

    public void setStart(final Position start) {    }
    
    public void setTarget(final Position target) {    }

    public byte type() {
        return mCastlingType;
    }
    
    @Override
    public Board tryExecute(final Board onBoard) {

        if (!onBoard.canCastle(FLAGS[mCastlingType])) {
            return null;
        }
        int color = (mCastlingType / 2) % 2;
        long freeSquares = PATH_MASKS[mCastlingType];
        long possibleAttacks = freeSquares & ATTACK_MASK;
        
        if ((freeSquares & onBoard.getOccupiedBits()) != 0L) return null;
        if ( onBoard.getAttacked(possibleAttacks, !toBool(color)) != 0L ) return null;
        
        Board result = mMove.tryExecute(onBoard, false);
        if (result != null) {
            moveRook(result, toBool(color));
            if ( !updateLastMove(this, result)) return null;
            result.recalculateAttacks();
        }
        return result;
    }
    
    private boolean moveRook(final Board result, final boolean isWhite) {
        result.removePieceAt(ROOKS[mCastlingType]);
        result.putPiece(Rook.ID, 
                        Color.toColor(isWhite), 
                        ROOK_TARGETS[mCastlingType]);
        return true;
    }
    
    private static final Position[][] POSITIONS = new Position[4][4];
    private static final byte[] FLAGS = new byte[4];
    
    private static final long ATTACK_MASK = 0x6c00_0000_0000_006cL;
    private static final long[] PATH_MASKS = new long[4];
    private static final long[] ROOKS  = new long[4];
    private static final long[] ROOK_TARGETS = new long[4];
    static {
        
        PATH_MASKS[W_KINGSIDE] = 0x0000_0000_0000_0060L;
        PATH_MASKS[W_QUEENSIDE] = 0x0000_0000_0000_000eL;
        PATH_MASKS[B_KINGSIDE] = 0x6000_0000_0000_0000L;
        PATH_MASKS[B_QUEENSIDE] = 0x0e00_0000_0000_0000L;
        
        ROOKS[W_KINGSIDE]  = 0x0000_0000_0000_0080L;
        ROOKS[W_QUEENSIDE] = 0x0000_0000_0000_0001L;
        ROOKS[B_KINGSIDE]  = 0x8000_0000_0000_0000L;
        ROOKS[B_QUEENSIDE] = 0x0100_0000_0000_0000L;
        
        ROOK_TARGETS[W_KINGSIDE] = 0x0000_0000_0000_0020L;
        ROOK_TARGETS[W_QUEENSIDE] = 0x0000_0000_0000_0008L;
        ROOK_TARGETS[B_KINGSIDE] = 0x2000_0000_0000_0000L;
        ROOK_TARGETS[B_QUEENSIDE] = 0x0800_0000_0000_0000L;
        
        POSITIONS[W_KINGSIDE]  = new Position[] { P("e1"), P("g1") };
        POSITIONS[W_QUEENSIDE] = new Position[] { P("e1"), P("c1") };
        POSITIONS[B_KINGSIDE]  = new Position[] { P("e8"), P("g8") };
        POSITIONS[B_QUEENSIDE] = new Position[] { P("e8"), P("c8") };
        
        FLAGS[W_KINGSIDE] = Board.CASTLING_W_KING;
        FLAGS[W_QUEENSIDE] = Board.CASTLING_W_QUEEN;
        FLAGS[B_KINGSIDE] = Board.CASTLING_B_KING;
        FLAGS[B_QUEENSIDE] = Board.CASTLING_B_QUEEN;
        
        
    }
    
    

    
}
