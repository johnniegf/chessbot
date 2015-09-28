package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;
import static de.htwsaar.chessbot.engine.model.Pieces.PC;
import de.htwsaar.chessbot.util.Bitwise;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.ArrayList;
import java.util.Collection;

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
    private long     mZobristHash;
    private boolean  mHashIsSet;

    /**
    * Erzeuge eine neue uninitialisierte Figur.
    */
    protected AbstractPiece() {
        this(Position.INVALID, false);
    }

    /**
    *
    */
    protected AbstractPiece(final Position position,
                            final boolean isWhite)
    {
        setPosition(position);
        setIsWhite(isWhite);
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
        checkNull(position, "position");
        mPosition = position;
    }

    public boolean isWhite() {
        return mIsWhite;
    }

    public void setIsWhite(final boolean isWhite) {
        mIsWhite = isWhite;   
    }
    
    public abstract long getAttackBits(final Board context);
    
    public Collection<Position> getAttacks(final Board context) {
        Collection<Position> attacks = new ArrayList<Position>();
        long attackBits = getAttackBits(context);
        int index = 0;
        while(attackBits != 0L) {
            index = Bitwise.lowestBit(attackBits);
            attacks.add( Position.P(index));
            attackBits = Bitwise.popLowestBit(attackBits);
        }
        return attacks;
    }
 
    public boolean attacks(final Board context, 
                           final Position targetSquare) 
    {
        checkNull(context, "context");
        checkNull(targetSquare, "targetSquare");
        
        return (getAttackBits(context) & targetSquare.toLong()) != 0L;
    }
    
    public long getMoveBits(final Board context) {
        return getAttackBits(context) ^ context.getPieceBitsForColor(isWhite());
        
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        Position from, to;
        from = getPosition();
        long moveBits = getMoveBits(context);
        int index = -1;
        while (moveBits != 0L) {
            index = Bitwise.lowestBit(moveBits);
            to = Position.P(index);
            moves.add( MV(from,to) );
            moveBits = Bitwise.popLowestBit(moveBits);
        }
        return moves;
    }
    
    public boolean canMoveTo(final Board context, 
                             final Position targetSquare) 
    {
        return (getMoveBits(context) & targetSquare.toLong()) != 0L;
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
        return PC(id(), isWhite(), targetSquare);
    }

    public Piece clone() {
        Piece copy = create();
        copy.setIsWhite(isWhite());
        copy.setPosition(getPosition());
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
    
    private static long[][] rayAttacks = new long[8][64];
    private static long[] fileMasks    = new long[8];
    private static long[] rankMasks    = new long[8];
    
    private static final void initRayAttacks() {
        rayAttacks = new long[8][64];
        for (int i = Direction.North; i <= Direction.NorthWest; i++) {
            for (int sq = 0; sq < 64; sq++) {
                rayAttacks[i][sq] = generateRayAttack(i, sq);
            }
        }
        
    }
    
    private static final long generateRayAttack(int direction, int sq) {
        return 0L;
    }
    
    public static final class Direction {
        public static final int North = 0;
        public static final int NorthEast = 1;
        public static final int East = 2;
        public static final int SouthEast = 3;
        public static final int South = 4;
        public static final int SouthWest = 5;
        public static final int West = 6;
        public static final int NorthWest = 7;
    }
    

    private static void initMasks() {
        long fileMask = 0x0101_0101_0101_0101L;
        long rankMask = 0x0000_0000_0000_00ffL;
        for (int i = 0; i < 8; i++, 
                               fileMask <<= 1,
                               rankMask <<= 8) 
        {
            fileMasks[i] = fileMask;
            rankMasks[i] = rankMask;
        }

    }
    
}
