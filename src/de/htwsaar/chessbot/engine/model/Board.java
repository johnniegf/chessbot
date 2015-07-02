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

    public boolean addPiece(Piece newPiece) {
        if (newPiece == null)
            return false;

        Position p = newPiece.getPosition();
        if (!p.existsOn(this) || !isFree(p))
            return false;
       
        int x = p.getRow() - 1;
        int y = p.getColumn() - 1;

        pieces[x][y] = newPiece;
        return true;
    }

    public boolean removePiece(Piece delPiece) {
        Piece p = getPiece(delPiece);
        if (p == null)
            return false;

        int x = p.getPosition().getRow() - 1;
        int y = p.getPosition().getColumn() - 1;
        pieces[x][y] = null;
        return true;
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
        if (at == null || !at.existsOn(this))
            return null;

        int x = at.getRow() - 1;
        int y = at.getColumn() - 1;

        return pieces[x][y];
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

    public Board clone() {
        Board cloned = new Board(width, height);
        for (Piece p : getPieces()) {
            cloned.addPiece(p.clone());
        }
        return cloned;
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
            if (pieceCount() != b.pieceCount())
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

    public int pieceCount() {
        return getPieces().size();
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(width).append("x").append(height).append("]");
        for (Piece p : getPieces())
            sb.append(p).append(", ");

        return sb.toString();
    }
}
