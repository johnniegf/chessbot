package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Pieces.PC;

import java.util.Collection;
import java.util.Random;

/**
* Abstrakter Figurtyp (zur Vereinfachung der Implementierung).
*
* @author Johannes Haupt
*/
public abstract class AbstractPiece 
           implements Piece
{
    private Position mPosition;
    private boolean  mIsWhite;
    private boolean  mHasMoved;
    private long     mZobristHash;
    private boolean  mHashIsSet;

    /**
    * Erzeuge eine neue uninitialisierte Figur.
    */
    protected AbstractPiece() {
        this(Position.INVALID, false, false);
    }

    /**
    *
    */
    protected AbstractPiece(final Position position,
                            final boolean isWhite,
                            final boolean hasMoved)
    {
        setPosition(position);
        setIsWhite(isWhite);
        setHasMoved(hasMoved);
        mZobristHash = -1L;
        mHashIsSet = false;
        init();
    }

    /**
    * Initialisierungsroutine.
    *
    * Zur Nutzung durch Unterklassen. Falls weitere Initialisierung
    * der Figur im Konstruktor dieser Klasse benötigt wird.
    */
    protected void init() {
    }

    public Position getPosition() {
        return mPosition;
    }

    public void setPosition(final Position position) {
        if (position == null)
            throw new NullPointerException("position");
        mPosition = position;
    }

    public boolean isWhite() {
        return mIsWhite;
    }

    public void setIsWhite(final boolean isWhite) {
        mIsWhite = isWhite;   
    }

    public boolean hasMoved() {
        return mHasMoved;
    }

    public void setHasMoved(final boolean hasMoved) {
        mHasMoved = hasMoved;
    }
 
    public boolean attacks(final Board context, 
                           final Position targetSquare) 
    {
        if (context == null)
            throw new NullPointerException("context");
        if (targetSquare == null)
            throw new NullPointerException("targetSquare");
        
        return getAttacks(context).contains(targetSquare);
    }

    public boolean canMoveTo(final Board context, 
                             final Position targetSquare) 
    {
        for (Move m : getMoves(context))
            if (m.getTarget() == targetSquare)
                return true;

        return false;
    }

    public long hash() {
        if (!mHashIsSet) {
            mZobristHash = ZobristHasher.getInstance().hashPiece(this);
            mHashIsSet = true;
        }
        return mZobristHash;
    }

    public boolean equals(final Object other) {
        // Trivialfälle
        if (other == null) return false;
        if (other == this) return true;

        if (other instanceof Piece) {
            Piece p = (Piece) other;
            return p.id() == id()
                && p.isWhite() == isWhite()
                && p.getPosition().equals(getPosition());
        } else {
            return false;
        }
    }

    public char fenShort() {
        return (isWhite() ? Character.toUpperCase(fen())
                          : Character.toLowerCase(fen()));
    }

    /**
    * Hilfsmethode, gib den FEN-Schlüssel der Figur zurück.
    */
    protected abstract char fen();

    public Piece move(final Position targetSquare) {
        if (targetSquare == null)
            throw new NullPointerException("targetSquare");
        return PC(fenShort(), targetSquare, true);
    }

    public Piece clone() {
        Piece copy = create();
        copy.setIsWhite(isWhite());
        copy.setPosition(getPosition());
        copy.setHasMoved(hasMoved());
        return copy;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(isWhite() ? " w " : " b ");
        sb.append(getPosition());
        sb.append(hasMoved() ? " m" : "");
        return sb.toString();
    }

    protected abstract Piece create();

    private static final long WHITE_HASH = 0x74d8019166506d3dL;
    private static final long BLACK_HASH = 0x405ae4a7840f7b4fL;
    private static final long HAS_MOVED_HASH = 0x89bcf19b0dd79f98L;
    private static final long HAS_NOT_MOVED_HASH = 0xc5c8222936746874L;

}
