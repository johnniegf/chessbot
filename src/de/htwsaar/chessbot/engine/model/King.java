package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;
import static de.htwsaar.chessbot.engine.model.Position.P;

import java.util.Collection;
import java.util.ArrayList;
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

    public Collection<Position> getAttacks(final Board context) {
        Collection<Position> attacks = new ArrayList<Position>();
        
        Position p0 = getPosition();
        Position pt;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dy == 0 && dx == 0)
                    continue;
                pt = p0.transpose(dx,dy);
                if (pt.isValid())
                    attacks.add(pt);
            }
        }
        return attacks;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        Position myPos = getPosition();
        for (Position p : getAttacks(context)) {
            if ( context.isAttacked(!isWhite(), p) > 0 )
                continue;
            if ( !context.isFree(p) ) {
                if (context.getPieceAt(p).isWhite() == isWhite())
                    continue;
            }
            moves.add( MV(myPos,p) );
        }
        moves.addAll(getCastlings(context));
        return moves;
    }

    private Collection<Move> getCastlings(final Board context) {
        Collection<Move> castlings = new ArrayList<Move>();
        if ( !hasMoved() ) {
        	Position p = getPosition();
        	for (int i : new int[]{1,8}) {
        		Position r = P(i,p.rank());
        		Piece rook = context.getPieceAt(r);
        		if ( rook == null ) continue;
        		if ( rook.hasMoved() ) continue;
        		if ( rook.isWhite() != isWhite() ) continue;
        		int d = r.compareTo(p) < 0 ? -1 : 1;
                boolean possible = true;
        		for (int c = 1; c <= 2; c++) {
                    if ( !context.isFree(p.transpose(c*d,0)) )
                        possible = false;
        			if ( 0 < context.isAttacked(!isWhite(), p.transpose(c*d, 0)) )
        				possible = false;
        		}
                if (possible)
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

}
