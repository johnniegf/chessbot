package de.htwsaar.chessbot.engine.model;

import java.util.*;

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
public class King extends Piece {

    public King() {
        super();
    }

    public King(Position position) {
        super(position);
    }

    public King(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public King(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite, hasMoved);
    }

    public Collection<Position> getValidMoves(Board context) {
        Collection<Position> validPositions = new ArrayList<Position>(8);
        Position pt, p = getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if ( x == 0 && y == 0 )
                    continue;

                pt = p.translate(x,y);
                if (pt.existsOn(context))
                    validPositions.add(pt);
            }
        }
        if ( !hasMoved() ) {
            pt = p.translate(0,2);
            if (pt.existsOn(context))validPositions.add(pt);
            pt = p.translate(0,-2);
            if (pt.existsOn(context))validPositions.add(pt);
        }

        return validPositions; 
    }

    public String getName() {
        return "König";
    }

    public String getShortName() {
        return "K";
    }

    public King clone() {
        return new King(getPosition().clone(), isWhite(), hasMoved());
    }
}
