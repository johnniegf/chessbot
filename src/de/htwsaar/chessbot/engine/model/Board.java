package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.P;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Board {
    
    /** Maximale Breite des Schachbretts. */
    public static final byte MAX_WIDTH  = 26;

    /** Maximale Höhe des Schachbretts. */
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

    /**
    * Erzeuge ein Schachbrett mit den übergebenen Dimensionen.
    *
    * Konvertiert die Aufrufparameter nach <code>byte</code>

    * @param width Breite des Schachbretts
    * @param height Höhe des Schachbretts
    */
    public Board(final int width, final int height) {
        this((byte) width, (byte) height);
    }

    /**
    * Erzeuge ein Schachbrett mit den übergebenen Dimensionen.
    *
    * @param width Breite des Schachbretts
    * @param height Höhe des Schachbretts
    */
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

    /**
    * Gib die Breite dieses Schachbretts zurück.
    *
    * @return die Breite des Schachfelds.
    */
    public byte width() {
        return mWidth;
    }

    /**
    * Gib die Höhe dieses Schachbretts zurück.
    *
    * @return die Höhe des Schachfelds.
    */
    public byte height() {
        return mHeight;
    }

    /* ================================
    *  == FIGURVERWALTUNG
    *  ================================ */
    
    /**
    * Stelle die übergebene Figur auf das Schachbrett.
    *
    * @param piece die aufzustellende Figur
    * @return <code>true</code> falls die Figur aufgestellt wurde,
    *         sonst <code>false</code>
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

    /**
    * Gib die Figur auf dem überbenenen Feld zurück.
    *
    * @param piecePosition Position auf dem Schachbrett
    * @return die Figur auf der übergebenen <code>Position</code>, oder
    *         <code>null</code> falls das Feld leer ist.
    */
    public Piece getPieceAt(final Position piecePosition) {
        return mPieces.get(piecePosition);
    }

    /**
    * Gib alle Figuren mit der übergebenen Id zurück.
    *
    * @param pieceId ID der gesuchten Figurart
    * @return alle Figuren der gesuchten Figurart, die auf dem 
    *         Schachbrett stehen.
    */
    public Set<Piece> getPiecesByType(long pieceId) {
        Set<Piece> pieces = new HashSet<Piece>();
        for (Piece pc : getPieces()) {
            if (pieceId == pc.id())
                pieces.add(pc);
        }
        return pieces;
    }

    /**
    * Gib alle Figuren auf dem Schachbrett zurück.
    * 
    * @return eine <code>Collection</code> aller Figuren, die sich
    *         derzeit auf dem Schachbrett befinden.
    */
    public Collection<Piece> getPieces() {
        return mPieces.values();
    }

    /**
    * Entferne die Figur auf dem übergebenen Feld.
    *
    * @param piecePosition
    * @return <code>true</code> falls eine Figur vom Schachbrett
    *         entfernt wurde, sonst <code>false</code>
    */
    public boolean removePieceAt(final Position piecePosition) {
        Piece p = mPieces.remove(piecePosition);
        if (p != null) {
            applyHash(p.hash());
            mPieceCount -= 1;
            return true;        
        } else
            return false;
    }

    /**
    * Gib zurück, ob das übergebene Feld frei ist.
    * 
    * @return <code>true</code> falls das übergebene Feld frei ist,
    *         sonst <code>false</code>
    */
    public boolean isFree(final Position position) {
        return getPieceAt(position) == null;
    }
    
    /**
    * Gib die Anzahl der Figuren, die auf dem Schachbrett stehen zurück.
    *
    * @return Anzahl der Figuren auf dem Brett
    */
    public short getPieceCount() {
        return mPieceCount;
    }

    /**
    * Gib zurück, ob das übergebene Feld von einer Figur der übergebenen
    * Farbe angegriffen wird.
    *
    * @param byWhitePieces Farbe der angreifenden Figuren, <code>true
    *                      </code> für weiß.
    * @param targetSquare  zu prüfendes Feld
    * @return Anzahl der Angriffe auf das übergebene Feld
    */
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

    /* ================================
    *  == SPIELERVERWALTUNG
    *  ================================ */

    /**
    * Gib zurück, ob weiß am Zug ist.
    * 
    * @return <code>true</code> falls Weiß am Zug ist, sonst <code>
    *         false</code>
    */
    public boolean isWhiteAtMove() {
        return mWhiteAtMove;
    }

    /**
    * Lege fest, ob weiß am Zug ist.
    *
    * @param whiteAtMove ist weiß am Zug?
    */
    public void setWhiteAtMove(final boolean whiteAtMove) {
        mWhiteAtMove = whiteAtMove;
    }

    /**
    * Wechsele den Spieler, der am Zug ist.
    */
    public void togglePlayer() {
        mWhiteAtMove = !mWhiteAtMove;
    }

    /* ================================
    *  == ZUGVERWALTUNG
    *  ================================ */

    /**
    * Erzeuge die Zugliste für diese Stellung.
    *
    * @return ein <code>Set</code> aller möglichen Züge
    *         in dieser Stellung.
    */
    public Set<Move> getMoveList() {
        Set<Move> moves = new HashSet<Move>();
        for (Piece pc : getPieces()) {
            moves.addAll(pc.getMoves(this));
        }
        return moves;
    }

    /**
    * Anzahl der Halbzüge seitdem eine Figur geschlagen oder ein
    * Bauer gezogen wurde.
    *
    * @return Anzahl der Halbzüge, nach 50-Zugregel
    */
    public int getHalfMoves() {
        return mHalfMoves;
    }

    /**
    * Lege die Anzahl der Halbzüge, seitdem eine Figur geschlagen oder 
    * ein Bauer gezogen wurde, fest.
    *
    * @param halfMoves Anzahl der Halbzüge, nach 50-Zugregel
    */
    public void setHalfMoves(final int halfMoves) {
        mHalfMoves = halfMoves;
    }

    /**
    * Gib die Anzahl der Spielzüge aus.
    *
    * @return Anzahl der Züge
    */
    public int getFullMoves() {
        return mFullMoves;
    }

    /**
    * Lege die Anzahl der gespielten Züge fest.
    *
    * @param fullMoves Anzahl der Spielzüge
    */
    public void setFullMoves(final int fullMoves) {
        mFullMoves = fullMoves;
    }

    /**
    * Gib das Feld zurück, auf dem Schlagen per en-passant möglich ist.
    *
    * @return die <code>Position</code>, auf der en passant möglich ist,
    *         oder eine ungültige Position, falls nicht.
    */
    public Position getEnPassant() {
        return mEnPassant;
    }

    /**
    * Lege das Feld fest, auf dem Schlagen per en passant möglich ist.
    *
    * @param enPassant die <code>Position</code>, auf der en passant
    *                  möglich ist.
    */
    public void setEnPassant(final Position enPassant) {
        if ( enPassant == null )
            throw new NullPointerException("enPassant");
        mEnPassant = enPassant;
    }

    /* ================================
    *  == HASHMANIPULATION
    *  ================================ */
    public int hashCode() {
        return (int) (hash() % Integer.MAX_VALUE);
    }

    /**
    * Gib den Zobrist-Hashwert dieser Stellung zurück.
    *
    * @return Hashwert der Stellung
    */
    public long hash() {
        return mZobristHash;
    }

    /**
    * Lege den Zobrist-Hashwert dieser Stellung fest.
    *
    * @param hash Hashwert der Stellung
    */
    public void setHash(final long hash) {
        mZobristHash = hash;
    }

    /**
    * Wende einen Hashwert auf den Hashwert dieser Stellung an.
    *
    * XOR
    *
    * @param hash Hashwert, der auf den Hashwert der Stellung 
    *             angewendet wird
    */
    public void applyHash(final long hash) {
        mZobristHash ^= hash;
    }

    /**
    * Berechne den Zobrist-Hashwert dieser Stellung.
    */
    public void calculateHash() {
        long hash = 0L;
        for (Piece pc : getPieces()) {
            hash ^= pc.hash();
        }
        setHash(hash);
    }

    /**
    * Kopiere diese Stellung.
    *
    * @return eine Kopie dieser Stellung
    */
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
