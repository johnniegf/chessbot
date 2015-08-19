package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;
import static de.htwsaar.chessbot.engine.model.Position.P;

import java.util.Collection;
import java.util.ArrayList;
/**
* Der König.
*
* <ul>
* <li>Der König kann horizontal, vertikal oder diagonal auf das unmittelbar 
* angrenzende Feld ziehen. Die beiden Könige können nie direkt nebeneinander 
* stehen, da sie einander bedrohen würden und ein König nicht auf ein 
* bedrohtes Feld ziehen darf.</li>
*
* <li>Bei der Rochade werden mit König und Turm nicht nur zwei Figuren in 
* einem Zug bewegt, es ist auch der einzige Zug, bei dem der König zwei 
* Felder ziehen darf. Beide dürfen im bisherigen Spielverlauf noch nie 
* bewegt worden sein, damit die Rochade zulässig ist. Es dürfen auch keine 
* anderen Figuren zwischen König und Turm stehen. Der König zieht zwei Felder 
* in Richtung des Turms, und dieser springt auf das Feld, das der König 
* eben überquert hat. Die Rochade ist außerdem nicht möglich, wenn der König 
* bedroht ist oder beim Rochieren über ein bedrohtes Feld hinweg ziehen würde.
* </li>
* </ul>
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
        if (context.isWhiteAtMove() == isWhite()) {
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
   

    private Collection<Move> getCastlings(final Board context) {
        Collection<Move> castlings = new ArrayList<Move>();
        if ( !hasMoved() 
          && 0 == context.isAttacked(!isWhite(), getPosition()) )
        {
        	Position p = getPosition();
        	for (int i : new int[]{1,8}) {
        		Position r = P(i,p.rank());
        		Piece rook = context.getPieceAt(r);
        		if ( rook == null ) continue;
        		if ( rook.hasMoved() ) continue;
        		if ( rook.isWhite() != isWhite() ) continue;
        		int d = r.compareTo(p) < 0 ? -1 : 1;
                boolean possible = true;
                Position curr = null;
        		for (int c = 1; c <= 2; c++) {
                    curr = p.transpose(c*d,0);
                    if ( !context.isFree(curr) )
                        possible = false;
        			if ( 0 < context.isAttacked(!isWhite(), curr) )
        				possible = false;
        		}
                if (possible)
        		    castlings.add( MV(p, curr, Castling.FLAG) );
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
