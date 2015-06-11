package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author
*/
public class Position {
    
    private static int MAX_ROWS = 8;
    private static int MAX_COLS = 8;

    /**
    * Legt die Obergrenze für Zeilen- und Spaltenanzahl fest.
    *
    * @param width  maximale Zeilenanzahl
    * @param height maximale Spaltenanzahl
    */
    public static void setMaxDimensions(int width, int height) {
        if ( width < 1 || height < 1 )
            throw new IllegalArgumentException();

        MAX_ROWS = width;
        MAX_COLS = height;
    }

    private int row;
    private int column;

    /**
    * Erzeugt eine neue Position mit den übergebenen Koordinaten.
    *
    * 
    */ 
    public Position(int column, int row) {
        this.set(column, row);
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
    public void setRow(int row) {
        if ( row < 1 || row > MAX_ROWS )
            throw new IllegalArgumentException();

        this.row = row;
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
    public void setColumn(int column) {
        if ( column < 1 || column > MAX_COLS )
            throw new IllegalArgumentException();

        this.column = column;
    }

    /**
    * Ändert die Position.
    */
    public void set(int column, int row) {
        setRow(row);
        setColumn(column);
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

    public Position clone() {
        return new Position(this.column, this.row);
    }

    public String toSAN() {
        StringBuilder sb = new StringBuilder();
        sb.append( getLetter(this.column)).append(this.row);
        return sb.toString();
    }

    private static char getLetter(int column) {
        return (char) ( 'a' + (char) (column-1)); 
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getRow()).append("|").append(getColumn()).append(")");
        return sb.toString();
    }
}
