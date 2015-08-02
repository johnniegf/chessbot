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
public class Queen 
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
        Piece pc;
        for (byte dx = -1; dx <= 1; dx += 2) {
            for (byte dy = -1; dy <= 1; dy += 2) {
                if (dx == 0 && dy == 0) 
                    continue;
                pt = p0;
                while (true) {
                    pt = pt.transpose(dx,dy);
                    if (!pt.existsOn(context))
                        break;

                    if (!context.isFree(pt)) {
                        pc = context.getPieceAt(pt);
                        if (pc.isWhite() == isWhite())
                            break;
                    }
                    attacks.add(pt);
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
        return 'Q';
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

    protected Queen create() {
        return new Queen();
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
