package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class BitBoard {

    private static final int WHITE = 0;
    private static final int BLACK = 1;

    private static int color(boolean isWhite) {
        return (isWhite ? WHITE : BLACK);
    }

    protected long[] mColors;
    protected long[] mPieces;
    protected long mOccupied;
    
    protected BitBoard() {
        mColors = new long[2];
        mPieces = new long[6]; 
    }

    public boolean pieceExists(final Piece piece) {
        if (piece == null)
            return false;

        int c = color(piece.isWhite());
        int i = piece.id();
        long pos = piece.getPosition().toLong();
        long bitboard = mColors[c] & mPieces[i];
        return (bitboard & pos) > 0;
    }

    public boolean isOccupied(final Position pos) {
        checkNull(pos, "position");
        long p = pos.toLong();
        return mOccupied & p != 0;
    }

    public boolean putPiece(final Piece piece) {
        if (isOccupied(piece.getPosition())) {
            return false;
        }
        long p = piece.getPosition().toLong();
        int i = piece.id();
        int c = color(piece.isWhite());
        mPieces[i] |= p;
        mColors[c] |= p;
        update();
        return true;
    }

    public boolean removePieceAt(final Position pos) {
        if (!isOccupied(pos))
            return false;

        long p = pos.toLong();
        int t;
        for (t = 0; t < 6; t++) {
            if (mPieces[t] & p != 0)
                break;
        }
        mPieces[t] ^= p;
        for (int i = 0; i < 2; i++) {
            if (mColors[i] & p == 0) {
                mColors[i] ^=  p;
                break;
            }
        }
        update();
        return true;
    }

    public Piece getPieceAt(final Position pos) {
        if (!isOccupied(pos))
            return null;
        long p = pos.toLong();
        int i;
        for (i = 0; i < 6; i++) {
            if (mPieces[i] & p != 0)
                break;
        }
        int c;
        for (c = 0; c < 2; c++) {
            if (mColors[c] & p != 0)
                break;
        }
        return PC(i,c,pos);

    }

    private void update() {
        mOccupied = mColors[WHITE] | mColors[BLACK];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
}
