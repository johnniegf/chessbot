package de.htwsaar.chessbot.engine.model;

import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import de.htwsaar.chessbot.engine.exception.*;
import static de.htwsaar.chessbot.engine.model.Position.*;

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
    private boolean   whiteMoving;
    private Position  enPassant;
    private Piece[][] pieces;

    /**
    *   Standardkonstruktor.
    */ 
    public Board() {   
        this(8, 8);
    }
    

    public Board(final int width, final int height) {
        this(width, height, true);
    }
    
    /**
    *   Konstruktor um Boardobjekt mit Feldgroesse zu erstellen 
    *
    *   @param width    Spielfeldbreite
    *   @param height   Spielfeldhoehe
    */
    public Board(final int width, 
                 final int height, 
                 final boolean whiteMoving) 
    {
        checkParam(width > 0, EXN_WIDTH_TOO_LOW);
        checkParam(height > 0, EXN_HEIGHT_TOO_LOW);

        this.width  = width;
        this.height = height;
        this.enPassant = null;
        this.whiteMoving = whiteMoving;
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
  
    public Collection<Piece> getPiecesByType(Piece pieceType) {
        Collection<Piece> result = new LinkedList<Piece>();
        for (Piece p : getPieces()) {
            if (pieceType.getClass().isInstance(p))
                result.add(p);
        }
        return result;
    }

    public Position enPassant() {
        return enPassant;
    }

    public void setEnPassant(final Position position) {
        if (position == null || !position.existsOn(this))
            enPassant = null;
        else
            enPassant = position;
    }

    public boolean isWhiteMoving() {
        return whiteMoving;
    }

    public void togglePlayer() {
        whiteMoving = !whiteMoving;
    }

    public boolean setWhiteMoving(boolean whiteMoving) {
        if (this.whiteMoving == whiteMoving)
            return false;

        this.whiteMoving = whiteMoving;
        return true;
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
    tag*   befinden zurueck
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
            if (b.isWhiteMoving() != isWhiteMoving())
                return false;
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
        cloned.setWhiteMoving(isWhiteMoving());
        cloned.setEnPassant(enPassant());
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
        if (piece == null)
            return false;

        Position p = piece.getPosition();
        if (piece.equals(pieceAt(p)))
            return removePieceAt(p);
        else
            return false;
    }

    public boolean removePieceAt(Position pos) {
        if (pos == null || !pos.existsOn(this))
            return false;

        int x = pos.getColumn() - 1;
        int y = pos.getRow() - 1;
        pieces[x][y] = null;
        return true;
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        //return prettyPrint(); /*
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(width).append("x").append(height).append("] ");
        for (Piece p : getPieces())
            sb.append(p).append(", ");
        return sb.toString();
//*/
    }

    public Move getMove(String sanMove) {
        if (sanMove == null)
            return null;

        for (Move m : getMoveList())
            if (sanMove.equals(m.toSAN()))
                return m;

        return null;
    }

    public Collection<Move> getMoveList() {
        Collection<Move> moveList = new ArrayList<Move>();
        Move currentMove;
        for (Piece p : getPieces()) {
            moveList.addAll(p.getMoveList(this));
        }
        return moveList;
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

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        for (int i = height; i >= 1; i--) {
            for (int j = 1; j <= width; j++) {
                Position p = P(j,i);
                Piece at = pieceAt(p);
                String c = (at == null ? "_" : at.toFEN());
                sb.append( c );
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static interface Formatter {
        String format(final Board board);
    }

    private static final String EXN_WIDTH_TOO_LOW =
        "Ungültige Breite.";
    private static final String EXN_HEIGHT_TOO_LOW =
        "Ungültige Höhe.";
}
