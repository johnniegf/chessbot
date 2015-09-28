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
    public static final int ID = 0;

    public int id() {
        return ID;
    }

    public long getAttackBits(final Board context) {
        return 0L;
    }
    
    public long getMoveBits(final Board context) {
        return 0L;
    }

    public Collection<Move> getMoves(final Board context) {
        return new ArrayList<Move>();
    }

    protected char fen() {
        return 'K';
    }

    protected King create() {
        return new King();
    }

}
