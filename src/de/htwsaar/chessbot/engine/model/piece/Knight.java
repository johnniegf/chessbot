package de.htwsaar.chessbot.engine.model.piece;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import static de.htwsaar.chessbot.engine.model.move.Move.MV;

import java.util.Collection;
import java.util.ArrayList;
/**
* Der Springer.
*
* <ul>
* <li>Der Springer darf auf eines der Felder ziehen, die seinem Standfeld am 
* nächsten, aber nicht auf gleicher Reihe, Linie oder Diagonale mit 
* diesem liegen.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Knight
     extends AbstractPiece
{
    public static final int ID = 3;

    public Knight(final Position position, final boolean isWhite) {
        super(position,isWhite);
    }
    
    public int id() {
        return ID;
    }

    public long getAttackBits(final Board context) {
        return 0L;
    }

    protected char fen() {
        return 'N';
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
