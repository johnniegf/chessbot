package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Beschreibung.
*
* @author
*/
public class Pawn extends Piece {
    
    /**
    * Standardkonstruktor.
    */ 
    public Pawn(Position position) {
        this(position, true, true);
    }

    public Pawn(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite, hasMoved);

        if (isWhite && position.getRow() < 2)
            throw new IllegalArgumentException();
        if (!isWhite && position.getRow() > 7)
            throw new IllegalArgumentException();

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
        for (int i = -1; i <= 1; ++i) 
            try { 
                possibleMoves.add( p.translate(increment,i) );
            } catch (RuntimeException e) {}

        if ( !hasMoved() )
            try {
                possibleMoves.add( p.translate(2*increment, 0) );
            } catch (RuntimeException e) {}

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

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
}
