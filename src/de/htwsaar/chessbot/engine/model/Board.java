package de.htwsaar.chessbot.engine.model;

import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import de.htwsaar.chessbot.engine.exception.*;
import static de.htwsaar.chessbot.engine.model.Position.*;

/**
* Interne Darstellung eines Schachbretts
*
* @author Johannes Haupt
* @author Timo Klein
* @author Henning Walte
*/
public class Board
{
    private final int width;
    private final int height;
    private boolean   whiteMoving;
    private Position  enPassant;
    private Piece[][] pieces;

    /**
    * Standardkonstruktor.
    *
    * Erzeuge ein leeres Schachbrett der Breite und Höhe 8, wobei weiß
    * am Zug ist.
    */ 
    public Board() {   
        this(8, 8);
    }
    

    public Board(final int width, final int height) {
        this(width, height, true);
    }
    
    /**
    * Erzeuge ein leeres Schachbrett mit den übergebenen Dimensionen 
    *
    * @param width    Breite des Schachbretts
    * @param height   Höhe des Schachbretts
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
    * Stelle eine Spielfigur auf das Spielbrett.
    *
    * @param piece    Schachfigur, die auf das Brett gestellt werden soll.
    * @return true, genau dann wenn die Figur auf das Schachbrett gestellt
    *         wurde.
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
    * Gib aus, ob das übergebene Feld von der übergebenen Farbe
    * bedroht wird.
    *   
    * @param p        Feldposition die überprueft werden soll
    * @param byWhite  
    * @return true genau dann wenn das Feld von der übergebenen Farbe
    *         bedroht wird, sonst false
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
    * Gib die erste Figur zurück, die das übergebene FEN-Kürzel hat.
    *
    * @param fenName    FEN-Kürzel der zu suchenden Figur.
    * @return die Figur mit den übergebenen FEN-Kürzel oder <code>null</code>
    */
    public Piece getPieceByName(final String fenName) {
        return getPieceByName(fenName, 0);
    }

    /**
    * Gib eine Figur zurück, die das übergebene FEN-Kürzel hat.
    *
    * @param fenName    FEN-Kürzel der zu suchenden Figur.
    * @param offset     Index der Figur, die zurückgegeben werden soll.
    * @return die Figur mit den übergebenen FEN-Kürzel oder <code>null</code>
    */
    public Piece getPieceByName(final String fenName, final int offset) {
        if (fenName == null)
            throw new NullPointerException();

        int i = 0;
        for (Piece p : getPieces()) {
            if (fenName.equals(p.toFEN()))
                if (i == offset)
                    return p;
                else
                    i++;
        }
        return null;
    }
  
    /**
    * Gib alle Figuren zurück, die vom übergebenen Typ sind.
    *
    * @param pieceType  prototyp der zu suchenden Figuren
    * @return eine Liste von Figuren, die derselben Klasse angehören
    *         wie <code>pieceType</code>
    */
    public Collection<Piece> getPiecesByType(Piece pieceType) {
        Collection<Piece> result = new LinkedList<Piece>();
        for (Piece p : getPieces()) {
            if (pieceType.getClass().isInstance(p))
                result.add(p);
        }
        return result;
    }

    /**
    * Gib aus, an welcher Position en passant möglich ist.
    *
    * @return en passant-Zielfeld
    */
    public Position enPassant() {
        return enPassant;
    }

    /**
    * Lege fest, an welcher Position en passant möglich ist.
    *
    * @param position   en-passant-Zielfeld
    */
    public void setEnPassant(final Position position) {
        if (position == null || !position.existsOn(this))
            enPassant = null;
        else
            enPassant = position;
    }

    /**
    * Gib aus, ob weiß am Zug ist.
    *
    * @return <code>true</code> wenn weiß am Zug ist, sonst <code>false</code>
    */
    public boolean isWhiteMoving() {
        return whiteMoving;
    }

    /**
    * Wechsele den Spieler, der am Zug ist.
    */
    public void togglePlayer() {
        whiteMoving = !whiteMoving;
    }

    /**
    * Lege fest, wer am Zug ist.
    *
    * @param whiteMoving    <code>true</code> für den weißen Spieler, <code>
    *                       false</code> für schwarz.
    */
    public boolean setWhiteMoving(boolean whiteMoving) {
        if (this.whiteMoving == whiteMoving)
            return false;

        this.whiteMoving = whiteMoving;
        return true;
    }

    /**
    * Gib aus, ob das übergebene Feld besetzt ist.
    *
    * @param p    Position die geprueft werden soll
    * @return true wenn das Feld frei ist, sonst false
    */
    public boolean isFree(Position p) {
        if (p == null)
            throw new NullPointerException("Position ist null"); 

        return pieceAt(p) == null;
    }
    
    /**
    * Breite des Schachbretts
    *
    * @return die Breite des Spielbrettes
    */
    public int getWidth() {
        return width;
    }
    
    /**
    * Höhe des Schachbretts
    *
    * @return die Hoehe des Spielbrettes
    */
    public int getHeight() {
        return height;
    }
    
    /**
    * Gib die Spielfigur an der übergebenen Position zurück
    *
    * @param at Position der auszugebenden Figur
    * @return Spielfigur an der übergebenen Position
    */
    public Piece pieceAt(Position at) {
        if (at == null || !at.existsOn(this)) 
            return null;

        int x = at.getColumn() -1;
        int y = at.getRow() -1;
        
        return pieces[x][y];
    }
    
    /**
    * Gib aus, welche Figuren zur Zeit auf dem Spielfeld stehen.
    *   
    * @return eine <code>Collection</code> aller Figuren, die auf 
    *         dem Spielbrett stehen
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
    * Gib die Anzahl der Figuren, die sich derzeit auf dem Spielbrett
    * befinden zurück.
    *
    * @return Anzahl der Spielfiguren auf dem Brett
    */
    public int getPieceCount() {
        return getPieces().size();
    }
    
    /**
    * Gib eine Spielfigur, die sich auf dem Spielbrett befindet zurück.
    *   
    * @param seek zu suchende Spielfigur
    * @return die Spielfigur wenn sie sich auf dem Spielbrett befindet, 
    *         ansonsten <code>null</code>
    */
    public Piece getPiece(Piece seek) {
        for (Piece p : getPieces()) {
            if (p.equals(seek))
                return p;
        }
        return null;
    }
    
    /**
    * Vergleiche diese Stellung mit einem anderen Objekt
    *
    * @param other    zu ueberpruefende Objekt
    * @return true falls <code>other</code> ein Objekt der Klasse <code>
    *         Board</code> ist, und sich die Stellungen gleichen ansonsten
    *         <code>false</code>
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
    * Gib aus ob das Spielbrett leer ist.
    *
    * @return true genau dann, wenn sich keine Figuren auf dem Brett befinden.
    */
    public boolean isEmpty() {
        return getPieceCount() == 0;
    }
    
    /**
    * Gib eine Kopie dieser Stellung zurück.
    *   
    * @return eine Kopie dieser Stellung
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
    * Nimm eine Figur vom Spielbrett 
    *
    * @param piece    Spielfigur die entfernt werden soll
    * @return true wenn die Figur entfernt wurde, ansonsten false
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

    /**
    * Nimm die Figur an der übergebenen Position vom Brett.
    *
    * @param pos    Position der zu entfernenden Figur.
    * @return <code>true</code> genau dann, wenn eine Figur vom Brett entfernt
    *         wurde, d.h. dieses Objekt durch den Aufruf dieser Methode
    *         verändert wurde.
    */
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

    /**
    * Gib den Zug aus der Zugliste zurück, der die übergebene Notation hat.
    *
    * @param sanMove    Stringform des Zugs
    * @return den Zug zur übergebenen Stringrepräsentation oder <code>
    *         null</code>, falls der übergebene Zug nicht möglich ist.
    */
    public Move getMove(String sanMove) {
        if (sanMove == null)
            return null;

        for (Move m : getMoveList())
            if (sanMove.equals(m.toUCI()))
                return m;

        return null;
    }

    /**
    * Erzeuge die Zugliste für diese Stellung.
    *
    * @return die Zugliste für die aktuelle Stellung
    */
    public Collection<Move> getMoveList() {
        Collection<Move> moveList = new ArrayList<Move>();
        Move currentMove;
        for (Piece p : getPieces()) {
            for (Move m : p.getMoveList(this))
                if (m.isPossible(this)) 
                    moveList.add(m);
        }
        return moveList;
    } 
    
    /**
    * Gib zurück, ob diese Stellung regelkonform ist.
    *
    * @return <code>true</code> genau dann, wenn diese Stellung den Regeln
    *         entspricht.
    */
    public boolean isValid() {
        boolean whiteMoving = isWhiteMoving();
        Piece king = getPieceByName(whiteMoving ? "k" : "K");
        Position kingSquare = king.getPosition();
        return !isAttacked(kingSquare, whiteMoving);
    }
    
    /**
    * Hilfmethode zur Ueberpruefung von Parameter
    *
    * @param condition    Algebrahischer Ausdruck
    * @param exn_message  Exceptionmeldung die ausgegeben werden soll
    */
    private void checkParam(boolean condition, String exn_message) {
        if (!condition)
            throw new BoardException(exn_message);
    }

    public static interface Formatter {
        String format(final Board board);
    }

    private static final String EXN_WIDTH_TOO_LOW =
        "Ungültige Breite.";
    private static final String EXN_HEIGHT_TOO_LOW =
        "Ungültige Höhe.";
}
