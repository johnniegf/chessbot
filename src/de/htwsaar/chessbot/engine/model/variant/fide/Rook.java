package de.htwsaar.chessbot.engine.model.variant.fide;

import static de.htwsaar.chessbot.engine.model.Move.M;
import de.htwsaar.chessbot.engine.model.*;
import java.util.Collection;
import java.util.ArrayList;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Rook
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
        boolean hitEnd = false;
        int c;
        for (int vert = 0; vert <= 1; vert++) {
            for (int d = -1; d <= 1; d += 2) {
                c = 1;
                while(true) {
                    pt = (vert == 0 ? p0.transpose(0,d*c) 
                                    : p0.transpose(d*c, 0));
                    if ( !pt.existsOn(context) )
                        break;
                    else if (!context.isFree(pt)) {
                        if (context.getPieceAt(pt).isWhite() != isWhite())
                            attacks.add(pt);
                         break;
                    } else {
                        attacks.add(pt);
                    }
                    c += d;
                }
            }
        }
        return attacks;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        Position myPos = getPosition();
        for (Position p : getAttacks(context)) {
            moves.add( M(myPos,p) );
        }
        return moves;
    }

    protected char fen() {
        return 'R';
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

    protected Rook create() {
        return new Rook();
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
}
