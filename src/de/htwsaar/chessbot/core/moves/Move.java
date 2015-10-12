package de.htwsaar.chessbot.core.moves;

import de.htwsaar.chessbot.core.Board;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.toColor;
import de.htwsaar.chessbot.core.Position;
import de.htwsaar.chessbot.core.pieces.King;
import de.htwsaar.chessbot.core.pieces.Rook;
import de.htwsaar.chessbot.core.pieces.Piece;
import de.htwsaar.chessbot.core.pieces.Pawn;
import java.util.Map;
import java.util.HashMap;
import static de.htwsaar.chessbot.util.Exceptions.checkCondition;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import static de.htwsaar.chessbot.util.Exceptions.msg;
/**
* Schachzug.
*
* Ein Schachzug hat ein Start- und ein Zielfeld. Unterklassen können evtl.
* weitere Eigenschaften definieren. Zugobjekte sind kontextabhängig, d.h. 
* ein- und derselbe Zug kann in verschiedenen Stellungen möglich sein 
* oder nicht.
*
* Züge werden erzeugt und vorgehalten vom <code>Move.Cache</code>. So
* existiert jedes differenzierbare Zugobjekt genau einmal.
*
* @author Johannes Haupt
*/
public abstract class Move {
    
    public static final Move NOMOVE = new Move.NoMove();

    public static final Move MV(final Position from,
                                final Position to)
    {
        return MV(from,to,StandardMove.TYPE);
    }
    
    public static final Move MV(final Position from,
                                final Position to,
                                final byte type)
    {
        return sCache.get(from,to,type);
    }
    
    public static final int CACHE_SIZE() {
        return sCache.size();
    }
    
    private Position mStart;
    private Position mTarget;
    
    protected Move(final Position startingSquare,
                   final Position targetSquare)
    {
        setStart(startingSquare);
        setTarget(targetSquare);       
    }
    
    public final Position getStart() {
        return mStart;
    }
    
    public void setStart(final Position startingSquare) {
        checkNull(startingSquare, "startingSquare");
        mStart = startingSquare;
    }
    
    public final Position getTarget() {
        return mTarget;
    }
    
    public void setTarget(final Position targetSquare) {
        checkNull(targetSquare, "targetSquare");
        checkCondition(targetSquare != getStart(), EXN_ILLEGAL_MOVE);
        mTarget = targetSquare;
    }
    
    public boolean isCapture(final Board onBoard) {
        return !onBoard.isFree(getTarget());
    }
    
    public boolean isQuiet(final Board onBoard) {
        return !isCapture(onBoard)
            && !Move.isPromotion(this);
    }
    
    public boolean isPossible(final Board onBoard) {
        Board r = tryExecute(onBoard);
        return r != null && r.isValid();
            
    }
    
    public final Board execute(final Board onBoard) {
        Board result = tryExecute(onBoard);
        if (result == null)
            throw new MoveException(
                msg(EXN_ILLEGAL_MOVE, this, onBoard) );
        if (!result.isValid())
            throw new MoveException(
                msg(EXN_ILLEGAL_MOVE, this, onBoard) );
        
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (obj instanceof Move) {
            final Move move = (Move) obj;
            if ( type() != move.type() ) return false;
            if ( getStart() != move.getStart() ) return false;
            if ( getTarget() != move.getTarget() ) return false;
            return true;
        } else
            return false;
    }
    
    @Override
    public int hashCode() {
        return type() << 12
             | getTarget().index() << 6
             | getStart().index();
    }
    
    public abstract Board tryExecute(final Board onBoard);
    
    public abstract byte type();
    
    @Override
    public String toString() {
        return getStart().toString() + getTarget();
    }
    
    protected boolean checkMove(final Board board, final Piece movingPiece) {
        if ( movingPiece == null ) return false;
        return movingPiece.canMoveTo(board, getTarget());
    }
    
    protected boolean doCapture(long origHash, Board board, final Piece movingPiece) {
        // Ist das Zielfeld besetzt...
        if ( !board.isFree(getTarget()) ) {
            Piece capturedPiece = board.getPieceAt(getTarget());
            // ... und die Figur darauf von der selben Farbe wie die gezogene?
            if ( capturedPiece.isWhite() == movingPiece.isWhite() )
                return false;
            // Wenn nein, dann schlage die Figur auf dem Zielfeld
            else {
                if (capturedPiece.id() == Rook.ID)
                    disableCastlingsForRookCapture(capturedPiece, board);
                board.removePiece(capturedPiece);
                board.setHalfMoves(0);
                board.clearHistory();
            }
        } else {
            if (movingPiece.id() != Pawn.ID) {
                board.setHalfMoves(board.getHalfMoves()+1);
                board.appendHistory(origHash);
            } else {
                board.setHalfMoves(0);
                board.clearHistory();
            }
        }
        return true;
    }
    
    protected boolean movePiece(Board board, final Piece movingPiece) {
        board.putPiece( movingPiece.move(getTarget()) );
        board.removePieceAt(getStart());
        return true;
    }
    
    protected boolean togglePlayer(Board board) {
        board.togglePlayer();
        if (board.isWhiteAtMove())
            board.setFullMoves(board.getFullMoves()+1);
        board.setEnPassant(Position.INVALID);
        return true;
    }
    
    protected boolean disableCastlings(final Piece movingPiece, Board board) {
        if (movingPiece.id() == King.ID) { 
            if (movingPiece.isWhite())
                board.unsetCastlings(
                    (byte) (Board.CASTLING_W_KING | Board.CASTLING_W_QUEEN)
                );
            else
                board.unsetCastlings(
                    (byte) (Board.CASTLING_B_KING | Board.CASTLING_B_QUEEN)
                );
        } 
        if (movingPiece.id() == Rook.ID) {
            Position pos = movingPiece.getPosition();
            if (pos.file() == 1)
                board.unsetCastlings(movingPiece.isWhite() 
                                     ? Board.CASTLING_W_QUEEN
                                     : Board.CASTLING_B_QUEEN);
            else if (pos.file() == 8)
                board.unsetCastlings(movingPiece.isWhite()
                                     ? Board.CASTLING_W_KING
                                     : Board.CASTLING_B_KING);
        }
        return true;           
    }
    
    protected boolean updateLastMove(final Move lastMove, Board context) {
    	checkNull(lastMove);
        context.setLastMove(lastMove);
        return true;
    }
    
    private static final long[] ROOKS = new long[2];
    private static final long[] SIDES = new long[2];
    private static final byte[][] FLAGS = new byte[2][2];
    private static final int KINGSIDE = 0;
    private static final int QUEENSIDE = 1;
    
        
    static {
        ROOKS[WHITE] = 0x0000_0000_0000_00a1L;
        ROOKS[BLACK] = 0xa100_0000_0000_0000L;
        SIDES[QUEENSIDE] = 0x0100_0000_0000_0001L;
        SIDES[KINGSIDE] = 0x8000_0000_0000_0080L;
        
        FLAGS[WHITE][QUEENSIDE] = Board.CASTLING_W_QUEEN;
        FLAGS[WHITE][KINGSIDE] = Board.CASTLING_W_KING;
        FLAGS[BLACK][QUEENSIDE] = Board.CASTLING_B_QUEEN;
        FLAGS[BLACK][KINGSIDE] = Board.CASTLING_B_KING;
        
    }    
    protected boolean disableCastlingsForRookCapture(final Piece captured, Board context) {
        int color = toColor(captured.isWhite());
        long pos = captured.getPosition().toBitBoard();
        for (int side = 0; side < 2; side++) {
            if (pos == (ROOKS[color] & SIDES[side])) {
                context.unsetCastlings(FLAGS[color][side]);
                return true;
            }
        }
        return false;
    }
    
    
    public static boolean isPawnPush(final Move m) {
        return m.type() == DoublePawnMove.TYPE;
    }
    
    public static boolean isEnPassant(final Move m) {
        return m.type() == EnPassantMove.TYPE;
    }
    
    public static boolean isNormal(final Move m) {
        return m.type() == StandardMove.TYPE;
        
    }
    
    public static boolean isCastling(final Move m) {
        byte t = m.type();
        return t == CastlingMove.W_KINGSIDE
            || t == CastlingMove.W_QUEENSIDE
            || t == CastlingMove.B_KINGSIDE
            || t == CastlingMove.B_QUEENSIDE;
    }
    
    public static boolean isPromotion(final Move m) {
        byte t = m.type();
        return t == PromotionMove.TO_BISHOP
            || t == PromotionMove.TO_KNIGHT
            || t == PromotionMove.TO_ROOK
            || t == PromotionMove.TO_QUEEN;
    }
    
    public static boolean isValidResult(final Board resultingBoard) {
        return resultingBoard != null && resultingBoard.isValid();
    }
    
    private static final String EXN_ILLEGAL_MOVE =
        "Der Zug ist regelwidrig und kann nicht ausgeführt werden: %s";
    
    private static final MoveCache sCache = new MoveCache();
    
    public static final class NoMove extends Move {
        
        public static final byte TYPE = 11;
        
        private NoMove() {
            super(Position.INVALID, Position.INVALID);
        }
        
        public byte type() {
            return TYPE;
        }
        
        public void setTarget(final Position target) {
            
        }
        
        public Board tryExecute(final Board board) {
            return null;
        }
        
        public String toString() {
            return "NoMove";
        }
        
    }
}

class MoveCache {
    
    private final Map<Short,Move> mCache;
    
    public MoveCache() {
        mCache = new HashMap<Short,Move>();
    }
    
    public int size() {
        return mCache.size();
    }
    
    public Move get(final Position from,
                    final Position to,
                    final byte type)
    {
        checkInBounds(type, "moveType", 0, EnPassantMove.TYPE);
        short key = makeIndex(from,to,type);
        Move result = mCache.get(key);
        if (result == null) {
            synchronized (mCache) {
                result = createMove(from,to,type);
                mCache.put(key, result);
            }
        }
        return result;
    }
    
    private static Move createMove(final Position from, final Position to, final byte type) {
        switch (type) {
            case CastlingMove.W_KINGSIDE:
            case CastlingMove.W_QUEENSIDE:
            case CastlingMove.B_KINGSIDE:
            case CastlingMove.B_QUEENSIDE:
                return new CastlingMove(type);
            
            case PromotionMove.TO_BISHOP:
            case PromotionMove.TO_KNIGHT:
            case PromotionMove.TO_ROOK:
            case PromotionMove.TO_QUEEN:
                return new PromotionMove(from, to, type);
               
            case StandardMove.TYPE:
                return new StandardMove(from, to);
                
            case DoublePawnMove.TYPE:
                return new DoublePawnMove(from);
                
            case EnPassantMove.TYPE:
                return new EnPassantMove(from,to);
                
            default:
                throw new MoveException();
        }
    }
    
    private static short makeIndex(final Position from, final Position to, final byte type) {
        int index = type << 12;
        index |= (to.isValid() ? to.index() : 0) << 6;
        index |= (from.isValid() ? from.index() : 0);
        return (short) index;
    }
    
}
