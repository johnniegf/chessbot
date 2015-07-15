package de.htwsaar.chessbot.engine.model;

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
* @author Johannes Haupt
*/
public final class King extends Piece {

    public King() {
        super();
    }

    public King(final Position position) {
        super(position);
    }

    public King(final Position position, 
                final boolean isWhite) 
    {
        super(position, isWhite);
    }

    public King(final Position position, 
                final boolean isWhite, 
                final boolean hasMoved) 
    {
        super(position, isWhite, hasMoved);
    }

    public final Collection<Move> getValidMoves(final Board context) {
        Collection<Move> validPositions = new ArrayList<Move>();
        Position pt, p = getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if ( x == 0 && y == 0 )
                    continue;

                pt = p.transpose(x,y);
                if (!pt.existsOn(context)) continue;
                if (context.isAttacked(pt, !isWhite())) continue;

                if (context.isFree(pt) || 
                    context.pieceAt(pt).isWhite() != isWhite())
                {
                    validPositions.add(new Move(this, pt));
                }
            }
        }
        if (!hasMoved() && !inCheck(context)) {
            for (int i = -1; i <= 1; i += 2) {
                boolean possible = true;
                for (int d = 1; d <= 2; d++) {
                    pt = p.transpose(d*i,0);
                    if ( 
                    !(
                        ( 
                            context.isFree(pt) ||
                            context.pieceAt(pt).isWhite() != isWhite() 
                        ) &&
                        !context.isAttacked(pt, !isWhite()) 
                    )
                    )
                    {
                        possible = false;
                        break;
                    }
                }
                if (possible)
                    validPositions.add(new Move(this, p.transpose(2*i,0)));
            }
        }

        return validPositions; 
    }

    public boolean inCheck(Board context) {
        return context.isAttacked(getPosition(), !isWhite());
    }

    public final String getName() {
        return "König";
    }

    public final String toSAN() {
        return "K";
    }

    public final King clone() {
        return new King(getPosition().clone(), isWhite(), hasMoved());
    }
}
