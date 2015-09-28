package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
/**
* Figurfabrik.
*
* Erzeugt und verwaltet Figuren. Hierbei kommt <em>lazy loading</em>
* zum Einsatz, d.h. Figuren werden erst erzeugt und im 
* Zwischenspeicher abgelegt, sobald sie das erste Mal angefordert
* werden.
*
* Figuren werden durch Kopieren von Prototypen, welche beim Erstellen
* der Fabrik übergeben werden, erzeugt.
*
* Die Fabrik ist ein Singleton, d.h. es existiert zu jeder Zeit nur eine
* Instanz.
*
* @author Dominik Becker
* @author David Holzapfel
* @author Johannes Haupt
*/
public final class Pieces {

    public static Piece PC(final char fenShort, final Position position) {
        return getInstance().get(fenShort, position);
    }

    public static Piece PC(final int pieceId,
                           final boolean isWhite,
                           final Position position)
    {
        return getInstance().get(pieceId, isWhite, position);
    }


    private static Pieces sInstance;

    public static Pieces getInstance() {
        initInstance();
        return sInstance;
    }

    private static void initInstance() {
        if (sInstance == null) {
            sInstance = new Pieces();
        }
    }

//---------------------------------------------------------

    private final Piece[] mPrototypes = new Piece[] {
        new Pawn(), new Knight(), new King(),
        new Rook(), new Bishop(), new Queen()
    };
    private Piece[] mCache;

    private Pieces() {
        mCache = new Piece[MAX_PIECES];
    }
    private static final int PIECE_TYPES = 6;
    private static final int MAX_PIECES = PIECE_TYPES * 2 * 64;

    private boolean addPrototype(final Piece prototype) {
        checkNull(prototype, "prototype");
        Piece current;
        current = prototype.clone();
        mPrototypes[prototype.id()] = current;
        return true;
    }

    private Piece getPrototype(final int pieceId) {
        checkInBounds(pieceId, "pieceId", 0, 5);
        return mPrototypes[pieceId];
    }

    private Piece createPiece(final int pieceId,
                              final boolean isWhite,
                              final Position position)
    {
        Piece newPiece = getPrototype(pieceId).clone();
        newPiece.setIsWhite(isWhite);
        newPiece.setPosition(position);
        return newPiece;
    }

    /**
    * Gib die Figur mit den übergebenen Eigenschaften zurück.
    *
    * Falls die Figur nicht existiert, wird sie zunächst erzeugt
    * und im Zwischenspeicher abgelegt.
    *
    * @param pieceId ID der zu erzeugenden Figur
    * @param isWhite Farbe der Figur
    * @param position Feld der Figur
    * @return die Figur mit den übergebenen Eigenschaften
    * @throws NullPointerException falls <code>position == null</code>
    */
    public synchronized Piece get(final int pieceId,
                                  final boolean isWhite,
                                  final Position position)
    {
        checkNull(position, "position");

        int index = index(pieceId, isWhite, position);
        
        Piece result = mCache[index];
        if ( result == null ) {
            result = createPiece(pieceId, isWhite, position);
            mCache[index] = result;
        }
        return result;       
    }

    public Piece get(final char fenShort, final Position position) {
        int pieceId = getIdFromFen(fenShort);
        boolean isWhite = Character.isUpperCase(fenShort);
        return get(pieceId, isWhite, position);
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

    private static int getIdFromFen(char character) {
        switch (character) {
            case 'P':
            case 'p':
                return Pawn.ID;
            case 'B':
            case 'b':
                return Bishop.ID;
            case 'N':
            case 'n':
                return Knight.ID;
            case 'R':
            case 'r':
                return Rook.ID;
            case 'Q':
            case 'q':
                return Queen.ID;
            case 'K':
            case 'k':
                return King.ID;
             
            default:
                return -1;
        }
    }

    private int index(final int pieceId,
                      final boolean isWhite,
                      final Position position)
    {
        return pieceId << 7 + (isWhite ? 0 : 1) << PIECE_TYPES + position.index();
    }

}
