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
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if ( Math.abs(dx) == Math.abs(dy) )
                    continue;
                pt = p0;
                while(true) {
                    pt = pt.transpose(dx,dy);
                    if ( !pt.isValid() )
                        break;
                    
                    attacks.add(pt);
                    if (!context.isFree(pt)) 
                        break;
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
    
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            Piece op = (Piece) other;
            return hasMoved() == op.hasMoved();
        }
        return false;
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
