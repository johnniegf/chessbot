package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.COLORS;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.invert;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.toBool;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.toColor;
import static de.htwsaar.chessbot.engine.model.BoardUtils.checkBitBoardPosition;
import static de.htwsaar.chessbot.engine.model.BoardUtils.toIndex;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.piece.King;
import de.htwsaar.chessbot.engine.model.piece.Pieces;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.util.Bitwise;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.ArrayList;
import java.util.Collection;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public final class Board {
    
    public static final BoardBuilder BUILDER = new BoardBuilder();
    
    public static Board B() {
        return BUILDER.getBoard();
    }
    
    public static Board B(final String fenString) {
        checkNull(fenString);
        return BUILDER.fromFenString(fenString);
    }

    // bitboards for pieces
    protected long[] mColors;
    protected long[] mPieces;
    protected Position mEnPassant;
    
    private long    mZobristHash;
    private boolean mWhiteAtMove;
    private short   mHalfMoves;
    private short   mMoveNumber;
    private byte    mCastlings;
    
    @Override
    public final Board clone() {
        Board copy = new Board();
        System.arraycopy(mPieces, 0, copy.mPieces, 0, 6);
        System.arraycopy(mColors, 0, copy.mColors, 0, 2);
        copy.setEnPassant(mEnPassant);
        copy.setHash(hash());
        copy.setWhiteAtMove(isWhiteAtMove());
        copy.setHalfMoves(getHalfMoves());
        copy.setFullMoves(getFullMoves());
        copy.setCastlings(getCastlings());
        return copy;
    }
    
    @Override
    public final boolean equals(final Object other) {
        if (other == this) return true;
        if (other == null) return false;
        
        if (other instanceof Board) {
            Board bb = (Board) other;
            //if (bb.hash() != hash()) return false;
            for (int c : COLORS) {
                if (bb.mColors[c] != mColors[c]) return false;
            }
            for (int t = 0; t < 6; t++) {
                if (bb.mPieces[t] != mPieces[t]) return false;
            }
            if (bb.mEnPassant != mEnPassant) return false;
            if (bb.isWhiteAtMove() != isWhiteAtMove()) return false;
            if (bb.getHalfMoves() != getHalfMoves()) return false;
            if (bb.getFullMoves() != getFullMoves()) return false;
            if (bb.getCastlings() != getCastlings()) return false;
            return true;
        }
        return false;
    }
    
    public Board() {
        mColors = new long[2];
        mPieces = new long[6];
        
        setHash(0L);
        mWhiteAtMove = true;
        applyHash(WHITE_HASH);
        
        mEnPassant = Position.INVALID;
        setHalfMoves(0);
        setFullMoves(1);
        setCastlings((byte) 0);
    }
    
    public boolean isAttacked(final Position pos, final boolean byWhite) {
        return isAttacked(pos.toBitBoard(), toColor(byWhite));
    }
    
    public boolean isAttacked(final long position, final int color) {
        return attackCount(position,color,false) > 0;
    }
    
    public int attackCount(final long position, final int color) {
        return attackCount(position,color,true);
    }
    
    private int attackCount(final long position,
                            final int color,
                            final boolean countAllAttacks) 
    {
        //checkBitBoardPosition(position);
        //checkInBounds(color, "color", 0, 1);
        final long colorMask = mColors[color];
        int attacks = 0;
        long attackers, reverseAttacks;
        Piece pc;
        for (int i = 0; i < 6; i++) {
            attackers = mPieces[i] & colorMask;
            if (attackers == 0L)
                continue;
            pc = Pieces.PC(i, 
                           (i == Pawn.ID ? !toBool(color) : toBool(color)), 
                           Position.BB(position));
            reverseAttacks = pc.getAttackBits(this);

            attackers &= reverseAttacks;
            if (countAllAttacks) {
                attacks += Bitwise.count(attackers);
            } else if (attackers != 0L) {
                return 1;
            }
        }
        return attacks;
    }
    
    public boolean isValid() {
        return !isKingInCheck(this) 
            && arePawnsValid(this);
        
    }
    
    public Position getEnPassant() {
        return mEnPassant;
    }
    
    public boolean setEnPassant(final Position pos) {
        checkNull(pos, "position");
        applyHash( HASHER.getEnPassantHash(getEnPassant()) );
        mEnPassant = pos;
        applyHash( HASHER.getEnPassantHash(getEnPassant()) );
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
        applyHash(TOGGLE_HASH);  
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
    
    public void calculateHash() {
        long hash = 0L;
        setHash(hash);
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
        Board b;
        int count = 0;
        for (Piece p : getPiecesForColor(toColor(isWhiteAtMove()))) {
            for (Move m : p.getMoves(this)) {
                if (m.isPossible(this)) {
                    moveList.add(m);
                    count++;
                }
            }
        }
        return moveList.toArray(new Move[count]);
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
        applyHash( HASHER.getCastlingHash(mCastlings) );
        mCastlings = castlings;
        applyHash( HASHER.getCastlingHash(mCastlings));
    }
    
    public void unsetCastlings(final byte castlings) {
        checkInBounds(castlings, "castlings", 0, 15);
        applyHash( HASHER.getCastlingHash(mCastlings));
        mCastlings &= ~castlings;
        applyHash( HASHER.getCastlingHash(mCastlings));
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

        int c = toColor(piece.isWhite());
        int i = piece.id();
        long pos = piece.getPosition().toBitBoard();
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
        long p = pos.toBitBoard();
        return isFree(p);
    }
    
    public boolean isFree(final long position) {
        //checkBitBoardPosition(position);
        return (occupied() & position) == 0L;
    }
    
    public boolean putPiece(final Piece piece) {

        final long p = piece.getPosition().toBitBoard();
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
        applyHash( HASHER.hashPiece(pieceId, toBool(color), Bitwise.lowestBitIndex(position)));
        return true;
    }
    
    public boolean movePieceTo(final Position from, final Position to) {
        return movePieceTo(from.toBitBoard(), to.toBitBoard());
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
        removePiece(type,color,from);
        putPiece(type,color,to);
        return true;
    }

    
    public byte getPieceCount() {
        return Bitwise.count(occupied());
    }
    
    public Piece[] getPieces() {
        Piece[] pieceList = new Piece[getPieceCount()];
        int offset = 0;
        for (int type = 0; type < 6; type++) {
            Piece[] pieces = getPieces(type);
            System.arraycopy(pieces, 0, pieceList, offset, pieces.length);
            offset += pieces.length;
        }
        return pieceList;
    }
    
    private Piece[] getPiecesForColor(final int color) {
        checkInBounds(color,0,1);
        int count = Bitwise.count(mColors[color]);
        Piece[] pieceList = new Piece[count];
        int offset = 0;
        for (int type = 0; type < 6; type++) {
            for (Piece piece : getPieces(type,color)) {
                pieceList[offset] = piece;
                offset += 1;
            }
        }
        return pieceList;
    }
    
    private Piece getPiece(final int pieceId, final long atPosition, final int color) {
        checkBitBoardPosition(atPosition);
        checkInBounds(color, 0, 1);
        checkInBounds(pieceId, 0, 5);
        long search = mPieces[pieceId] & mColors[color] & atPosition;
        if (search != 0L) {
            return Pieces.PC(pieceId, toBool(color), toIndex(atPosition) );
        }
        return null;
    }
    
    public Piece[] getPieces(final int pieceType) {
        final int count = Bitwise.count(mPieces[pieceType]);
        Piece[] pieces = new Piece[count];
        int ofs = 0;
        for (int c : COLORS) {
            Piece[] tmp = getPieces(pieceType, c);
            System.arraycopy(tmp, 0, pieces, ofs, tmp.length);
            ofs += tmp.length;
        }
        return pieces;
    }
    
    public Piece[] getPieces(final int pieceType, final int color) {
        checkInBounds(pieceType, 0, 5);
        checkInBounds(color, "color", 0, 1);
        long pieces = mPieces[pieceType] & mColors[color];

        int count = Bitwise.count(pieces);
        Piece[] pieceList = new Piece[count];
        int offset = 0;
        long position;
        while (pieces != 0L) {
            position = Bitwise.lowestBit(pieces);
            pieceList[offset] = getPiece(pieceType, position, color);
            pieces = Bitwise.popLowestBit(pieces);
            offset += 1;
        }
        return pieceList;
    }

    public boolean removePieceAt(final Position pos) {
        checkNull(pos, "position");
        return removePieceAt(pos.toBitBoard());
    }
    
    public boolean removePieceAt(final long position) {
        //checkBitBoardPosition(position);
        if (isFree(position)) return false;
        
        Piece rem = getPieceAt(position);
        return removePiece(rem);
        /*
        for (int i = 0; i < 6; i++) {
            mPieces[i] &= ~position;
            if (i < 2)
                mColors[i] &= ~position;
        }
        return true;
            */
    }
    
    public long occupied() {
        return mColors[WHITE] ^ mColors[BLACK];
    }
    
    public boolean removePiece(final Piece piece) {
        return removePiece(piece.id(),
                           toColor(piece.isWhite()), 
                           piece.getPosition().toBitBoard());
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
        applyHash( HASHER.hashPiece(pieceId, toBool(color), Bitwise.lowestBitIndex(position)));
        return true;
    }
    
    public long getAttacked(final long squares, final boolean byWhite) {
        long mSquares = squares;
        long attacked  = 0L;
        long pos;
        while(mSquares != 0L) {
            pos = Bitwise.lowestBit(mSquares);
            if (isAttacked(pos, toColor(byWhite))) {
                attacked |= pos;
            }
            mSquares = Bitwise.popLowestBit(mSquares);
        }
        return attacked;
    }
    
    public Piece getPieceAt(final Position pos) {
        checkNull(pos);
        return getPieceAt(pos.toBitBoard());
    }

    public Piece getPieceAt(final long p) {
        checkBitBoardPosition(p);
        if (isFree(p))
            return null;
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
        return Pieces.PC(i,toBool(c),toIndex(p));
    }
    
    public long getPieceBitsForColor(final boolean white) {
        return mColors[toColor(white)];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        char[][] squares = new char[8][8];
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                Piece p = getPieceAt(Position.P(x+1,y+1));
                squares[x][y] = (p == null ? '.' : p.fenShort());
            }

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                sb.append(squares[x][y]);
            }
            sb.append("\n");
        }
        sb.append(isWhiteAtMove() ? "w" : "b");
        sb.append(space);
        sb.append(sCastlings[getCastlings()]);
        sb.append(space);
        sb.append(getEnPassant().isValid() ? getEnPassant() : "-");
        sb.append(space);
        sb.append(getHalfMoves());
        sb.append(space);
        sb.append(getFullMoves());
        sb.append(newline);
        sb.append(String.format("hash(0x%016x)",hash()));
        return sb.toString();
    }
    

    
    private static long getColorHash(final int color) {
        return ZobristHasher.getInstance().getColourHash(color == WHITE);
    }
    
    private static boolean isKingInCheck(final Board bb) {
        //checkInBounds(color, "color", 0, 1);
        int color = toColor(!bb.isWhiteAtMove());
        long king = bb.mColors[color] & bb.mPieces[King.ID];
        return king != 0L && bb.isAttacked(king, invert(color));
    }
    
    private static boolean arePawnsValid(final Board bb) {
        long whitePawns = bb.mPieces[Pawn.ID] & bb.mColors[WHITE];
        if ((whitePawns & sIllegalWhitePawns) != 0L)
            return false;
        long blackPawns = bb.mPieces[Pawn.ID] & bb.mColors[BLACK];
        return (blackPawns & sIllegalBlackPawns) == 0L;
    }
    
    private static final String newline = System.getProperty("line.separator");
    private static final String space = " ";
    
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
    
    private static final ZobristHasher HASHER = ZobristHasher.getInstance();
    private static final long TOGGLE_HASH = HASHER.getColourHash(true)
                                          ^ HASHER.getColourHash(false);
    private static final long WHITE_HASH = HASHER.getColourHash(true);
    


}
