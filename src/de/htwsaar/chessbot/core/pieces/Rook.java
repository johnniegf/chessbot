package de.htwsaar.chessbot.core.pieces;

import de.htwsaar.chessbot.core.Board;
import static de.htwsaar.chessbot.core.BitBoardUtils.East;
import static de.htwsaar.chessbot.core.BitBoardUtils.North;
import static de.htwsaar.chessbot.core.BitBoardUtils.South;
import static de.htwsaar.chessbot.core.BitBoardUtils.West;
import de.htwsaar.chessbot.core.Position;
/**
* Der Turm.
*
* <ul>
* <li>Ein Turm darf auf Linien und Reihen, also horizontal und vertikal, 
* beliebig weit ziehen, ohne jedoch über andere Figuren zu springen. 
* Die einzige Ausnahme davon ist die Rochade, bei der Turm und König 
* bewegt werden. Ein Turm hat, wie Dame und Läufer, eine nur durch 
* den Spielfeldrand begrenzte Reichweite.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Rook
     extends AbstractPiece
{
    public static final int ID = 2;
    
    private static final int[] directions = new int[] {
        North, West, South, East
    };
    
    public Rook(final Position position, final boolean isWhite) {
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

    protected char fen() {
        return 'R';
    }
}
