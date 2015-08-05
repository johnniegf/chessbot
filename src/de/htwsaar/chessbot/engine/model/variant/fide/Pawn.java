package de.htwsaar.chessbot.engine.model.variant.fide;

import static de.htwsaar.chessbot.engine.model.ChessVariant.MV;
import de.htwsaar.chessbot.engine.model.*;

import java.util.Set;
import java.util.HashSet;
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

    public Set<Position> getAttacks(final Board context) {
        Set<Position> attacks = new HashSet<Position>();
        
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

    private Set<Position> getTargets(final Board context) {
        Set<Position> targets = new HashSet<Position>();

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

    public Set<Move> getMoves(final Board context) {
        Set<Move> moves = new HashSet<Move>();
        Position myPos = getPosition();
        char flag = (isPromotion(myPos) ? MovePromotion.FLAG : Move.FLAG);
        for (Position p : getAttacks(context)) {
        	if (p.equals(context.getEnPassant()))
        		moves.add( MV(myPos,p,MoveEnPassant.FLAG) );
        	else
        		moves.add( MV(myPos,p,flag) );
        }
        for (Position p : getTargets(context)) {
        	if ( Math.abs(myPos.rank() - p.rank()) == 2 )
        		moves.add( MV(myPos,p,DoublePawnMove.FLAG) );
        	else
        		moves.add( MV(myPos,p,flag) );
        }
        return moves;
    }
    
    private boolean isPromotion(final Position start) {
    	if ( isWhite() )
    		if (start.rank() == 7) return true;
    	else
    		if (start.rank() == 2) return true;
    	
    	return false;
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
}
