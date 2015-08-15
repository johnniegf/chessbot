package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;

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
    public static final long HASH = 0x7916b7a5c26f1e7bL;

    public long id() {
        return HASH;
    }

    public Collection<Position> getAttacks(final Board context) {
        Collection<Position> attacks = new ArrayList<Position>();
        
        Position p0 = getPosition();
        Position pt;
        for (int m = -1; m <= 1; m += 2) {
            for (int n = -2; n <= 2; n += 4) {
                pt = p0.transpose(m,n);
                if (pt.isValid())
                    attacks.add(pt);
                
                pt = p0.transpose(n,m);
                if (pt.isValid())
                    attacks.add(pt);
            }
        }
        return attacks;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        if (context.isWhiteAtMove() == isWhite()) {
            Position myPos = getPosition();
            Piece pc;
            for (Position p : getAttacks(context)) {
                if (!context.isFree(p)) {
                    pc = context.getPieceAt(p);
                    if (pc.isWhite() == isWhite())
                        continue;
                }
                moves.add( MV(myPos,p) );
            }
        }
        return moves;
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

    protected Knight create() {
        return new Knight();
    }

}
