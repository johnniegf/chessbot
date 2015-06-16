package de.htwsaar.chessbot.engine.model;

/**
* Spielzug.
*
* @author Johannes Haupt
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

    /**
    * Erzeuge einen neuen Spielzug, in dem die übergebene Figur auf das
    * übergebene Feld zieht.
    *
    * @param piece          zu bewegende Figur
    * @param targetPosition Zielfeld des Zugs
    */
    public Move(final Piece piece, final Position targetPosition) {
        this.piece = piece;
        this.targetPosition = targetPosition;
    }

    /**
    * Erzeuge einen neuen Spielzug, in dem die übergebene Figur auf das
    * übergebene Feld zieht.
    *
    * @param context        Stellung, aus der der Zug generiert wird
    * @param fromPosition   Startfeld des Zugs
    * @param targetPosition Zielfeld des Zugs
    */
    public Move(final Board context, 
                final Position fromPosition, 
                final Position toPosition) 
    {
        this.piece = context.pieceAt(fromPosition);
        this.targetPosition = toPosition;
    }

    /**
    * Gib das Zielfeld zurück.
    *
    * @return das Zielfeld des Zugs
    */
    public Position getTarget() {
        return targetPosition;
    }

    /**
    * Lege das Zielfeld fest.
    * 
    * @param targetPosition das neue Zielfeld des Zugs
    */
    public void setTarget(final Position targetPosition) {
        this.targetPosition = targetPosition;
    }

    /**
    * Gib die Figur zurück, die in diesem Zug bewegt wird.
    *
    * @return die Figur dieses Zugs
    */
    public Piece getPiece() {
        return piece;
    }

    /**
    * Lege die Figur fest, die in diesem Zug bewegt wird.
    *
    * @param piece die neue Figur dieses Zugs
    */
    public void setPiece(final Piece piece) {
        this.piece = piece;
    }

    /**
    * Gib zurück, ob dieser Zug ein Nullzug ist.
    *
    * @return <code>true</code> wenn der Zug ein Nullzug ist, sonst
    *         <code>false</code>
    */
    public boolean isNull() {
        return piece == null
            || targetPosition == null
            || !targetPosition.isValid();
    }

    /**
    * Gib zurück, ob dieser Zug in der übergebenen Stellung möglich ist.
    *
    * @param context Stellung in der der Zug ausgeführt wird
    * @return <code>true</code>, wenn der Zug möglich ist, 
    *         sonst <code>false</code>
    */
    public boolean isPossible(Board context) {
        if (isNull())
            return false;
        if (context.getPiece(piece) == null)
            return false;

        return piece.canMoveTo(targetPosition, context);
    }

    /**
    * Führt diesen Zug in der übergebenen Stellung aus, falls er möglich ist.
    * 
    * @param onBoard Stellung in der der Zug ausgeführt wird
    * @return die veränderte Stellung, falls der Zug möglich ist und 
    *         korrekt ausgeführt wurde, sonst <code>null</code>
    */
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

    /**
    * Prüfe diesen Zug auf Gleichheit mit einem anderen Objekt.
    *
    * @param other
    * @return <code>true</code>, wenn das übergebene Objekt ein Zug 
    *         ist, dessen Figur und Zielfeld mit denen diesen Zugs 
    *         übereinstimmen, sonst <code>false</code>
    */
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

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        return String.format(
            "%s %s",
            (piece == null ? "00" : piece.toString()),
            (targetPosition == null ? "00" : targetPosition.toString())
        );        
    }

    /**
    * Gib die algebraische Notation dieses Zugs zurück.
    *
    * @return algebraische Notation des Zugs
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
