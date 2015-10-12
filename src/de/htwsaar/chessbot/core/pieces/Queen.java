package de.htwsaar.chessbot.core.pieces;

import de.htwsaar.chessbot.core.Board;
import static de.htwsaar.chessbot.core.BitBoardUtils.East;
import static de.htwsaar.chessbot.core.BitBoardUtils.North;
import static de.htwsaar.chessbot.core.BitBoardUtils.NorthEast;
import static de.htwsaar.chessbot.core.BitBoardUtils.NorthWest;
import static de.htwsaar.chessbot.core.BitBoardUtils.South;
import static de.htwsaar.chessbot.core.BitBoardUtils.SouthEast;
import static de.htwsaar.chessbot.core.BitBoardUtils.SouthWest;
import static de.htwsaar.chessbot.core.BitBoardUtils.West;
import de.htwsaar.chessbot.core.Position;
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

    private static final int[] directions = new int[] {
        North, NorthEast,
        East, SouthEast,
        South, SouthWest,
        West, NorthWest
    };
    
    public Queen(final Position position, final boolean isWhite) {
        super(position,isWhite);
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
        return 'Q';
    }

}
