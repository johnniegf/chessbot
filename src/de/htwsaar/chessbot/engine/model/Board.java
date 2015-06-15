package de.htwsaar.chessbot.engine.model;

import java.util.Collection;
import java.util.ArrayList;

/**
* Beschreibung.
*
* @author
*/
public class Board {
    
    private final int width;
    private final int height;
    private Piece[][] pieces;

    /**
    * Standardkonstruktor.
    */ 
    public Board() {
        this(8,8);
    }

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

    public boolean isAttacked(Position p, boolean byWhite) {
        return false;
    }

    public boolean isFree(Position p) {
        return pieceAt(p) == null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Piece pieceAt(Position at) {
        
        return null;
    }

    public Collection<Piece> getPieces() {
        Collection<Piece> result = new ArrayList<Piece>();
        for (Piece[] col : pieces) {
            for (Piece p : col)
                if (p != null)
                    result.add(p);
        }
        return result;
        
    }

    public Piece getPiece(Piece seek) {
        for (Piece p : getPieces()) {
            if (p.equals(seek))
                return p;
        }
        return null;
    }

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
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
}
