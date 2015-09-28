package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;
import de.htwsaar.chessbot.util.Bitwise;

import java.util.Collection;
import java.util.ArrayList;
/**
* Die Dame.
*
* <ul>
* <li>Die Dame darf in horizontaler, vertikaler und diagonaler Richtung 
* beliebig weit ziehen, ohne jedoch über andere Figuren zu springen. Sie 
* vereint somit die Zugmöglichkeiten eines Turms und eines Läufers in sich.
* </li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Queen 
     extends AbstractPiece
{
    public static final int ID = 1;

    public int id() {
        return ID;
    }
    
    public long getAttackBits(final Board context) {
        long attacks = 0L;
        Piece rook = Pieces.PC(Rook.ID, isWhite(), getPosition());
        Piece bishop = Pieces.PC(Bishop.ID, isWhite(), getPosition());
        return bishop.getAttackBits(context) | rook.getAttackBits(context);
    }


    
    protected char fen() {
        return 'Q';
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

    protected Queen create() {
        return new Queen();
    }

}
