package de.htwsaar.chessbot.engine.model.piece;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.move.PromotionMove;
import de.htwsaar.chessbot.engine.model.Position;
import static de.htwsaar.chessbot.engine.model.move.Move.MV;

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
    public static final int ID = 5;

    public Pawn(final Position position, final boolean isWhite) {
        super(position,isWhite);
    }
    
    public int id() {
        return ID;
    }

    public long getAttackBits(final Board context) {
        return 0L;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        moves.addAll( getCaptures(context) );
        moves.add( getNormalAdvance(context) );
        moves.add( getDoubleAdvance(context) );
        return moves;
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

    private Collection<? extends Move> getCaptures(Board context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Move getNormalAdvance(Board context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Move getDoubleAdvance(Board context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static final long[][] PAWN_PUSH_MASKS = new long[2][8];
    static {
        long whiteMask = 0x0101_0000L;
        long blackMask = 0x0101_0000L;
        for (int i = 0; i < 8; i++) {
            PAWN_PUSH_MASKS[0][i] = whiteMask << i;
            PAWN_PUSH_MASKS[1][i] = blackMask << i;
        }
    }
}
