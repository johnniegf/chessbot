package de.htwsaar.chessbot.engine.model.piece;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.isNegativeRay;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.shift;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.ZobristHasher;
import static de.htwsaar.chessbot.engine.model.move.Move.MV;
import static de.htwsaar.chessbot.engine.model.piece.Pieces.PC;
import de.htwsaar.chessbot.util.Bitwise;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
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
    private long     mZobristHash = -1;
    private boolean  hashIsSet = false;

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

    public final void setPosition(final Position position) {
        checkNull(position, "position");
        mPosition = position;
    }

    public boolean isWhite() {
        return mIsWhite;
    }

    public final void setIsWhite(final boolean isWhite) {
        mIsWhite = isWhite;   
    }
    
    public abstract long getAttackBits(final Board context);
    
    public Collection<Position> getAttacks(final Board context) {
        Collection<Position> attacks = new ArrayList<Position>();
        long attackBits = getAttackBits(context);
        int index = 0;
        while(attackBits != 0L) {
            index = Bitwise.lowestBitIndex(attackBits);
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
        
        return (getAttackBits(context) & targetSquare.toBitBoard()) != 0L;
    }
    
    public long getMoveBits(final Board context) {
        if (context.isWhiteAtMove() != isWhite())
            return 0L;
        return getAttackBits(context) & ~context.getPieceBitsForColor(isWhite());
        
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        Position from, to;
        from = getPosition();
        long moveBits = getMoveBits(context);
        int index = -1;
        while (moveBits != 0L) {
            index = Bitwise.lowestBitIndex(moveBits);
            to = Position.P(index);
            moves.add( MV(from,to) );
            moveBits = Bitwise.popLowestBit(moveBits);
        }
        return moves;
    }
    
    @Override
    public boolean canMoveTo(final Board context, 
                             final Position targetSquare) 
    {
        return (getMoveBits(context) & targetSquare.toBitBoard()) != 0L;
    }

    @Override
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

    @Override
    public char fenShort() {
        return (isWhite() ? Character.toUpperCase(fen())
                          : Character.toLowerCase(fen()));
    }

    /**
    * Hilfsmethode, gib den FEN-Schlüssel der Figur zurück.
    */
    protected abstract char fen();
    
    public final long hash() {
        if (!hashIsSet) {
            mZobristHash = ZobristHasher.getInstance().hashPiece(this);
            hashIsSet = true;
        }
        return mZobristHash;
    }

    public Piece move(final Position targetSquare) {
        if (targetSquare == null)
            throw new NullPointerException("targetSquare");
        return PC(id(), isWhite(), targetSquare);
    }

    public Piece clone() {
        Piece copy = create(this.id(), this.getPosition(), this.isWhite());
        copy.setIsWhite(isWhite());
        copy.setPosition(getPosition());
        return copy;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(isWhite() ? " w " : " b ");
        sb.append(getPosition());
        return sb.toString();
    }

    public static Piece create(final int pieceType,
                               final Position position,
                               final boolean isWhite) 
    {
        checkInBounds(pieceType, "pieceType", 0, 5);
        switch (pieceType) {
            case King.ID:
                return new King(position, isWhite);
            case Queen.ID:
                return new Queen(position, isWhite);
            case Rook.ID:
                return new Rook(position, isWhite);
            case Knight.ID:
                return new Knight(position, isWhite);
            case Bishop.ID:
                return new Bishop(position, isWhite);
            case Pawn.ID:
                return new Pawn(position, isWhite);
            default:
                return null;
        }
    }
    
    public static long[][] rayAttacks = new long[8][64];

    
    private static void initRayAttacks() {
        rayAttacks = new long[8][64];
        for (int i = 0; i < 8; i++) {
            for (int sq = 0; sq < 64; sq++) {
                rayAttacks[i][sq] = generateRayAttack(i, sq);
            }
        }
    }
    
    protected long getRayAttacks(final Board context, int[] directions) {
        int curpos = getPosition().index();
        long occupation = context.getOccupiedBits();
        long attackRay, blocker;
        int behind;
        long attacks = 0L;
        for (int dir : directions) {
            attackRay = rayAttacks[dir][curpos];
            blocker = attackRay & occupation;
            if (blocker != 0L) {
                if (isNegativeRay(dir)) {
                    behind = Bitwise.highestBitIndex(blocker);
                } else {
                    behind = Bitwise.lowestBitIndex(blocker);
                }
                attacks |= attackRay ^ rayAttacks[dir][behind];
            } else {
                attacks |= attackRay;
            }
        }
        return attacks;
    }
    
    private static long generateRayAttack(int direction, int sq) {
        long pos0 = 1L << sq;
        long pt = shift(direction, pos0);
        long ray = 0L;
        while (pt != 0L) {
            ray |= pt;
            pt = shift(direction,pt);
        }
        return ray;
    }
    
    static {
        initRayAttacks();
    }
    

}
