package de.htwsaar.chessbot.engine.model;

import java.util.*;

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

    protected Piece(Position position, boolean isWhite) {
        this(position, isWhite, false);
    }

    protected Piece(Position position, boolean isWhite, boolean hasMoved) {
        this.setPosition(position);
        this.isWhite  = isWhite;
        this.hasMoved = hasMoved;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position newPosition) {
        if ( newPosition == null ) {
            throw new NullPointerException();
        } 
        
        this.position = newPosition;
    }

    public boolean isWhite() {
        return this.isWhite;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public abstract String getName();

    public abstract String getShortName();

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" ")
          .append( isWhite ? "w" : "b" ).append(" ")
          .append(getPosition());
        return sb.toString();
    }

    public abstract Collection<Position> getValidMoves(Board context);
}
