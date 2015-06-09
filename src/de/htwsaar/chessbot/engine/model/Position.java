package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author
*/
public class Position {
    
    private static int MAX_ROWS = 8;
    private static int MAX_COLS = 8;

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
    public Position(int row, int column) {
        this.set(row, column);
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int column) {
        if ( column < 1 || column > MAX_COLS )
            throw new IllegalArgumentException();

        this.column = column;
    }

    public void setRow(int row) {
        if ( row < 1 || row > MAX_ROWS )
            throw new IllegalArgumentException();

        this.row = row;
    }

    public void set(int row, int column) {
        setRow(row);
        setColumn(column);
    }

    public void translate(int deltaX, int deltaY) {
        int trow = this.row + deltaX, 
            tcol = this.column + deltaY;
        
        setRow(trow);
        setColumn(tcol);
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
}
