package de.htwsaar.chessbot.engine.model;

/**
* Interne Darstellung einer Schachfigur.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public abstract class Piece {
    
    private Position position;
    private boolean  isWhite;
    private boolean  hasMoved;

    protected Piece(Position position) {
        this(position, true, true);
    }

    protected Piece(Position position, boolean isWhite, boolean hasMoved) {
        if (position == null) {
            throw new NullPointerException();
        }
            
        this.position = position;
        this.isWhite  = isWhite;
        this.hasMoved = hasMoved;
    }

    public Position getPosition() {
        return this.position;
    }

    public boolean isWhite() {
        return this.isWhite;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public abstract String toString();

    public abstract Collection<Position> getValidMoves(Board context);
}
