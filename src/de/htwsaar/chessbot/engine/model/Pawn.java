package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;

import java.util.Collection;
import java.util.ArrayList;
/**
* Der Bauer.
*
* <ul>
* <li>Der Bauer kann einen Schritt nach vorne ziehen, wenn das Zielfeld 
* leer ist. </li>
*
* <li>Wurde der Bauer noch nicht gezogen und befindet sich somit noch in der 
* Ausgangsstellung, kann er wahlweise auch zwei Schritte vorrücken, 
* sofern das Feld vor ihm und das Zielfeld leer sind.</li>
*
* <li>Der Bauer schlägt vorwärts diagonal ein Feld weit. Ist ein diagonal 
* vor ihm liegendes Feld jedoch leer, kann er nicht darauf ziehen (außer 
* bei einem en-passant-Schlag). Er ist der einzige Spielstein, der in 
* eine andere Richtung schlägt als er zieht.</li>
*
* <li>Der Bauer kann als einziger Spielstein en passant schlagen. Hat ein 
* gegnerischer Bauer im unmittelbar vorausgehenden gegnerischen Halbzug 
* einen Doppelschritt gemacht und steht ein eigener Bauer so, dass er das 
* dabei übersprungene Feld angreift, kann er den gegnerischen Bauern so 
* schlagen, als ob dieser nur ein Feld aus der Ausgangsstellung 
* vorgerückt wäre.</li>
*
* <li>Wenn ein Bauer die gegnerische Grundreihe betritt, so muss er als 
* Bestandteil dieses Zuges in eine Dame, einen Turm, einen Läufer 
* oder einen Springer der eigenen Farbe umgewandelt werden. Der Bauer wird 
* aus dem Spiel genommen, und auf das Feld, auf das der Bauer in diesem Zug 
* gezogen wurde, wird die neue Figur gesetzt. Die Eigenschaften der neuen 
* Figur treten sofort in Kraft, dies kann auch zum unmittelbaren Schachmatt
* führen. Die Umwandlung ist nicht davon abhängig, ob die ausgewählte Figur 
* im Laufe des Spiels geschlagen wurde. Durch Umwandlung kann ein Spieler 
* also mehr Exemplare einer Figurenart bekommen, als in der Grundstellung 
* vorhanden sind.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Pawn
     extends AbstractPiece
{
    public static final long HASH = 0x2916b7a5c26f1e7bL;

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
            if (pt.isValid()) 
                attacks.add(pt);
        }   
        return attacks;
    }

    private Collection<Position> getTargets(final Board context) {
        Collection<Position> targets = new ArrayList<Position>();

        Position p0 = getPosition();
        Position pt;
        byte increment = (isWhite() ? (byte) 1 : (byte) -1);
        pt = p0.transpose(0, increment);
        if (pt.isValid() && context.isFree(pt)) {
                targets.add(pt);
            if (!hasMoved()) {
                pt = p0.transpose(0, 2*increment);
                if (pt.isValid() && context.isFree(pt))
                    targets.add(pt);
            }
        }
        return targets;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        if (context.isWhiteAtMove() == isWhite()) {
            Position myPos = getPosition();
            for (Position p : getAttacks(context)) {
                if (context.isFree(p)) {
            	    if (p == context.getEnPassant())
            		    moves.add( MV(myPos,p,MoveEnPassant.FLAG) );
                    continue;
                }
                if (context.getPieceAt(p).isWhite() == isWhite())
                    continue;
    
                if (isPromotion(myPos,p)) {
                    moves.addAll(getPromotions(myPos, p));
                } else
            		moves.add( MV(myPos,p) );
            }
            for (Position p : getTargets(context)) {
            	if ( Math.abs(myPos.rank() - p.rank()) == 2 )
            		moves.add( MV(myPos,p,DoublePawnMove.FLAG) );
            	else {
                    if (isPromotion(myPos,p)) {
                        moves.addAll(getPromotions(myPos, p));
                    } else
                	    moves.add( MV(myPos,p) );
                }
            }
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
   
    private Collection<Move> getPromotions(final Position from, final Position to) {
        Collection<Move> promotions = new ArrayList<Move>(4);
        promotions.add( MV( from, to, MovePromotion.TO_QUEEN ) );
        promotions.add( MV( from, to, MovePromotion.TO_ROOK ) );
        promotions.add( MV( from, to, MovePromotion.TO_KNIGHT ) );
        promotions.add( MV( from, to, MovePromotion.TO_BISHOP ) );
        return promotions;
    }

    private boolean isPromotion(final Position start, final Position target) {
    	if ( isWhite() ) {
    		if (start.rank() == 7 && target.rank() == 8) return true;
    	} else {
    		if (start.rank() == 2 && target.rank() == 1) return true;
        }
    	
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
        hash += super.hashCode();
        return hash;
    }

    protected Pawn create() {
        return new Pawn();
    }
}
