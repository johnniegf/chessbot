package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;

import java.util.Collection;
import java.util.ArrayList;
/**
* Der Läufer.
* <ul>
* <li>Läufer ziehen in diagonaler Richtung beliebig weit über das Brett. Über 
* andere Figuren hinweg dürfen die dabei nicht ziehen.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Bishop
     extends AbstractPiece
{
    public static final int ID = 4;

    public int id() {
        return ID;
    }

    public Collection<Position> getAttacks(final Board context) {
        Collection<Position> attacks = new ArrayList<Position>();
        
        Position p0 = getPosition();
        Position pt;
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                pt = p0;
                while (true) {
                    pt = pt.transpose(dx, dy);
                    if (!pt.isValid())
                        break;
                    attacks.add(pt);
                    if (!context.isFree(pt)) {
                        break;
                    }
                }
            }
        }
        return attacks;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        if (context.isWhiteAtMove() == isWhite()) {
            Position myPos = getPosition();
            for (Position p : getAttacks(context)) {
                if (!context.isFree(p))
                    if (context.getPieceAt(p).isWhite() == isWhite())
                        continue;
            
                moves.add( MV(myPos,p) );
            }
        }
        return moves;
    }

    protected char fen() {
        return 'B';
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

    protected Bishop create() {
        return new Bishop();
    }

}
