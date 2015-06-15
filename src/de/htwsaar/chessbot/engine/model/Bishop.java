package de.htwsaar.chessbot.engine.model;

import java.util.*;

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
public final class Bishop extends Piece {
    
    public Bishop() {
        super();
    }

    public Bishop(final Position position) {
        super(position);
    }

    public Bishop(final Position position, 
                  final boolean isWhite) 
    {
        super(position, isWhite);
    }

    public Bishop(final Position position,
                  final boolean isWhite, 
                  final boolean hasMoved) 
    {
        super(position, isWhite);
    }

    public final Collection<Position> getValidMoves(final Board context) {
        Collection<Position> result =  new ArrayList<Position>();
        Position p; 
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                int c = 1;
                p = getPosition().transpose(c*i, c*j);
                while(p.existsOn(context)) 
                {
                    if (context.isFree(p))
                       result.add(p);
                    else if (context.pieceAt(p).isWhite() != isWhite()) {
                        result.add(p);
                        break;
                    } else {
                        break;
                    }
                    c += 1;
                    p = getPosition().transpose(c*i, c*j);
               } 
            }
        }
        return result;
    }

    public final String getName() {
        return "Läufer";
    }

    public final String getShortName() {
        return "B";
    }

    public boolean equals(final Object other) {
        if (other == null) 
            return false;
        if (other == this)
            return true;

        try {
            Bishop b = (Bishop) other;
            return b.getPosition().equals(getPosition())
                && b.isWhite() == isWhite();
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public final Bishop clone() {
        return new Bishop(getPosition().clone(), isWhite());
    }
}
