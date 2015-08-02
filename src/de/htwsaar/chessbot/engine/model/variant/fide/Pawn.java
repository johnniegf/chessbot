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
public class Pawn
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
        byte increment = (isWhite() ? (byte) 1 : (byte) -1);
        for (byte d = -1; d <= 1; d += 2) {
            pt = p0.transpose(d, increment);
            if (pt.existsOn(context)) 
                attacks.add(pt);
        }   
        return attacks;
    }

    private Collection<Position> getTargets(final Board context) {
        Collection<Position> targets = new ArrayList<Position>(2);

        Position p0 = getPosition();
        Position pt;
        byte increment = (isWhite() ? (byte) 1 : (byte) -1);
        pt = p0.transpose(0, increment);
        if (pt.existsOn(context) && context.isFree(pt))
            targets.add(pt);
        if (!hasMoved()) {
            pt = p0.transpose(0, 2*increment);
            if (pt.existsOn(context) && context.isFree(pt))
                targets.add(pt);
        }
        return targets;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        Position myPos = getPosition();
        for (Position p : getAttacks(context)) {
            moves.add( M(myPos,p) );
        }
        for (Position p : getTargets(context)) {
            moves.add( M(myPos,p) );
        }
        return moves;
    }

    protected char fen() {
        return 'P';
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

    protected Pawn create() {
        return new Pawn();
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
