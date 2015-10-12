package de.htwsaar.chessbot.core.pieces;

import de.htwsaar.chessbot.core.Board;
import static de.htwsaar.chessbot.core.BitBoardUtils.NorthEast;
import static de.htwsaar.chessbot.core.BitBoardUtils.NorthWest;
import static de.htwsaar.chessbot.core.BitBoardUtils.SouthEast;
import static de.htwsaar.chessbot.core.BitBoardUtils.SouthWest;
import de.htwsaar.chessbot.core.Position;
import static de.htwsaar.chessbot.core.moves.Move.MV;
import de.htwsaar.chessbot.util.Bitwise;

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
    private static final int[] directions = new int[] { 
        NorthEast,SouthEast,SouthWest,NorthWest 
    };
    
    public Bishop(final Position position, final boolean isWhite) {
        super(position, isWhite);
    }
    
    @Override
    public int id() {
        return ID;
    }
    
    @Override
    public long getAttackBits(final Board context) {
        return getRayAttacks(context, directions);
        
    }

    @Override
    protected char fen() {
        return 'B';
    }
}
