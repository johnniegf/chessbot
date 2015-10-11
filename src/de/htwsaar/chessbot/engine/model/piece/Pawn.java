package de.htwsaar.chessbot.engine.model.piece;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.toColor;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.North;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.NorthEast;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.NorthWest;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.South;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.SouthEast;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.SouthWest;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.shift;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.move.PromotionMove;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.move.DoublePawnMove;
import de.htwsaar.chessbot.engine.model.move.EnPassantMove;
import static de.htwsaar.chessbot.engine.model.move.Move.MV;
import de.htwsaar.chessbot.engine.model.move.StandardMove;
import de.htwsaar.chessbot.util.Bitwise;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
/**
* Der Bauer.
*
* <ul>
* <li>Der Bauer kann einen Schritt nach vorne ziehen, wenn das Zielfeld 
* leer ist. </li>
*
* <li>Wurde der Bauer noch nicht gezogen und befindet sich somit noch in der 
* Ausgangsstellung, kann er wahlweise auch zwei Schritte vorrücken, 
* sofern das Feld vor ihm und das Zielfeld leer sind.</li>
*
* <li>Der Bauer schlägt vorwärts diagonal ein Feld weit. Ist ein diagonal 
* vor ihm liegendes Feld jedoch leer, kann er nicht darauf ziehen (außer 
* bei einem en-passant-Schlag). Er ist der einzige Spielstein, der in 
* eine andere Richtung schlägt als er zieht.</li>
*
* <li>Der Bauer kann als einziger Spielstein en passant schlagen. Hat ein 
* gegnerischer Bauer im unmittelbar vorausgehenden gegnerischen Halbzug 
* einen Doppelschritt gemacht und steht ein eigener Bauer so, dass er das 
* dabei übersprungene Feld angreift, kann er den gegnerischen Bauern so 
* schlagen, als ob dieser nur ein Feld aus der Ausgangsstellung 
* vorgerückt wäre.</li>
*
* <li>Wenn ein Bauer die gegnerische Grundreihe betritt, so muss er als 
* Bestandteil dieses Zuges in eine Dame, einen Turm, einen Läufer 
* oder einen Springer der eigenen Farbe umgewandelt werden. Der Bauer wird 
* aus dem Spiel genommen, und auf das Feld, auf das der Bauer in diesem Zug 
* gezogen wurde, wird die neue Figur gesetzt. Die Eigenschaften der neuen 
* Figur treten sofort in Kraft, dies kann auch zum unmittelbaren Schachmatt
* führen. Die Umwandlung ist nicht davon abhängig, ob die ausgewählte Figur 
* im Laufe des Spiels geschlagen wurde. Durch Umwandlung kann ein Spieler 
* also mehr Exemplare einer Figurenart bekommen, als in der Grundstellung 
* vorhanden sind.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Pawn
     extends AbstractPiece
{
    public static final int ID = 5;
    
    private final long mPushMask;

    public Pawn(final Position position, final boolean isWhite) {
        super(position,isWhite);
        if (position.rank() == 7 && !isWhite) {
            mPushMask = PAWN_PUSH_MASKS[1][position.file()-1];
        } else if (position.rank() == 2 && isWhite) {
            mPushMask = PAWN_PUSH_MASKS[0][position.file()-1];
        } else {
            mPushMask = 0L;
        }
    }
    
    public int id() {
        return ID;
    }

    public long getAttackBits(final Board context) {
        return ATTACK_MASKS[isWhite() ? 0 : 1][getPosition().index()];
    }
    
    public long getMoveBits(final Board context) {
        if (context.isWhiteAtMove() != isWhite())
            return 0L;
        return getNormalTarget(context)
             | getCaptureTargets(context)
             | getPushTarget(context);
    }

    @Override
    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        moves.addAll(getCapturingMoves(context));
        moves.addAll(getNormalMove(context));
        Move pushMove = getPushMove(context);
        if (pushMove != null)
            moves.add(pushMove );
        return moves;
    }

    private boolean isPromotion(final Position start) {
    	return start.rank() == (isWhite() ? 7 : 2);
    }

    @Override
    protected char fen() {
        return 'P';
    }
    
    private boolean canPush() {
        return getPushMask() != 0L;
    }
    
    private long getPushMask() {
        return mPushMask;
    }

    private long getCaptureTargets(Board context) {
        int c = toColor(isWhite());
        return ATTACK_MASKS[c][getPosition().index()] 
             & (context.getPieceBitsForColor(!isWhite()) 
              | (context.getEnPassant().toBitBoard() & EP_MASKS[c])
             );
    }
    
    private Collection<Move> getCapturingMoves(Board context) {
        long possibleCaptures = getCaptureTargets(context);
        Collection<Move> captures = new ArrayList<Move>();
        while (possibleCaptures != 0L) {
            int idx = Bitwise.lowestBitIndex(possibleCaptures);
            Position target = Position.P(idx);
            if (isPromotion(getPosition())) {
                captures.addAll( getPromotions(getPosition(), target) ); 
            } else if (target == context.getEnPassant()) {
                captures.add( Move.MV(getPosition(), target, EnPassantMove.TYPE));
            } else {
                captures.add( Move.MV(getPosition(), target, StandardMove.TYPE));
            }
            possibleCaptures = Bitwise.popLowestBit(possibleCaptures);
        }
        return captures;
    }
    
    private long getNormalTarget(Board context) {
        int direction = (isWhite() ? North : South);
        long target = shift(direction, getPosition().toBitBoard());
        return target & ~context.getOccupiedBits();
    }

    private Collection<Move> getNormalMove(Board context) {
        Collection<Move> moves = new ArrayList<Move>();

        Position target = Position.BB(getNormalTarget(context));
        if (!target.isValid())
            return moves;
        
        if (isPromotion(getPosition()))
            return getPromotions(getPosition(), target);
        else
            moves.add( Move.MV(getPosition(), target, StandardMove.TYPE) );
        
        return moves;
    }

    private Move getPushMove(Board context) {
        long push = getPushTarget(context);
        if (push == 0L)
            return null;
        
        return Move.MV(
            getPosition(), 
            Position.BB(push),
            DoublePawnMove.TYPE
        );
    }
    
    private long getPushTarget(final Board context) {
        if (!canPush())
            return 0L;
        
        if ((context.getOccupiedBits() & getPushMask()) != 0L)
            return 0L;
        
        return TARGET_SQUARE_MASK & getPushMask();
    }
    
    private static Collection<Move> getPromotions(final Position start,
                                                  final Position target)
    {
        return Arrays.asList( new Move[] {
            Move.MV(start, target, PromotionMove.TO_BISHOP),
            Move.MV(start, target, PromotionMove.TO_KNIGHT),
            Move.MV(start, target, PromotionMove.TO_ROOK),
            Move.MV(start, target, PromotionMove.TO_QUEEN)
        });
    }
    
    private static final long[][] ATTACK_MASKS = new long[2][64];
    private static final long[] EP_MASKS = new long[2];
    private static final long[] PROMOTION_MASKS = new long[2];
    
    private static final long[][] PAWN_PUSH_MASKS = new long[2][8];
    private static final long TARGET_SQUARE_MASK = 0x0000_00ff_ff00_0000L;
    static {
        
        // init white attack masks
        for (int i = 0; i < 64; i++) {
            long p0 = Position.P(i).toBitBoard();
            long attacks = shift(NorthWest, p0) | shift(NorthEast, p0);
            ATTACK_MASKS[WHITE][i] = attacks;
            attacks = shift(SouthWest, p0) | shift(SouthEast, p0);
            ATTACK_MASKS[BLACK][i] = attacks;
        }
        
        long whiteMask = 0x0000_0000_0101_0000L;
        long blackMask = 0x0000_0101_0000_0000L;
        for (int i = 0; i < 8; i++) {
            PAWN_PUSH_MASKS[WHITE][i] = whiteMask << i;
            PAWN_PUSH_MASKS[BLACK][i] = blackMask << i;
        }
        
        EP_MASKS[WHITE] = 0x0000_ff00_0000_0000L;
        EP_MASKS[BLACK] = 0x0000_0000_00ff_0000L;
        
        PROMOTION_MASKS[WHITE] = 0xff00_0000_0000_0000L;
        PROMOTION_MASKS[BLACK] = 0x0000_0000_0000_00ffL;
    }
}
