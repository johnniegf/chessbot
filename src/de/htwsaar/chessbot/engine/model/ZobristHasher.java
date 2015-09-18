package de.htwsaar.chessbot.engine.model;

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
    private static final int  CASTLING_COUNT = 4;


    private long[] mPieceHashes;
    private long[] mCastlingHashes;
    private long[] mColourHashes;

    /**
    * Standardkonstruktor.
    */ 
    private ZobristHasher() {
        Random rng = new Random(RNG_SEED);
        mPieceHashes = new long[PIECE_COUNT];
        mCastlingHashes = new long[CASTLING_COUNT];
        mColourHashes = new long[2];

        for (int i = 0; i < PIECE_COUNT; i++) {
            mPieceHashes[i] = rng.nextLong();
        }

        for (int i = 0; i < CASTLING_COUNT; i++) {
            mCastlingHashes[i] = rng.nextLong();
        }

        for (int i = 0; i < 2; i++) {
            mColourHashes[i] = rng.nextLong();
        }
    }

    public long hashPiece(final Piece pieceToHash) {
        checkNull(pieceToHash, "pieceToHash");
        int index = makeIndex(pieceToHash);
        checkInBounds(index,0,PIECE_COUNT);
        return mPieceHashes[index];
    }

    public long getCastlingHash(final char castlingType) {
        int index = 0;       
        switch (castlingType) {
            case 'K':
                break;
            case 'Q':
                index = 1;
                break;
            case 'k':
                index = 2;
                break;
            case 'q':
                index = 3;
                break;
            default:
                throw new FenStringParseException();
        }
        return mCastlingHashes[index];
    }

    public long getColourHash(final boolean forWhite) {
        return mColourHashes[(forWhite ? 1 : 0)];
    }

    private int makeIndex(final Piece piece) {
        int index = piece.id() << 7;
        index += (piece.isWhite() ? 1 : 0) << 6;
        index += piece.getPosition().rank() * 8;
        index += piece.getPosition().file();
        return index;
    }

}
