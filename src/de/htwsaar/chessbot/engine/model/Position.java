package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author
*/
public final class Position {
    
    public static final int MAX_ROWS = 50;
    public static final int MAX_COLS = 26;

    private int row;
    private int column;

    /**
    * Erzeugt eine neue Position mit den übergebenen Koordinaten.
    *
    * 
    */ 
    public Position(int column, int row) {
        this.column = column;
        this.row    = row;
    }

    public Position(String sanString) {
        String san = sanString.trim();
        //TODO: Regex für Notation prüfen
        if (san.length() < 2)
            throw SAN_STRING_TOO_SHORT;

        char col   = san.charAt(0);
        String row = san.substring(1);
        this.column = (col - 'a' + 1);
        this.row = Integer.valueOf(row);
    }

    /**
    * Gibt die x-Koordinate dieser Position zurück.
    */
    public int getRow() {
        return this.row;
    }
    
    /**
    * Legt die x-Koordinate dieser Position fest.
    */
    public Position setRow(int row) {
        return new Position(this.column, row);
    }

    /**
    * Gibt die y-Koordinate dieser Position zurück.
    */
    public int getColumn() {
        return this.column;
    }

    /**
    * Legt die y-Koordinate dieser Position fest.
    */
    public Position setColumn(int column) {
        return new Position(column, this.row);
    }

    /**
    * Ändert die Position.
    */
    public Position set(int column, int row) {
        return new Position(column, row);
    }

    /**
    * Verschiebt die Position um die übergebenen Deltas.
    */
    public Position translate(int deltaX, int deltaY) {
        int trow = this.row + deltaX, 
            tcol = this.column + deltaY;
    
        return new Position(tcol, trow);
    }

    public int hashCode() {
        return this.row * 231 + this.column * 127;
    }

    public boolean equals(Object other) {
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

    public boolean existsOn(Board context) {
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

    public boolean isValid() {
        if ( getRow() < 1 || getColumn() < 1)
            return false;
        if ( getRow() > MAX_ROWS || getColumn() > MAX_COLS )
            return false;

        return true;
    }

    public Position clone() {
        return new Position(this.column, this.row);
    }

    public String toSAN() {
        if ( !isValid() )
            return "INVALID";
        StringBuilder sb = new StringBuilder();
        sb.append( getLetter(this.column)).append(this.row);
        return sb.toString();
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getColumn()).append("|").append(getRow()).append(")");
        return sb.toString();
    }
    
    private static char getLetter(int column) {
        return (char) ( 'a' + (char) (column-1)); 
    }

    private static final IllegalArgumentException SAN_STRING_TOO_SHORT =
        new IllegalArgumentException("Die algebraische Notation ist zu kurz!");

}
