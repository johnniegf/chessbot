package de.htwsaar.chessbot.engine.model.variant.fide;

import static de.htwsaar.chessbot.engine.model.ChessVariant.MV;
import static de.htwsaar.chessbot.engine.model.Position.P;
import de.htwsaar.chessbot.engine.model.*;
import java.util.Set;
import java.util.HashSet;
/**
* Der König.
*
* @author Kevin Alberts
* @author Dominik Becker
* @author Johannes Haupt
*/
public class King
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
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dy == 0 && dx == 0)
                    continue;
                pt = p0.transpose(dx,dy);
                if (pt.existsOn(context))
                    attacks.add(pt);
            }
        }
        return attacks;
    }

    public Set<Move> getMoves(final Board context) {
        Set<Move> moves = new HashSet<Move>();
        Position myPos = getPosition();
        for (Position p : getAttacks(context)) {
            if ( context.isAttacked(!isWhite(), p) > 0 )
                continue;
            else
                moves.add( MV(myPos,p) );
        }
        moves.addAll(getCastlings(context));
        return moves;
    }

    public boolean canMoveTo(final Board context,
                             final Position targetSquare)
    {
        if ( super.canMoveTo(context, targetSquare) )
            return context.isAttacked(!isWhite(), targetSquare) == 0;
        else
            return false;
    }

    private Set<Move> getCastlings(final Board context) {
        Set<Move> castlings = new HashSet<Move>();
        if ( !hasMoved() ) {
        	Position p = getPosition();
        	for (int i : new int[]{1,8}) {
        		Position r = P(i,p.rank());
        		Piece rook = context.getPieceAt(r);
        		if ( rook == null ) continue;
        		if ( rook.hasMoved() ) continue;
        		if ( rook.isWhite() != isWhite() ) continue;
        		int d = p.compareTo(r);
        		for (int c = 1; c <= 2; c++) {
        			if ( 0 < context.isAttacked(!isWhite(), p.transpose(c*d, 0)) )
        				continue;
        		}
        		castlings.add( MV(p, p.transpose(d*2,0), Castling.FLAG) );
        	}
        }
        return castlings;
    }

    protected char fen() {
        return 'K';
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

    protected King create() {
        return new King();
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
