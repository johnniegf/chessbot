package de.htwsaar.chessbot.engine.model;

    import java.util.*;
    /**
* Beschreibung.
*
* @author Johannes Haupt
*/
public final class Position 
        implements Comparable<Position> 
{

    public static final byte MAX_FILE = Board.MAX_WIDTH;
    public static final byte MAX_RANK = Board.MAX_HEIGHT;

    private static PositionCache sCache = new PositionCache(); 

    public static Position P(final String sanPosition) {
        return sCache.get(sanPosition);
    }

    public static Position P(int file, int rank) {
        return P((byte) file, (byte) rank);
    }

    public static Position P(byte file, byte rank) {
        return sCache.get(file,rank);
    }
        
    private static boolean isValidPos(final byte file, final byte rank) {
        return file > 0 && file <= MAX_FILE
            && rank > 0 && rank <= MAX_RANK;
    }

    public static final Position INVALID = new Position();


    private final byte mFile;
    private final byte mRank;

    /**
    * Standardkonstruktor.
    */ 
    protected Position() {
        this((byte) 0,(byte) 0);   
    }

    protected Position(final byte file, final byte rank) {
        mFile = file;
        mRank = rank;
    }

    public boolean isValid() {
        return isValidPos(mFile,mRank);
    }

    public boolean existsOn(final Board board) {
        return isValid()
            && mFile <= board.width()
            && mRank <= board.height();
    }

    public Position transpose(int deltaFile, int deltaRank) {
        return transpose((byte) deltaFile, (byte) deltaRank);
    }

    public Position transpose(byte deltaFile, byte deltaRank) {
        return P((byte) (mFile + deltaFile), (byte) (mRank + deltaRank));
    }

    public byte file() {
        return mFile;
    }

    public byte rank() {
        return mRank;
    }

    public boolean isWhite() {
        return (file() + rank()) % 2 != 0; 
    }

    public int hashCode() {
        return (Board.MAX_WIDTH * (mRank-1)) + mFile;
    }

    public int compareTo(final Position other) {
        int result = Byte.compare(mRank, other.rank());
        if (result == 0) {
            result = Byte.compare(mFile, other.file());
        }
        return result;
    }

    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other == this) return true;

        if (other instanceof Position) {
            Position p = (Position) other;
            return this.compareTo(p) == 0
                || this.isValid() == p.isValid();
        } else {
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
        if (isValid()) {
            sb.append(getLetter(file()))
              .append(rank());
        } else
            sb.append("??");
        return sb.toString();
    }

    private static char getLetter(final byte file) {
        if ( file < 'a' || file > 'z' )
            throw new IllegalArgumentException("file");
        return (char) ('a' + (file-1));
    }
}

class PositionCache {
    private Map<String,Position> mCache;

    public PositionCache() {
        mCache = new HashMap<String,Position>();
        for (byte f = 1; f <= Position.MAX_FILE; f++) {
            for (byte r = 1; r <= Position.MAX_RANK; r++) {
                mCache.put(makeIndex(f,r), new Position(f,r));
            }
        }
    }

    public Position get(final String san) {
        Position p = mCache.get(san.trim());
        if (p == null) {
            p = Position.INVALID;
        }
        return p;
    }

    public Position get(final byte file, final byte rank) {
        return get(makeIndex(file,rank));       
    }

    private static final char A = 'a';
    private static String makeIndex(final byte file, final byte rank) {
        return (char) (A + (file-1)) + "" + rank;
    }
}
