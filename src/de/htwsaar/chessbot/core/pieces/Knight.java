package de.htwsaar.chessbot.core.pieces;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.Position;
import static de.htwsaar.chessbot.core.moves.Move.MV;

import java.util.Collection;
import java.util.ArrayList;
/**
* Der Springer.
*
* <ul>
* <li>Der Springer darf auf eines der Felder ziehen, die seinem Standfeld am 
* nächsten, aber nicht auf gleicher Reihe, Linie oder Diagonale mit 
* diesem liegen.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Knight
     extends AbstractPiece
{
    public static final int ID = 3;
    
    private final long mAttackMask;

    public Knight(final Position position, final boolean isWhite) {
        super(position,isWhite);
        mAttackMask = calculateAttackMask();
    }
    
    private long calculateAttackMask() {
        return ATTACK_MASKS[getPosition().index()];
    }
    
    public int id() {
        return ID;
    }
    
    private long getAttackMask() {
        return mAttackMask;
    }

    public long getAttackBits(final Board context) {
        return getAttackMask();
    }

    protected char fen() {
        return 'N';
    }

    /**
    * Gib den Hashwert dieses Objekts aus.
    *
    * @return Hashwert dieses Objekts.
    */
    public int hashCode() {
        int hash = 0;
        // Berechnungen

        return hash;
    }

    private static long[] ATTACK_MASKS = new long[64];
    
    static {
        Position p0,pt;
        long currentAttacks;
        for (int i = 0; i < 64; i++) {
            currentAttacks = 0L;
            p0 = Position.P(i);
            for (int a = -1; a <= 1; a += 2) {
                for (int b = -2; b <= 2; b += 4) {
                    pt = p0.transpose(a, b);
                    if (pt.isValid())
                        currentAttacks |= pt.toBitBoard();
                    
                    pt = p0.transpose(b, a);
                    if (pt.isValid())
                        currentAttacks |= pt.toBitBoard();
                }
            }
            ATTACK_MASKS[i] = currentAttacks;
        }
    }
    
}
