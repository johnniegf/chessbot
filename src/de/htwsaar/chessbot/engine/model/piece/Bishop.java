package de.htwsaar.chessbot.engine.model.piece;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import static de.htwsaar.chessbot.engine.model.move.Move.MV;

import java.util.Collection;
import java.util.ArrayList;
/**
* Der Läufer.
* <ul>
* <li>Läufer ziehen in diagonaler Richtung beliebig weit über das Brett. Über 
* andere Figuren hinweg dürfen die dabei nicht ziehen.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Bishop
     extends AbstractPiece
{
    public static final int ID = 4;

    public Bishop(final Position position, final boolean isWhite) {
        super(position, isWhite);
    }
    
    public int id() {
        return ID;
    }

    public long getAttackBits(final Board context) {
        return 0L;
    }

    protected char fen() {
        return 'B';
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

}
