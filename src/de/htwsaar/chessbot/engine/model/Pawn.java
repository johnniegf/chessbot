package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author
*/
public class Pawn {
    
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

    public Collection<Position> getValidMoves(Board context) {
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
