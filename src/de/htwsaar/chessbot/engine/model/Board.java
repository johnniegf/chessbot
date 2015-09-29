package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.piece.King;
import de.htwsaar.chessbot.engine.model.piece.Pieces;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.util.Bitwise;
import static de.htwsaar.chessbot.util.Exceptions.checkCondition;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.ArrayList;
import java.util.Collection;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Board {
    
    public static final BoardBuilder BUILDER = new BoardBuilder();
    
    public static Board B() {
        return BUILDER.getBoard();
    }
    
    public static Board B(final String fenString) {
        checkStringNotEmpty(fenString);
        return BUILDER.fromFenString(fenString);
    }

    private static void checkStringNotEmpty(String fenString) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // bitboards for pieces
    protected long[] mColors;
    protected long[] mPieces;
    protected long mOccupied;
    protected long mEnPassant;
    
    private long    mZobristHash;
    private boolean mWhiteAtMove;
    private short   mHalfMoves;
    private short   mMoveNumber;
    private byte    mCastlings;
    
    public Board clone() {
        Board copy = new Board();
        for (int i = 0; i < 6; i++) {
            copy.mPieces[i] = mPieces[i];
            if (i < 2)
                copy.mColors[i] = mColors[i];
        }
        copy.setEnPassant(mEnPassant);
        copy.setHash(hash());
        copy.setWhiteAtMove(isWhiteAtMove());
        copy.setHalfMoves(getHalfMoves());
        copy.setFullMoves(getFullMoves());
        return copy;
    }
    
    public Board() {
        mColors = new long[2];
        mPieces = new long[6];
        mEnPassant = 0L;
        
        mZobristHash = 0L;
        mWhiteAtMove = true;
        mHalfMoves   = 0;
        mMoveNumber  = 0;
        applyHash(getColorHash(WHITE));
    }
    
    public boolean isAttacked(final Position pos, final boolean byWhite) {
        return isAttacked(pos.toLong(), c(byWhite));
    }
    
    public boolean isAttacked(final long position, final int color) {
        return attackCount(position,color,false) > 0;
    }
    
    public int attackCount(final long position, final int color) {
        return attackCount(position,color,true);
    }
    
    private int attackCount(final long positions,
                            final int color,
                            final boolean countAllAttacks) 
    {
        checkInBounds(color, "color", 0, 1);
        final long colorMask = mColors[color];
        byte attacks = 0;
        long position, attackers, reverseAttacks;
        long squares = positions;
        Piece pc;
        while (squares != 0L) {
            for (int i = 0; i < 6; i++) {
                position = Bitwise.lowestBit(squares);
                pc = Pieces.PC(i, 
                               (i == Pawn.ID ? !color(color) : color(color)), 
                               Position.P(Bitwise.lowestBitIndex(position)));
                reverseAttacks = pc.getAttackBits(this);

                attackers = mPieces[i] & colorMask & reverseAttacks;
                if (countAllAttacks) {
                    attacks += Bitwise.count(attackers);
                } else {
                    return 1;
                }
            }
            squares = Bitwise.popLowestBit(squares);
        }
        return attacks;
    }
    
    public boolean isValid() {
        return !isKingInCheck(this, c(!isWhiteAtMove())) 
            && arePawnsValid(this);
        
    }
    
    public Position getEnPassant() {
        return Position.P(mEnPassant);
    }
    
    public boolean setEnPassant(final Position pos) {
        checkNull(pos, "position");
        return setEnPassant(pos.toLong());
    }
    
    public boolean setEnPassant(final long position) {
        checkBitBoardPosition(position);
        mEnPassant = position & sEnPassantMask;
        return true;
    }
    
    public boolean isWhiteAtMove() {
        return mWhiteAtMove;
    }
    
    public void setWhiteAtMove(final boolean whiteAtMove) {
        if (whiteAtMove != isWhiteAtMove())
            togglePlayer();
    }
    
    public void togglePlayer() {
        mWhiteAtMove = !mWhiteAtMove;
        applyHash(getColorHash(WHITE));
        applyHash(getColorHash(BLACK));  
    }
    
    public long hash() {
        return mZobristHash;
    }
    
    public void setHash(final long hash) {
        mZobristHash = hash;
    }
    
    public void applyHash(final long hash) {
        mZobristHash ^= hash;
    }
    
    public short getHalfMoves() {
        return mHalfMoves;
    }
    
    public void setHalfMoves(final int halfMoves) {
        checkInBounds(halfMoves, "halfMoves", 0, Short.MAX_VALUE);
        mHalfMoves = (short) halfMoves;
    }
    
    public short getFullMoves() {
        return mMoveNumber;
    }
    
    public void setFullMoves(final int fullMoves) {
        checkInBounds(fullMoves, "fullMoves", 0, Short.MAX_VALUE);
        mMoveNumber = (short) fullMoves;
        
    }
    
    public Move[] getMoveList() {
        Collection<Move> moveList = new ArrayList<Move>();
        int moveCount = 0;
        Board b;
        for (Piece p : getPieces(ALL,c(isWhiteAtMove()))) {
            for (Move m : p.getMoves(this)) {
                moveList.add(m);
                moveCount++;
            }
        }
        return moveList.toArray(new Move[moveCount]);
    }
    
        
    public byte getCastlings() {
        return mCastlings;
    }
    
    public boolean canCastle(final byte castlingType) {
        checkInBounds(castlingType, "castlingType", 0, 15);
        return (getCastlings() & castlingType) != 0;
    }
    
    public void setCastlings(final byte castlings) {
        checkInBounds(castlings, "castlings", 0, 15);
        mCastlings = castlings;
    }
    
    public void unsetCastlings(final byte castlings) {
        checkInBounds(castlings, "castlings", 0, 15);
        mCastlings &= ~castlings;
    }
    
    //--------------------------------------------------------------------------
    // --- Figurverwaltung -----------------------------------------------------
    //--------------------------------------------------------------------------
    
    /**
     * Prüfe, ob die übergebene Figur auf dem Schachbrett steht.
     * @param piece die zu suchende Figur
     * @return <code>true</code> genau dann, wenn die Figur auf dem Brett steht.
     */
    public boolean pieceExists(final Piece piece) {
        if (piece == null)
            return false;

        int c = c(piece.isWhite());
        int i = piece.id();
        long pos = piece.getPosition().toLong();
        long bitboard = mColors[c] & mPieces[i];
        return (bitboard & pos) > 0;
    }

    /**
     * Gib zurück, ob die übergebene Position frei ist.
     * @param pos das Feld auf dem Schachbrett
     * @return <code>true</code> genau dann, wenn pos nicht besetzt ist.
     */
    public boolean isFree(final Position pos) {
        checkNull(pos, "position");
        long p = pos.toLong();
        return isFree(p);
    }
    
    public boolean isFree(final long position) {
        checkBitBoardPosition(position);
        return (occupied() & position) == 0L;
    }
    
    /**
     * Leere das Schachbrett.
     */
    public void clear() {
        for (int i = 0; i < 6; i++) {
            mPieces[i] = 0L;
            if (i < 2) 
                mColors[i] = 0L;
        }
        mEnPassant = 0L;
        mOccupied  = 0L;
    }

    public boolean putPiece(final Piece piece) {

        final long p = piece.getPosition().toLong();
        final int c = (piece.isWhite() ? WHITE : BLACK);
        
        return putPiece(piece.id(), c, p);
    }
    
    public boolean putPiece(final int pieceId, final int color, final long position) {
        checkInBounds(pieceId, "pieceId", 0, 5);
        checkInBounds(color, "color", 0, 1);
        if (!isFree(position)) {
            return false;
        }
        mPieces[pieceId] |= position;
        mColors[color] |= position;
        return true;
    }
    
    public boolean movePieceTo(final Position from, final Position to) {
        return movePieceTo(from.toLong(), to.toLong());
    }
    
    public boolean movePieceTo(final long from, final long to) {
        checkBitBoardPosition(from);
        checkBitBoardPosition(to);
        if (isFree(from))
            return false;
        if (!isFree(to))
            return false;
        
        int type;
        for (type = 0; type < 6; type++) {
            if ((mPieces[type] & from) != 0L) {
                break;
            }
        }
        int color;
        for (color = 0; color < 2; color++) {
            if ((mColors[color] & from) == 0L)
                break;
        }
        removePieceAt(from);
        putPiece(type,color,to);
        return true;
    }

    
    public byte getPieceCount() {
        return Bitwise.count(occupied());
    }
    
    public Piece[] getPieces() {
        long currentPieces = 0L;
        Piece[] pieceList = new Piece[getPieceCount()];
        int offset = 0;
        for (int type = 0; type < 6; type++) {
            for (Piece p : getPieces(type))
                pieceList[offset++] = p;
            
        }
        return pieceList;
    }
    
    private Piece getPiece(final int pieceId, final long atPosition) {
        boolean isWhite = (atPosition & mColors[WHITE]) != 0L;
        return Pieces.PC(pieceId, isWhite, Position.P(atPosition));    
    }
    
    public Piece[] getPieces(final int pieceType) {
        return getPieces(pieceType, 2);
    }
    
    public Piece[] getPieces(final int pieceType, final int color) {
        checkInBounds(pieceType, "pieceType", 0, 2);
        long colorMask = 0L;
        switch (color) {
            case WHITE:
                colorMask = mColors[WHITE];
                break;
            case BLACK:
                colorMask = mColors[BLACK];
                break;
            case BOTH:
                colorMask = mColors[BLACK] ^ mColors[WHITE];
                break;
        }
        long pieces = mPieces[pieceType] & colorMask;
        long position;
        int count = Bitwise.count(pieces);
        int offset = 0;
        Piece[] pieceList = new Piece[count];
        byte index = 0;
        while (pieces != 0L) {
            index = Bitwise.lowestBitIndex(pieces);
            position = 1L << index;
            pieceList[offset++] = getPiece(pieceType, position);
            pieces = Bitwise.popLowestBit(pieces);
        }
        return pieceList;
    }

    public boolean removePieceAt(final Position pos) {
        checkNull(pos, "position");
        return removePieceAt(pos.toLong());
    }
    
    public boolean removePieceAt(final long position) {
        checkBitBoardPosition(position);
        for (int i = 0; i < 6; i++) {
            mPieces[i] &= ~position;
            if (i < 2)
                mColors[i] &= ~position;
        }
        return true;
    }
    
    public long occupied() {
        return mColors[WHITE] ^ mColors[BLACK];
    }
    
    public boolean removePiece(final Piece piece) {
        return removePiece(piece.id(),
                           c(piece.isWhite()), 
                           piece.getPosition().toLong());
    }
    
    public boolean removePiece(final int pieceId,
                               final int color,
                               final long position)
    {
        checkInBounds(pieceId, "pieceId", 0, 5);
        checkInBounds(color, "color", 0, 1);
        checkBitBoardPosition(position);
        mPieces[pieceId] &= ~position;
        mColors[color] &= ~position;
        return true;
    }

    public Piece getPieceAt(final Position pos) {
        if (isFree(pos))
            return null;
        long p = pos.toLong();
        int i;
        for (i = 0; i < 6; i++) {
            if ((mPieces[i] & p) != 0)
                break;
        }
        int c;
        for (c = 0; c < 2; c++) {
            if ((mColors[c] & p) != 0)
                break;
        }
        return Pieces.PC(i,c == WHITE,pos);
    }
    
    public long getPieceBitsForColor(final boolean white) {
        return mColors[c(white)];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
    
    private static int c(boolean isWhite) {
        return (isWhite ? WHITE : BLACK);
    }
    
    private static boolean color(final int color) {
        return color == WHITE;
    }
    
    private static int invert(final int color) {
        return (int) Bitwise.xor(color,1);
    }
    
    private static void checkBitBoardPosition(final long bb) {
        checkCondition(Bitwise.count(bb) == 1,
                       "Position darf nur ein 1-bit enthalten");
        
    }
    
    private static long getColorHash(final int color) {
        return ZobristHasher.getInstance().getColourHash(color == WHITE);
    }
    
    private static boolean isKingInCheck(final Board bb, final int color) {
        checkInBounds(color, "color", 0, 1);
        long king = bb.mColors[color] & bb.mPieces[King.ID];
        return bb.isAttacked(king, color ^ 1);
    }
    
    private static boolean arePawnsValid(final Board bb) {
        long whitePawns = bb.mPieces[Pawn.ID] & bb.mColors[WHITE];
        if ((whitePawns & sIllegalWhitePawns) != 0L)
            return false;
        long blackPawns = bb.mPieces[Pawn.ID] & bb.mColors[BLACK];
        return (blackPawns & sIllegalBlackPawns) == 0L;
    }
    
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    private static final int BOTH  = 2;
    private static final int ALL   = -1;
    private static final long sEnPassantMask = 0x0000_ff00_00ff_0000L;
    private static final long sIllegalWhitePawns = 0x0000_0000_0000_00ffL;
    private static final long sIllegalBlackPawns = 0xff00_0000_0000_0000L;
    
    public static final byte CASTLING_W_KING  = 1 << 3;
    public static final byte CASTLING_W_QUEEN = 1 << 2;
    public static final byte CASTLING_B_KING  = 1 << 1;
    public static final byte CASTLING_B_QUEEN = 1;
    
    private static final String[] sCastlings = new String[] {
        "-",    "q",   "k",   "kq",
        "Q", "  Qq",  "Qk",  "Qkq",
        "K",   "Kq",  "Kk",  "Kkq",
        "KQ", "KQq", "KQk", "KQkq"
    };


}
