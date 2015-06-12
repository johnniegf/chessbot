package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Bauer.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Pawn extends Piece {
    
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
/*
        //TODO:
        // Hat der Bauer noch nicht gezogen, kann er 2 Felder vorrücken!
        int increment = isWhite ? 1 : -1;
        List<Position> validTargets = new ArrayList(3);
        Position topPos      = this.getPosition().translate(0, increment),
                 topLeftPos  = this.getPosition().translate(0, increment),
                 topRightPos = this.getPosition().translate(0, increment);
        Piece    topLeft, top, topRight;
    
        // Kann der Bauer ziehen?
        top = context.getPiece( topPos );
        if (top == null) validTargets.add(topPos);
        
        // Kann der Bauer schlagen?
        topLeft = context.getPiece( topLeftPos );
        if (topLeft.isWhite() != isWhite()) validTargets.add( topLeftPos )
        
        topRight = context.getPiece( topRightPos );
        if (topRight.isWhite() != isWhite()) validTargets.add( topRightPos );
    
        return validTargets;
*/
    }
    public Pawn clone() {
        return new Pawn(getPosition().clone(), isWhite(), hasMoved());
    }
}
