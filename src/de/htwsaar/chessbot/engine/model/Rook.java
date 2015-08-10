package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;

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
        int c;
        for (int vert = 0; vert <= 1; vert++) {
            for (int d = -1; d <= 1; d += 2) {
                pt = p0;
                c = 1;
                while(true) {
                    pt = (vert == 0 ? p0.transpose(0,d*c) 
                                    : p0.transpose(d*c, 0));
                    if ( !pt.isValid() )
                        break;
                    else if (!context.isFree(pt)) {
                        attacks.add(pt);
                        break;
                    } else {
                        attacks.add(pt);
                    }
                    c += 1;
                }
            }
        }
        return attacks;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        Position myPos = getPosition();
        for (Position p : getAttacks(context)) {
            if (!context.isFree(p))
                if (context.getPieceAt(p).isWhite() == isWhite())
                    continue;

            moves.add( MV(myPos,p) );
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

}
