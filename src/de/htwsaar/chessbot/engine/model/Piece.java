package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.exception.*;
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


    /**
    * Erzeuge eine neue weiße Schachfigur an der übergebenen Position.
    *
    * @param position Die Position der neuen Figur
    * @throws NullPointerException wenn <code>position == null</code>
    */
    protected Piece(Position position) {
        this(position, true, true);
    }

    /**
    * Erzeuge eine neue Schachfigur an der übergebenen Position.
    *
    * @param position Die Position der neuen Figur
    * @param isWhite  Die Farbe der neuen Figur (<code>true</code> für weiß)
    * @throws NullPointerException wenn <code>position == null</code>
    */
    protected Piece(Position position, boolean isWhite) {
        this(position, isWhite, false);
    }

    /**
    * Erzeuge eine neue Schachfigur an der übergebenen Position.
    *
    * @param position Die Position der neuen Figur
    * @param isWhite  Die Farbe der neuen Figur (<code>true</code> für weiß)
    * @param hasMoved Ob die Figur bereits bewegt wurde.
    * @throws NullPointerException wenn <code>position == null</code>
    */
    protected Piece(Position position, boolean isWhite, boolean hasMoved) {
        this.setPosition(position);
        this.isWhite  = isWhite;
        this.hasMoved = hasMoved;
    }

    /**
    * Gib die aktuelle Position der Figur aus.
    *
    * @return die aktuelle Position der Figur
    */
    public Position getPosition() {
        return this.position;
    }

    /**
    * Lege die Position der Figur fest.
    *
    * @param newPosition neue Position der Figur
    */
    public void setPosition(Position newPosition) {
        if ( newPosition == null ) {
            throw new NullPointerException();
        } 
        
        this.position = newPosition;
    }

    /**
    * Gib die Farbe der Figur aus.
    *
    * @return <code>true</code> für weiß, <code>false</code> für schwarz
    */ 
    public boolean isWhite() {
        return this.isWhite;
    }

    /**
    * Gib aus, ob die Figur bewegt wurde.
    *
    * @return <code>true</code> wenn die Figur bewegt wurde,
    *         sonst <code>false</code>
    */
    public boolean hasMoved() {
        return this.hasMoved;
    }
    
    /**
    * Lege fest, ob die Figur bewegt wurde.
    *
    * @param hasMoved ob die Figur bewegt wurde.
    */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
    * Bewege die Figur an die übergebene Position.
    *
    * @param  targetPosition Zielposition der Figur
    * @param  context        Aktuelle Stellung des Schachbretts
    * @return eine Kopie dieser Figur an der übergebenen Position
    * @throws InvalidMove wenn die Figur nicht auf das übergebene 
    *                     Feld ziehen kann
    */
    public Piece move(Position targetPosition, Board context) {
        Piece target = this.clone();
        if ( !target.canMoveTo(targetPosition, context) )
            throw new InvalidMove();
        
        target.setPosition( targetPosition );
        target.setHasMoved( true );
        return target;
    }

    /**
    * Gib aus, ob ein Zug auf das übergebene Feld für eine 
    * Stellung möglich ist.
    *
    * @param  targetPosition Zielposition der Figur
    * @param  context        Aktuelle Stellung des Schachbretts
    * @return <code>true</code> wenn der Zug möglich ist, sonst
    *         <code>false</code>
    */
    public boolean canMoveTo(Position targetPosition, Board context) {
        Collection<Position> validMoves = getValidMoves(context);
        return validMoves != null 
            && validMoves.contains(targetPosition);
    }

    /**
    * Erzeuge eine Kopie dieser Figur.
    *
    * @return eine Kopie dieser Figur.
    */
    public abstract Piece clone();

    /**
    * Gib die Bezeichnung der Figur aus.
    *
    * @return die Bezeichnung der Figur
    */
    public abstract String getName();

    /**
    * Gib das Kürzel der Figur in algebraischer Notation aus.
    *
    * @return Figurkürzel in algebraischer Notation
    */
    public abstract String getShortName();

    /**
    * Gib eine Beschreibung der Figur aus.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" ")
          .append( isWhite ? "w" : "b" ).append(" ")
          .append(getPosition());
        return sb.toString();
    }

    /**
    * Gib eine Liste aller möglichen Züge zurück.
    *
    * TODO: Stellung prüfen. Zur Zeit wird ein leeres Brett angenommen.
    *
    * @return Liste aller möglichen Züge für die übergebene Stellung 
    */
    public abstract Collection<Position> getValidMoves(Board context);
}
