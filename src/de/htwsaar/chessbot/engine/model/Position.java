package de.htwsaar.chessbot.engine.model;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Collection;
import java.util.ArrayList;

/**
* Interne Darstellung einer Position auf dem Schachbrett.
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public final class Position {

    /**
    * Eine ungültige Position.
    */
    public static final Position INVALID = new Position(0,0);

    /**
    * Kurzschreibweise für Konstruktor.
    *
    * @param column x-Koordinate der neuen Position
    * @param row    y-Koordinaten der neuen Position
    * @return Position mit den übergebenen Koordinaten
    */
    public static final Position P(int column, int row) {
        return new Position(column, row);
    }
    
    /**
    * Kurzschreibweise für Konstruktor.
    *
    * @param san Postion in algebraischer Notation
    * @return Position mit der übergebenen algebraischen Notation
    */
    public static final Position P(String san) {
        return new Position(san);
    }

    /**
    * Erzeuge eine Liste von Positionen aus algebraischer Notation.
    *
    * @param positions kommagetrennte Liste von Positionen in 
    *                  algebraischer Notation
    * @return eine Liste mit allen übergebenen Positionen
    */
    public static final Collection<Position> PList(String... positions) {
        Collection<Position> result = new ArrayList<Position>();
        if (positions != null) {
            for (String p : positions)
                result.add(P(p));
        }
        return result;
    }

    private final int row;
    private final int column;

    /**
    * Erzeuge eine neue Position mit den übergebenen Koordinaten.
    *
    * @param column x-Koordinate der neuen Position
    * @param row    y-Koordinaten der neuen Position
    */ 
    public Position(final int column, final int row) {
        this.column = column;
        this.row    = row;
    }

    /**
    * Erzeuge eine neue Position mit der übergebenen algebraischen Notation.
    *
    * @param sanString Postion in algebraischer Notation
    */
    public Position(final String sanString) {
        String san = sanString.trim();
        if (san.length() < 2)
            throw SAN_STRING_TOO_SHORT;
        if (!REGEX_SAN.matcher(san).matches()) {
            throw SAN_FORMAT_ERROR;
        }

        char col   = san.charAt(0);
        String row = san.substring(1);
        this.column = (col - 'a' + 1);
        this.row = Integer.valueOf(row);
    }

    /**
    * Gib die x-Koordinate dieser Position zurück.
    *
    * @return Zeile dieser Position
    */
    public int getRow() {
        return this.row;
    }
    
    /**
    * Lege die x-Koordinate dieser Position fest.
    *
    * @param row neue Zeile dieser Position
    * @return neue Position mit geänderter Koordinate
    */
    public Position setRow(final int row) {
        return new Position(this.column, row);
    }

    /**
    * Gib die y-Koordinate dieser Position zurück.
    *
    * @return Spalte dieser Position
    */
    public int getColumn() {
        return this.column;
    }

    /**
    * Lege die y-Koordinate dieser Position fest.
    *
    * @param column neue Spalte dieser Position
    * @return neue Position mit geänderter Koordinate
    */
    public Position setColumn(final int column) {
        return new Position(column, this.row);
    }

    /**
    * Verschiebe die Position um die übergebenen Deltas.
    *
    * @param deltaRow Verschiebung in y-Richtung
    * @param deltaCol Verschiebung in x-Richtung
    * @return neue Position mit geänderten Koordinaten
    */
    public Position transpose(final int deltaCol, final int deltaRow) {
        int trow = this.row + deltaRow, 
            tcol = this.column + deltaCol;
    
        return new Position(tcol, trow);
    }

    /**
    * Verschiebe die Position um das übergeben Delta.
    *
    * @param delta Verschiebung in x- und y-Richtung
    * @return neue Position mit geänderten Koordinaten
    */
    public Position transpose(final Position delta) {
        return transpose(delta.getColumn(), delta.getRow());
    }

    /**
    * Erzeuge einen Hashwert dieser Position.
    */
    public int hashCode() {
        return this.row * 231 + this.column * 127;
    }

    /**
    * Prüfe die Position auf Gleichheit mit einem Objekt.
    *
    * @param other zu prüfendes Objekt
    * @return <code>true</code>, wenn das übergebene Objekt eine Position
    *         mit den selben Koordinaten wie diese ist, 
    *         sonst <code>false</code>
    */
    public boolean equals(final Object other) {
        if ( other == null )
            return false;
        try {
            Position otherPosition = (Position) other;
            if ( getRow()    != otherPosition.getRow()
              || getColumn() != otherPosition.getColumn() ) {
                return false;
            }

            return true;
        } catch (ClassCastException cce) {
            return false;
        }
    }

    /**
    * Prüfe, ob diese Position auf einem Schachbrett existiert.
    *
    * @param context das zu prüfende Schachbrett
    * @return <code>true</code>, wenn die Position auf dem übergebenen
    *         Schachbrett liegt, sonst <code>false</code>
    */
    public boolean existsOn(final Board context) {
        if ( !isValid() ) return false;

        if (context == null) {
            return getRow() <= 8 && getColumn() <= 8;
        } else {
/*         
            return getColumn() <= context.getWidth()
                && getRow() <= context.getHeight();
*/
            return false;
        }
    }

    /**
    * Prüfe, ob die Koordinaten dieser Stellung zulässig sind.
    *
    * Die Zählung der Koordinaten beginnt bei 1. Die (einschließlichen)
    * Maximalwerte sind in den Konstanten <code>MAX_ROWS</code>(50) und
    * <code>MAX_COLS</code>(26) festgehalten.
    *
    * @return <code>true</code>, wenn die Koordinaten innerhalb der
    *         zulässigen Grenzen liegen, sonst <code>false</code>
    */
    public boolean isValid() {
        if ( getRow() < 1 || getColumn() < 1)
            return false;
        if ( getRow() > MAX_ROWS || getColumn() > MAX_COLS )
            return false;

        return true;
    }

    /**
    * Kopiere diese Position.
    *
    * @return eine neue Position, die dieser Position gleicht
    */
    public Position clone() {
        return new Position(this.column, this.row);
    }

    /**
    * Gib die algebraische Notation dieser Position aus.
    *
    * @return Darstellung der Position in algebraischer Notation
    */
    public String toSAN() {
        if ( !isValid() )
            return "INVALID";
        StringBuilder sb = new StringBuilder();
        sb.append( getLetter(this.column)).append(this.row);
        return sb.toString();
    }

    /**
    * Stringkonversion, gib Details dieser Position aus.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(")
          .append( getColumn() )
          .append("|")
          .append( getRow() )
          .append(")");
        return sb.toString();
    }
    
    private static char getLetter(int column) {
        return (char) ( 'a' + (char) (column-1)); 
    }
    
    private static final int MAX_ROWS = 50;
    private static final int MAX_COLS = 26;
    private static final Pattern REGEX_SAN = 
        Pattern.compile("[a-z][1-9][0-9]?");

    private static final IllegalArgumentException SAN_STRING_TOO_SHORT =
        new IllegalArgumentException("Die algebraische Notation ist zu kurz!");
    private static final IllegalArgumentException SAN_FORMAT_ERROR =
        new IllegalArgumentException("Die algebraische Notation ist ungültig!");

}
