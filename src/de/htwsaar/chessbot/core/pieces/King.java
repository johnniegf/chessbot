package de.htwsaar.chessbot.core.pieces;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.BoardException;
import de.htwsaar.chessbot.core.BitBoardUtils;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.invert;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.toColor;
import static de.htwsaar.chessbot.core.BitBoardUtils.East;
import static de.htwsaar.chessbot.core.BitBoardUtils.North;
import static de.htwsaar.chessbot.core.BitBoardUtils.South;
import static de.htwsaar.chessbot.core.BitBoardUtils.West;
import static de.htwsaar.chessbot.core.BitBoardUtils.shift;
import de.htwsaar.chessbot.core.Position;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.core.moves.Move.MV;
import static de.htwsaar.chessbot.core.Position.P;
import de.htwsaar.chessbot.core.moves.CastlingMove;
import static de.htwsaar.chessbot.core.moves.Move.MV;
import de.htwsaar.chessbot.util.Bitwise;

import java.util.Collection;
import java.util.ArrayList;
/**
* Der König.
*
* <ul>
* <li>Der König kann horizontal, vertikal oder diagonal auf das unmittelbar 
* angrenzende Feld ziehen. Die beiden Könige können nie direkt nebeneinander 
* stehen, da sie einander bedrohen würden und ein König nicht auf ein 
* bedrohtes Feld ziehen darf.</li>
*
* <li>Bei der Rochade werden mit König und Turm nicht nur zwei Figuren in 
* einem Zug bewegt, es ist auch der einzige Zug, bei dem der König zwei 
* Felder ziehen darf. Beide dürfen im bisherigen Spielverlauf noch nie 
* bewegt worden sein, damit die Rochade zulässig ist. Es dürfen auch keine 
* anderen Figuren zwischen König und Turm stehen. Der König zieht zwei Felder 
* in Richtung des Turms, und dieser springt auf das Feld, das der König 
* eben überquert hat. Die Rochade ist außerdem nicht möglich, wenn der König 
* bedroht ist oder beim Rochieren über ein bedrohtes Feld hinweg ziehen würde.
* </li>
* </ul>
*
* @author Kevin Alberts
* @author Dominik Becker
* @author Johannes Haupt
*/
public class King
     extends AbstractPiece
{
    public static final int ID = 0;
    
    private final long mAttackMask;
    
    public King(final Position position, final boolean isWhite) {
        super(position,isWhite);
        mAttackMask = calculateAttackMask();
    }
    
    private long calculateAttackMask() {
        long position = getPosition().toBitBoard();
        long attacks = shift(East, position) | shift(West, position);
        long row = attacks | position;
        attacks |= shift(North, row) | shift(South, row);
        return attacks;
    }

    @Override
    public final int id() {
        return ID;
    }

    @Override
    public final long getAttackBits(final Board context) {
        return mAttackMask;
    }
    
    public final long getNormalMoveBits(final Board context) {
        if (context.isWhiteAtMove() != isWhite())
            return 0L;
        long moveBits = getAttackBits(context);
        return moveBits ^ context.getAttackedBits(moveBits, !isWhite());
    }
    
    @Override
    public final long getMoveBits(final Board context) {
        return getNormalMoveBits(context) | getCastlingBits(context);
    }
    
    private boolean canCastle() {
        return (getPosition().toBitBoard() & CASTLING_START_MASK) != 0L;
    } 
    
    @Override
    public final Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        Position from, to;
        from = getPosition();
        long moveBits = getNormalMoveBits(context);
        long current;
        while (moveBits != 0L) {
            current = Bitwise.lowestBit(moveBits);
            to = Position.BB(current);
            moves.add( MV(from,to) );
            moveBits ^= current;
        }
        moves.addAll(getCastlingMoves(context));
        return moves;
    }
    
    private long getCastlingBits(final Board context) {
        if (!canCastle())
            return 0;
        long result = 0L;
        int color = toColor(isWhite());
        byte castlings = context.getCastlings();
        long occupation = context.getOccupiedBits();
        for (int side = QUEENSIDE; side <= KINGSIDE; side++) {
            if ((castlings & MOVE_FLAGS[color][side]) == 0L)
                continue;
            long obstructed = occupation & CASTLING_MASKS[color][side];
            if (obstructed != 0L)
                continue;
            long attacked = ATTACK_MASK & CASTLING_MASKS[color][side];
            attacked |= getPosition().toBitBoard();
            if (context.getAttackedBits(attacked, !isWhite()) != 0L ) {
                continue;
            }

            result |= CASTLING_TARGETS[color][side];
        }
        return result;
    }
    
    private Collection<Move> getCastlingMoves(final Board context) {
        Collection<Move> castlings = new ArrayList<Move>();
        int c = toColor(isWhite());
        long bits = getCastlingBits(context);
        for (int side = QUEENSIDE; side <= KINGSIDE; side++) {
            if ((bits & CASTLING_TARGETS[c][side]) != 0L) {
                castlings.add( Move.MV(Position.INVALID, 
                                       Position.INVALID, 
                                       MOVE_IDS[c][side]));
            }
        }
        return castlings;
    }

    @Override
    protected final char fen() {
        return 'K';
    }
    
    private static byte getFlag(final int color, final int side) {
        return MOVE_IDS[color][side];
    }
    
    
    private static final int QUEENSIDE = 0;
    private static final int KINGSIDE = 1;
    
    private static final byte[][] MOVE_IDS = new byte[2][2];
    private static final byte[][] MOVE_FLAGS = new byte[2][2];
    private static final long[][] CASTLING_MASKS = new long[2][2];
    private static final long[][] CASTLING_ROOKS = new long[2][2];
    private static final long[][] CASTLING_TARGETS = new long[2][2];
    private static final long CASTLING_START_MASK = 0x1000_0000_0000_0010L;
    private static final long ATTACK_MASK = 0x6c00_0000_0000_006cL;
    
    static {
        MOVE_IDS[WHITE][QUEENSIDE] = CastlingMove.W_QUEENSIDE;
        MOVE_IDS[WHITE][KINGSIDE]  = CastlingMove.W_KINGSIDE;
        MOVE_IDS[BLACK][QUEENSIDE] = CastlingMove.B_QUEENSIDE;
        MOVE_IDS[BLACK][KINGSIDE]  = CastlingMove.B_KINGSIDE;
        
        MOVE_FLAGS[WHITE][QUEENSIDE] = Board.CASTLING_W_QUEEN;
        MOVE_FLAGS[WHITE][KINGSIDE] = Board.CASTLING_W_KING;
        MOVE_FLAGS[BLACK][QUEENSIDE] = Board.CASTLING_B_QUEEN;
        MOVE_FLAGS[BLACK][KINGSIDE] = Board.CASTLING_B_KING;
        
        CASTLING_MASKS[WHITE][QUEENSIDE] = 0x0000_0000_0000_000eL;
        CASTLING_MASKS[WHITE][KINGSIDE]  = 0x0000_0000_0000_0060L;
        CASTLING_MASKS[BLACK][QUEENSIDE] = 0x0e00_0000_0000_0000L;
        CASTLING_MASKS[BLACK][KINGSIDE]  = 0x6000_0000_0000_0000L;
        
        CASTLING_ROOKS[WHITE][QUEENSIDE] = 0x0000_0000_0000_0001L;
        CASTLING_ROOKS[WHITE][KINGSIDE]  = 0x0000_0000_0000_0080L;
        CASTLING_ROOKS[BLACK][QUEENSIDE] = 0x0100_0000_0000_0000L;
        CASTLING_ROOKS[BLACK][KINGSIDE]  = 0x8000_0000_0000_0000L;
        
        CASTLING_TARGETS[WHITE][QUEENSIDE] = 0x0000_0000_0000_0004L;
        CASTLING_TARGETS[WHITE][KINGSIDE]  = 0x0000_0000_0000_0040L;
        CASTLING_TARGETS[BLACK][QUEENSIDE] = 0x0400_0000_0000_0000L;
        CASTLING_TARGETS[BLACK][KINGSIDE]  = 0x4000_0000_0000_0000L;
    }

}
