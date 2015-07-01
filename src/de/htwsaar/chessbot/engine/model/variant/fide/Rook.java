package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.Piece;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.Board;

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
public final class Rook extends Piece {

    public Rook() {
        super();
    }

    public Rook(final Position position) {
        super(position);
    }

    public Rook(final Position position, 
                final boolean isWhite) 
    {
        super(position, isWhite);
    }

    public Rook(final Position position, 
                final boolean isWhite, 
                final boolean hasMoved) 
    {
        super(position, isWhite, hasMoved);
    }
    
    public int hashCode() {
        return super.hashCode() * (hasMoved() ? 61 : 67);
    }

    public final Collection<Position> getValidTargets(final Board context) {
        Collection<Position> possibleMoves = new ArrayList<Position>(14);
        Position p = getPosition();
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == j || i == -j) continue;
                int c = 1;
                p = getPosition().transpose(c*i, c*j);
                while(p.existsOn(context)) 
                {
                    if (context.isFree(p))
                       possibleMoves.add(p);
                    else if (context.pieceAt(p).isWhite() != isWhite()) {
                        possibleMoves.add(p);
                        break;
                    } else {
                        break;
                    }
                    c += 1;
                    p = getPosition().transpose(c*i, c*j);
               } 
            }
        }
        return possibleMoves;
    }

    public final String getName() {
        return "Turm";
    }

    public final String toSAN() {
        return "R";
    }
    
    public final Rook clone() {
        return new Rook(getPosition().clone(), isWhite(), hasMoved());
    }
}

