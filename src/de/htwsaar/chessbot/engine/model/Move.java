package de.htwsaar.chessbot.engine.model;

/**
* Interne Repräsentation eines Spielzugs.
*
* Oberklasse aller Züge für jedes Regelwerk. Spezifika der Zugarten (für 
* FIDE-Regeln z.B. en passant, Rochade, Bauernumwandlung) können in 
* Unterklassen behandelt werden.
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
    * @param toPosition     Zielfeld des Zugs
    */
    public Move(final Board context, 
                final Position fromPosition, 
                final Position toPosition) 
    {
        this.piece = context.pieceAt(fromPosition);
        this.targetPosition = toPosition;
    }

    public Move(final Board context,
                final String sanMove)
    {
        if (!sanMove.matches(REGEX_SAN_MOVE))
            throw new SanStringParseException();

        String[] fields = sanMove.replaceAll(POS_REGEX, " $1 ")
                                 .replaceAll("  ", " ")
                                 .split(" ");
        for (String f : fields) {
            System.out.println("In Move(Board,String): " + f);
        }
        this.piece = extractPiece(context, fields[0], fields[1]);
        this.targetPosition = extractTargetPosition(context, fields[2]);
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
        if (context == null)
            throw new NullPointerException();
        if (context.getPiece(piece) == null)
            return false;
        if (context.isWhiteMoving() != piece.isWhite())
            return false;
        if (!piece.canMoveTo(targetPosition, context))
            return false;

        Board b = tryExecute(context);
        return b.isValid();
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

        return tryExecute(onBoard);
    }

    /**
    * Versuche, diesen Zug auszuführen.
    */
    protected Board tryExecute(final Board onBoard) {
        Board target = onBoard.clone();
        Piece movedPiece = piece.move(targetPosition, target);
        if (!target.isFree(targetPosition))
            target.removePieceAt(targetPosition);
        
        target.addPiece(movedPiece);
        target.removePiece(piece);
        target.togglePlayer();
        target.setEnPassant(Position.INVALID);
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
    public String toSAN(Board context) {
        StringBuilder sb = new StringBuilder();
        if (isNull()) {
            sb.append("0000");
        } else {
            String hits = (context.isFree(targetPosition) ? "" : "X" );
            sb.append(piece.toSAN())
              .append(piece.getPosition().toSAN())
              .append(hits)
              .append(targetPosition.toSAN());
        }
        return sb.toString();
    }

    /**
    * Gib diesen Zug in UCI-Notation zurück.
    *
    * Lange algebraische Notation ohne Figurkürzel. Beispiele:
    * <ul>
    *   <li>e2e4 - Bauernzug</li>
    *   <li>e1g1 - Rochade, weiß auf Königsseite</li>
    *   <li>a2a1Q - Bauernumwandlung auf a1 zu schwarzer Dame </li>
    * </ul>
    *
    * @return UCI-Notation
    */
    public String toUCI() {
        StringBuilder sb = new StringBuilder();
        sb.append(piece.getPosition().toSAN());
        sb.append(targetPosition.toSAN());
        return sb.toString();
    }

    private static Piece extractPiece(final Board context, 
                                      final String pieceSAN,
                                      final String posSAN) 
    {
        Position p = Position.P(posSAN);
        Piece pc = context.pieceAt(p);
        if (pc == null)
            throw new InvalidMoveException();
        if (pc.toSAN() != pieceSAN);

        return pc;
    }

    private static Position extractTargetPosition(final Board context,
                                                  final String posSAN)
    {
        return Position.P(posSAN);

    }



    private static final String POS_REGEX = 
        "([a-z][1-9][0-9]?)";
    private static final String REGEX_SAN_MOVE = 
        "[A-Z]?[a-z][1-9][0-9]?X?[a-z][1-9][0-9]?[A-Z]?";
    private static final String REGEX_UCI_MOVE = 
        "[a-z][1-9][0-9]?[a-z][1-9][0-9]?[A-Z]?";
}
