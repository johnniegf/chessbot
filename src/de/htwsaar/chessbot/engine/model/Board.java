package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.P;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Board {
    
    public static final byte MAX_WIDTH  = 26;
    public static final byte MAX_HEIGHT = 40;

    private static final byte DEFAULT_WIDTH  = 8;
    private static final byte DEFAULT_HEIGHT = 8;

//---------------------------------------------------------

    private final byte mWidth;
    private final byte mHeight;
    private Map<Position,Piece> mPieces;
    private short mPieceCount;
    private boolean mWhiteAtMove;
    private Position mEnPassant;
    private int mHalfMoves;
    private int mFullMoves;
    private long mZobristHash;

    /**
    * Standardkonstruktor.
    */ 
    public Board() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Board(final int width, final int height) {
        this((byte) width, (byte) height);
    }

    public Board(final byte width, final byte height) {
        checkDimensions(width, height);

        mWidth  = width;
        mHeight = height;
        mWhiteAtMove = true;
        mEnPassant = Position.INVALID;
        mHalfMoves = 0;
        mFullMoves = 1;
        mPieces = new HashMap<Position,Piece>(16);
        mPieceCount = 0;
    }

    public byte width() {
        return mWidth;
    }

    public byte height() {
        return mHeight;
    }

    /*
    *  FIGURVERWALTUNG
    */
    
    public boolean putPiece(final Piece piece) {
        if ( piece == null )
            return false;
        Position p = piece.getPosition();
        if ( !p.existsOn(this) )
            return false;

        mPieces.put(p, piece);
        applyHash(piece.hash());
        mPieceCount += 1;
        return true;
    }

    public Piece getPieceAt(final Position piecePosition) {
        return mPieces.get(piecePosition);
    }

    public Collection<Piece> getPiecesByType(long pieceId) {
        Collection<Piece> pieces = new ArrayList<Piece>();
        for (Piece pc : getPieces()) {
            if (pieceId == pc.id())
                pieces.add(pc);
        }
        return pieces;
    }

    public Collection<Piece> getPieces() {
        return mPieces.values();
    }

    public boolean removePieceAt(final Position piecePosition) {
        Piece p = mPieces.remove(piecePosition);
        if (p != null) {
            applyHash(p.hash());
            mPieceCount -= 1;
            return true;        
        } else
            return false;
    }

    public boolean isFree(final Position position) {
        return getPieceAt(position) == null;
    }

    public short getPieceCount() {
        return mPieceCount;
    }

    public int isAttacked(final boolean byWhitePieces, 
                          final Position targetSquare)
    {
        if (targetSquare == null)
            throw new NullPointerException("targetSquare");

        int attackCount = 0;
        for (Piece pc : getPieces()) {
            if (pc.isWhite() == byWhitePieces) {
                if (pc.attacks(this,targetSquare))
                    attackCount++;
            }
        }
        return attackCount;
    }

    /*
    *  SPIELERVERWALTUNG
    */ 
    public boolean isWhiteAtMove() {
        return mWhiteAtMove;
    }

    public void setWhiteAtMove(final boolean whiteAtMove) {
        mWhiteAtMove = whiteAtMove;
    }

    public void togglePlayer() {
        mWhiteAtMove = !mWhiteAtMove;
    }

    /*
    *  ZUGVERWALTUNG
    */
    public Collection<Move> getMoveList() {
        Collection<Move> moveList = new ArrayList<Move>();
        for (Piece pc : getPieces()) {
            moveList.addAll(pc.getMoves(this));
        }
        return moveList;
    }

    public int getHalfMoves() {
        return mHalfMoves;
    }

    public void setHalfMoves(final int halfMoves) {
        mHalfMoves = halfMoves;
    }

    public int getFullMoves() {
        return mFullMoves;
    }

    public void setFullMoves(final int fullMoves) {
        mFullMoves = fullMoves;
    }

    public Position getEnPassant() {
        return mEnPassant;
    }

    public void setEnPassant(final Position enPassant) {
        if ( enPassant == null )
            throw new NullPointerException("enPassant");
        mEnPassant = enPassant;
    }

    /*
    * HASHMANIPULATION
    */
    public int hashCode() {
        return (int) (hash() % Integer.MAX_VALUE);
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
        for (Piece pc : getPieces()) {
            hash ^= pc.hash();
        }
        setHash(hash);
    }

    public Board clone() {
        Board copy = new Board(width(), height());
        copy.setHalfMoves(getHalfMoves());
        copy.setFullMoves(getFullMoves());
        copy.setWhiteAtMove(isWhiteAtMove());
        copy.setEnPassant(getEnPassant());
        copy.setHash(hash());
        for (Piece pc : getPieces()) {
            copy.putPiece(pc);
        }
        return copy;
    }

    /**
    * Prüfe das Objekt auf Gleichheit mit einem anderen Objekt.
    *
    * @param other das zu prüfende Objekt.
    * @return <code>true</code> genau dann, wenn die Objekte gleich sind,
    *         sonst <code>false</code>
    */
    public boolean equals(final Object other) {
        // Triviale Fälle
        if (other == null) return false;
        if (other == this) return true;

        if (other instanceof Board) {
            final Board b = (Board) other;
            if ( b.width() != width() ) return false;
            if ( b.height() != height() ) return false;
            if ( b.getHalfMoves() != getHalfMoves() ) return false;
            if ( b.getFullMoves() != getFullMoves() ) return false;
            if ( b.isWhiteAtMove() != isWhiteAtMove() ) return false;
            if ( b.getEnPassant() != getEnPassant() )
                if (b.getEnPassant().isValid() != getEnPassant().isValid())
                    return false;
            if ( b.getPieceCount() != getPieceCount() ) return false;
            for ( Piece op : b.getPieces() ) {
                if ( !op.equals(getPieceAt(op.getPosition())) )
                    return false;
            }
            return true;   
        } else {
            return false;
        }
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    private void checkDimensions(byte width, byte height) {
        if (width < 1 || width > MAX_WIDTH)
            throw new InvalidDimensions(width, height);
        if (height < 1 || height > MAX_HEIGHT)
            throw new InvalidDimensions(width, height);
    }
}
