package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.engine.model.piece.Piece;
import java.util.*;
import static de.htwsaar.chessbot.util.Exceptions.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class ZobristHasher {

    private static ZobristHasher sInstance;
    private static boolean initialized = false;

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

    private static final long RNG_SEED       = 0x2660cddb1c76d1a4L;
    private static final int  PIECE_COUNT    = 6 * 2 * 64;
    private static final int  CASTLING_COUNT = 16;


    private final long[] mPieceHashes;
    private final long[] mCastlingHashes;
    private final long[] mColourHashes;
    private final long[] mEnPassantHashes;

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
    }

    public long hashPiece(final Piece pieceToHash) {
        checkNull(pieceToHash, "pieceToHash");
        return hashPiece(pieceToHash.id(), pieceToHash.isWhite(), pieceToHash.getPosition().index());
    }
    
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
        checkNull(p, "enPassant");
        if (!p.isValid())
            return 0L;
        return getEnPassantHash(p.file());
    }
    
    public long getEnPassantHash(final byte file) {
        if (file > 0 && file <= 8)
            return mEnPassantHashes[file-1];
        else return 0L;
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
