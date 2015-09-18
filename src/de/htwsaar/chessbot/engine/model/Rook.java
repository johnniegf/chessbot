package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Move.MV;

import java.util.Collection;
import java.util.ArrayList;
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
    public int id() {
        return ID;
    }

    public Collection<Position> getAttacks(final Board context) {
        Collection<Position> attacks = new ArrayList<Position>();
        
        Position p0 = getPosition();
        Position pt;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if ( Math.abs(dx) == Math.abs(dy) )
                    continue;
                pt = p0;
                while(true) {
                    pt = pt.transpose(dx,dy);
                    if ( !pt.isValid() )
                        break;
                    
                    attacks.add(pt);
                    if (!context.isFree(pt)) 
                        break;
                }
            }
        }
        return attacks;
    }

    public Collection<Move> getMoves(final Board context) {
        Collection<Move> moves = new ArrayList<Move>();
        if (context.isWhiteAtMove() == isWhite()) {
            Position myPos = getPosition();
            for (Position p : getAttacks(context)) {
                if (!context.isFree(p))
                    if (context.getPieceAt(p).isWhite() == isWhite())
                        continue;

                moves.add( MV(myPos,p) );
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
   

    protected char fen() {
        return 'R';
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

    protected Rook create() {
        return new Rook();
    }

}
