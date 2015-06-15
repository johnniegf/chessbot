package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author
*/
public class Move {

    private Piece    piece;
    private Position targetPosition;

    /**
    * Erzeuge einen Nullzug.
    */ 
    public Move() {
        this(null, Position.INVALID);
    }

    public Move(final Piece piece, final Position targetPosition) {
        this.piece = piece;
        this.targetPosition = targetPosition;
    }

    public Move(final Board context, 
                final Position fromPosition, 
                final Position toPosition) 
    {
        this.piece = context.pieceAt(fromPosition);
        this.targetPosition = toPosition;
    }

    public Position getTarget() {
        return targetPosition;
    }

    public void setTarget(final Position targetPosition) {
        this.targetPosition = targetPosition;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(final Piece piece) {
        this.piece = piece;
    }

    public boolean isNull() {
        return piece == null
            || targetPosition == null
            || !targetPosition.isValid();
    }

    public boolean isPossible(Board context) {
        if (isNull())
            return false;

        return piece.canMoveTo(targetPosition, context);
    }

    public String toString() {
        return String.format(
            "%s %s",
            (piece == null ? "00" : piece.toString()),
            (targetPosition == null ? "00" : targetPosition.toString())
        );        
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toSAN() {
        StringBuilder sb = new StringBuilder();
        if (isNull()) {
            sb.append("0000");
        } else {
            sb.append(piece.getShortName())
              .append(piece.getPosition())
              .append(targetPosition);
        }
        return sb.toString();
    }
}
