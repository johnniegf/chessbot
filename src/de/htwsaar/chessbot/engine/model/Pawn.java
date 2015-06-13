package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Bauer.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Pawn extends Piece {

    public Pawn() {
        super();
    }

    public Pawn(Position position) {
        super(position);
    }

    public Pawn(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Pawn(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite, hasMoved);
    }

    public String getName() {
        return "Bauer";
    }

    public String getShortName() {
        return "";
    }

    public Collection<Position> getValidMoves(Board context) {
        List<Position> possibleMoves = new ArrayList<Position>(4);
        int increment = isWhite() ? 1 : -1;
        Position p = getPosition();
        Position pn;
        for (int i = -1; i <= 1; ++i) {
            pn = p.translate(increment,i);
            if (pn.existsOn(context))
                possibleMoves.add(pn);
        }
        if ( !hasMoved() )
            possibleMoves.add( p.translate(2*increment, 0) );

        return possibleMoves;
    }

    public boolean equals(Object other) {
        return super.equals(other);
    }

    public Pawn clone() {
        return new Pawn(getPosition().clone(), isWhite(), hasMoved());
    }
}
