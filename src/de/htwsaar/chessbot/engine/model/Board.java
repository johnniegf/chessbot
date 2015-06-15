package de.htwsaar.chessbot.engine.model;

import java.util.Collection;
import java.util.ArrayList;
import de.htwsaar.chessbot.engine.exception.*;

/**
*   Repraesentation des SpielBrettes
*
*   @author Timo Klein
*   @author Henning Walte
*/
public class Board
{
    
    private final int width;
    private final int height;
    private Piece[][] pieces;

    /**
    *   Standardkonstruktor.
    */ 
    public Board() {   
        this(8,8);
    }
    
    /**
    *   Konstruktor um Boardobjekt mit Feldgroesse zu erstellen 
    *
    *   @param width    Spielfeldbreite
    *   @param height   Spielfeldhoehe
    */
    public Board(int width, int height) {
        this.width  = width;
        this.height = height;
        this.pieces = new Piece[width][height];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pieces[x][y] = null;
            }
        }
    }
   
    /**
    *   Fuegt Spielfigur auf dem Spielbrett hinzu
    *
    *   @param piece    Spielfigur die auf dem Spielbrett 
    *                   hinzugefuegt werden soll
    */
    public boolean addPiece(Piece piece) {
        if ( piece == null)
            return false;

        Position p = piece.getPosition();
        if ( !p.existsOn(this) )
            return false;
        if ( !isFree(p) )
            return false;
        
        int x = p.getColumn() -1;
        int y = p.getRow() -1;
    
        pieces[x][y] = piece;
        return true;
    }
    
    /**
    *   Ist das Feld bedroht
    *   
    *   @param p        Feldposition die Ueberprueft werden soll
    *   @param byWhite  
    *   @return true falls Feld bedroht, false wenn nicht
    */
    public boolean isAttacked(Position p, boolean byWhite) {
        for (Piece piece : getPieces()) {
            if (piece.isWhite() == byWhite && piece.attacks(p, this)) {
                return true;
            }
        }
        return false;
    }
    
    /**
    *   Ist das Feld an der Position frei
    *
    *   @param p    Position die geprueft werden soll
    *   @return true wenn Feld frei ist, false wenn Feld besetzt ist
    */
    public boolean isFree(Position p) {
        if (p == null)
            throw new NullPointerException("Position ist null"); 

        return pieceAt(p) == null;
    }
    
    /**
    *   Breite vom Spielbrett
    *
    *   @return Gibt die Breite des Spielbrettes zurueck
    */
    public int getWidth() {
        return width;
    }
    
    /**
    *   Hoehe vom Spielbrett
    *
    *   @return Gibt die Hoehe vom Spielbrett zurueck
    */
    public int getHeight() {
        return height;
    }
    
    /**
    *   Spielfigur an einer bestimmten Position
    *
    *   @param at   
    *   @return Spielfigur an der mitgegeben Position
    */
    public Piece pieceAt(Position at) {
        if (at == null || !at.existsOn(this)) 
            return null;

        int x = at.getColumn() -1;
        int y = at.getRow() -1;
        
        return pieces[x][y];
    }
    
    /**
    *   Eine Sammlung von Spielfiguren die sich derzeit auf dem Spielbrett befinden
    *   
    *   @return Sammlung von Spielfiguren die auf dem Spielbrett sind
    */
    public Collection<Piece> getPieces() {
        Collection<Piece> result = new ArrayList<Piece>();
        for (Piece[] col : pieces) {
            for (Piece p : col)
                if (p != null)
                    result.add(p);
        }
        return result;
    }
    
    /**
    *   Gibt die Anzahl der Figuren die sich derzeit auf dem Spielbrett
    *   befinden zurueck
    *
    *   @return Anzahl der Spielfiguren
    */
    public int getPieceCount() {
        return getPieces().size();
    }
    
    /**
    *   Gibt eine Spielfigur die sich auf dem Spielbrett befindet zurueck
    *   
    *   @param seek zu suchende Spielfigur
    *   @return Spielfigur wenn sie sich auf dem Spielbrett befindet, 
    *           ansonsten null
    */
    public Piece getPiece(Piece seek) {
        for (Piece p : getPieces()) {
            if (p.equals(seek))
                return p;
        }
        return null;
    }
    
    /**
    *   Vergleich zweier Objekte
    *
    *   @param other    zu ueberpruefende Objekt
    *   @return true falls Objekt gleich, ansonsten false
    */
    
    public boolean equals(final Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;

        try {
            Board b = (Board) other;
            Position p;
            Piece op;
            if (getPieces().size() != b.getPieces().size())
                return false;

            for (Piece mp : getPieces()) {
                p = mp.getPosition();
                op = b.pieceAt(p);
                if (!mp.equals(op))
                    return false;
            }
            return true;
        } catch (ClassCastException cce) {
            return false;
        }
    }
    
    /**
    *   Prüft ob Spielbrett leer ist
    *
    *   @return true falls 
    */
    public boolean isEmpty() {
        return getPieceCount() == 0;
    }
    
    /**
    *   Gibt eine Kopie vom Spielbrett zurueck
    *   
    *   @return tiefe Kopie des Board-Objektes
    */
    public Board clone() {
        Board cloned = new Board(this.getWidth(),this.getHeight());
        for (Piece p : getPieces())
            cloned.addPiece(p.clone());
        return cloned;
    }
    
    /**
    *   Loescht eine Spielfigur vom Spielbrett 
    *
    *   @param piece    Spielfigur die geloescht werden soll
    *   @return true wenn Figur geloescht wurde, ansonsten false
    */
    public boolean removePiece(Piece piece) {
        if (getPiece(piece) == null)
            return false;

        int x = piece.getPosition().getColumn() -1;
        int y = piece.getPosition().getRow() -1;
    
        pieces[x][y] = null;

        return true;
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(width).append("x").append(height).append("] ");
        for (Piece p : getPieces())
            sb.append(p).append(", ");
        return sb.toString();
    }
    
    /**
    *   Hilfmethode zur Ueberpruefung von Parameter
    *
    *   @param condition    Algebrahischer Ausdruck
    *   @param exn_message  Exceptionmeldung die ausgegeben werden soll
    */
    private void checkParam(boolean condition, String exn_message) {
        if (!condition)
            throw new BoardException(exn_message);
    }
}
