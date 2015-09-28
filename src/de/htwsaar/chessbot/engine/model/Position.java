package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.util.Bitwise;
import java.util.*;
/**
* Feld auf dem Schachbrett.
*
*  
*
* @author Johannes Haupt
*/
public final class Position 
        implements Comparable<Position> 
{
    /** Maximale Anzahl Spalten */
    public static final byte MAX_FILE = 8;

    /** Maximale Anzahl Reihen */
    public static final byte MAX_RANK = 8;

    private static PositionCache sCache = new PositionCache(); 

    /**
    * Gib das Feld mit der übergebenen algebraischen Notation zurück.
    *
    * @param sanPosition Standard-algebraische Notation der Position.
    * @return die zu sanPosition gehörige Position oder eine ungültige
    *         Position, falls sanPosition nicht existiert.
    */
    public static Position P(final String sanPosition) {
        if (sanPosition == null)
            throw new NullPointerException("sanPosition");
        return sCache.get(sanPosition);
    }

    /**
    * Erzeuge ein Positionsobjekt aus den übergebenen Koordinaten.
    *
    * @param file Spalte der Position
    * @param rank Zeile der Position
    * @return Position mit den übergebenen Koordinaten
    */
    public static Position P(int file, int rank) {
        return P((byte) file, (byte) rank);
    }
    
    /**
    * Gib das Feld mit den übergebenen Koordinaten zurück.
    *
    * @param file Spalte der Position
    * @param rank Zeile der Position
    * @return Position mit den übergebenen Koordinaten
    */
    public static Position P(byte file, byte rank) {
        if ( !isValid(file,rank) )
            return INVALID;
        return sCache.get(file,rank);
    }
    
    public static Position P(final byte index) {
        if (index < 0 || index > 63)
            return INVALID;
        return P(index / 8 + 1, index % 8 + 1);
    }
    
    public static Position P(final long bitboard) {
        if (Bitwise.count(bitboard) != 1)
            return INVALID;
        return P(Bitwise.lowestBit(bitboard));
    }

    /**
    *
    */
    public static Collection<Position> PList(String... sanStrings) {
        Collection<Position> positions = new ArrayList<Position>();
        if (sanStrings == null)
            throw new NullPointerException("sanStrings");
        
        for (String san : sanStrings) {
            if (san == null)
                continue;
            positions.add(P(san));
        }
        return positions;
    }
        
    /**
    *
    */
    public static boolean isValid(final byte file, final byte rank) {
        return file > 0 && file <= MAX_FILE
            && rank > 0 && rank <= MAX_RANK;
    }

    /** Ugültige Position */
    public static final Position INVALID 
        = new Position((byte) 0, (byte) 0);

    private final byte mFile;
    private final byte mRank;

    /**
    * Erzeuge ein Positionsobjekt aus den übergebenen Koordinaten.
    *
    * @param file Spalte der Position
    * @param rank Zeile der Position
    * @return Position mit den übergebenen Koordinaten
    */
    protected Position(final byte file, final byte rank) {
        mFile = file;
        mRank = rank;
    }

    /**
    * Gib zurück, ob diese Position gültig ist.
    *
    * Eine Position ist gültig, wenn gilt: 
    * <code>0 &lt; file() &lt;= MAX_FILE</code> und 
    * <code>0 &lt; rank() &lt;= MAX_RANK</code>
    *
    * @return <code>true</code>, wenn die Position gültig ist,
    *         sonst <code>false</code>
    */
    public boolean isValid() {
        return isValid(mFile,mRank);
    }

    /**
    * Gib zurück, ob die Position auf dem übergebenen Brett existiert.
    *
    * @param board das zu prüfende Brett
    * @return <code>true</code>, wenn die Position innerhalb der
    *         Dimensionen des Bretts liegt, sonst <code>false</code>
    */
    public boolean existsOn(final Board board) {
        return isValid();
    }

    /**
    * Verschiebe diese Position um die übergebenen Deltas.
    *
    * @param deltaFile Verschiebung in x-Richtung
    * @param deltaRank Verschiebung in y-Richtung
    * @return die verschobene Position
    */
    public Position transpose(int deltaFile, int deltaRank) {
        return transpose((byte) deltaFile, (byte) deltaRank);
    }

    /**
    * Verschiebe diese Position um die übergebenen Deltas.
    *
    * @param deltaFile Verschiebung in x-Richtung
    * @param deltaRank Verschiebung in y-Richtung
    * @return die verschobene Position
    */
    public Position transpose(byte deltaFile, byte deltaRank) {
        return P((byte) (mFile + deltaFile), (byte) (mRank + deltaRank));
    }

    /**
    * Gib die x-Koordinate dieser Position zurück.
    */
    public byte file() {
        return mFile;
    }

    /**
    * Gib die y-Koordinate dieser Position zurück.
    */
    public byte rank() {
        return mRank;
    }

    /**
    * Gib zurück, ob dieses Feld weiß ist.
    */
    public boolean isWhite() {
        return (file() + rank()) % 2 != 0; 
    }

    public long toLong() {
        return 1 << (hashCode()-1);
    }
    
    public int index() {
        return (MAX_FILE * (rank()-1)) + (file()-1);
    }

    public int hashCode() {
        return index();
    }

    /**
    * Vergleiche mit einem anderen Feld.
    *
    * Felder mit gültigen Positionen werden zweistufig geordnet, 
    * zunächst nach der Zeile auf der sie liegen und dann nach der 
    * Spalte. 
    * Eine ungültige Position ist immer kleiner als eine gültige
    * und gleich (sogar identisch zu) jeder ungültigen Position.
    *
    * @param other zu vergleichendes Feld
    * @return Ergebnis des Vergleichs.
    * @see java.lang.Comparable#compareTo
    */
    public int compareTo(final Position other) {
        if (isValid()) {
            int result = Byte.compare(mRank, other.rank());
            if (result == 0) {
                result = Byte.compare(mFile, other.file());
            }
            return result;
        } else {
            return (other.isValid() ? -1 : 0);
        }
    }

    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other == this) return true;

        if (other instanceof Position) {
            Position p = (Position) other;
            return this.compareTo(p) == 0;
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
        if ( file < 1 || file > MAX_FILE )
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
