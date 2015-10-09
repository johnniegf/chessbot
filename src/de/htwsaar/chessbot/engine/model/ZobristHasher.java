package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.engine.model.piece.Piece;
import java.util.*;
import static de.htwsaar.chessbot.util.Exceptions.*;

/**
* Erzeugt und vergibt Zobrist-Hashkonstanten.
*
* @author Johannes Haupt
*/
public class ZobristHasher {

    private static ZobristHasher sInstance;
    private static boolean initialized = false;

    /**
     * Gib die globale Instanz des Hashers zurück.
     * Falls die Instanz nicht existiert, wird sie zunächst erzeugt.
     * @return globale Instanz des ZobristHasher
     */
    public static ZobristHasher getInstance() {
        if (!isInitialized())
            initialize();
        return sInstance;
    }
    
    private static boolean isInitialized() {
        return initialized;   
    }

    private static void initialize() {
        sInstance = new ZobristHasher();
        initialized = true;
    }

    /**
     * Initialisierungswert für den Zufallszahlengenerator.
     * Ein guter Seed erzeugt eine vorhersagbare, aber trotzdem gleichmäßige
     * Verteilung.
     */
//    public static final long RNG_SEED       = 0x2660cddb1c76d1a4L;
//    public static final long RNG_SEED       = 0x354bb49ee49d0095L;
    public static final long RNG_SEED       = 0x261be203e9123e8cL;
    private static final int  PIECE_COUNT    = 6 * 2 * 64;
    private static final int  CASTLING_COUNT = 16;


    private final long[] mPieceHashes;
    private final long[] mCastlingHashes;
    private final long[] mColourHashes;
    private final long[] mEnPassantHashes;
    private final long   mEnPassantInvalid;

    /**
    * Standardkonstruktor.
    */ 
    private ZobristHasher() {
        Random rng = new Random(RNG_SEED);
        mPieceHashes = new long[PIECE_COUNT];
        mCastlingHashes = new long[CASTLING_COUNT];
        mColourHashes = new long[2];
        mEnPassantHashes = new long[8];

        for (int i = 0; i < PIECE_COUNT; i++) {
            mPieceHashes[i] = rng.nextLong();
        }

        for (int i = 0; i < CASTLING_COUNT; i++) {
            mCastlingHashes[i] = rng.nextLong();
        }

        for (int i = 0; i < 2; i++) {
            mColourHashes[i] = rng.nextLong();
        }
        
        for (int i = 0; i < Position.MAX_FILE; i++) {
            mEnPassantHashes[i] = rng.nextLong();
        }
        mEnPassantInvalid = rng.nextLong();
    }

    /**
     * Gib den Hashwert der übergebenen Figur zurück.
     * @param pieceToHash zu hashende Figur
     * @return Hash der Figur
     * @throws NullPointerException
     *      falls <code>pieceToHash == null</code>
     */
    public long hashPiece(final Piece pieceToHash) {
        checkNull(pieceToHash, "pieceToHash");
        return hashPiece(pieceToHash.id(), pieceToHash.isWhite(), pieceToHash.getPosition().index());
    }
    
    /**
     * Gib den Hashwert der Figur mit den übergebenen Attributen zurück.
     * @param pieceType Art der Figur
     * @param isWhite   Farbe der Figur
     * @param positionIndex Index der Figurenposition.
     * @return Hashwert der Figur
     * @throws IndexOutOfBoundsException
     *      falls <code>pieceType</code> kein Figurtyp ist, oder
     * @see Piece#id
     */
    public long hashPiece(final int pieceType,
                          final boolean isWhite,
                          final int positionIndex)
    {
        checkInBounds(pieceType, 0, 5);
        checkInBounds(positionIndex, 0, 63);
        int index = makeIndex(pieceType, isWhite, positionIndex);
        checkInBounds(index,0,PIECE_COUNT);
        return mPieceHashes[index];
    }

    public long getCastlingHash(final byte castlingType) {
        return mCastlingHashes[castlingType];
    }
    
    public long getEnPassantHash(final Position p) {
        checkNull(p);
        if (!p.isValid())
            return mEnPassantInvalid;
        return getEnPassantHash(p.file());
    }
    
    public long getEnPassantHash(final byte file) {
        if (file > 0 && file <= 8)
            return mEnPassantHashes[file-1];
        else return mEnPassantInvalid;
    }

    public long getColourHash(final boolean forWhite) {
        return mColourHashes[(forWhite ? 1 : 0)];
    }

    private int makeIndex(final int pieceType,
                          final boolean isWhite,
                          final int posIndex) 
    {
        int index = pieceType << 7;
        index += (isWhite? 1 : 0) << 6;
        index += posIndex;
        return index;
    }

}
