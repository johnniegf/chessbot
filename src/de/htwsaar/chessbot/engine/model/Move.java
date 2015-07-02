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
        if (context.getPiece(piece) == null)
            return false;

        return piece.canMoveTo(targetPosition, context);
    }

    public Board execute(Board onBoard) {
        if ( !isPossible(onBoard) )
            return null;

        Board target = onBoard.clone();

        target.removePiece(piece);
        if (!target.isFree(targetPosition))
            target.removePiece(target.pieceAt(targetPosition));

        Piece movedPiece = piece.clone();
        movedPiece.setPosition(targetPosition);
        target.addPiece(movedPiece);
        return target;
    }

    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other == this) return true;

        try {
            Move om = (Move) other;
            return om.getPiece().equals(getPiece())
                && om.getTarget().equals(getTarget());
        } catch (ClassCastException cce) {
            return false;
        }
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
            sb.append(piece.toSAN())
              .append(piece.getPosition().toSAN())
              .append(targetPosition.toSAN());
        }
        return sb.toString();
    }
}
