package de.htwsaar.chessbot.engine.model;

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
* @author Dominik Becker
* @author David Holzapfel
* @author Johannes Haupt
*/
public final class Pieces {

    public static Piece PC(final char fenShort,
                           final Position position,
                           final boolean hasMoved)
    {
        return getInstance().get(fenShort, position, hasMoved);
    }

    public static Piece PC(final char fenShort, final Position position) {
        return PC(fenShort, position, true);
    }


    private static Pieces sInstance;

    public static Pieces getInstance() {
        initInstance();
        return sInstance;
    }

    private static void initInstance() {
        if (sInstance == null) {
            Collection<Piece> prototypes = Arrays.asList(new Piece[]{
                new King(), new Queen(), new Rook(),
                new Bishop(), new Knight(), new Pawn()
            });
            sInstance = new Pieces(prototypes);
        }
    }

//---------------------------------------------------------

    private Map<Character,Piece> mPrototypes;
    private Map<Integer,Piece>   mCache;

    private Pieces(final Collection<Piece> prototypes) {
        if ( prototypes == null )
            throw new NullPointerException("prototypes");

        mPrototypes = new HashMap<Character,Piece>();
        for ( Piece pc : prototypes ) {
            addPrototype(pc);
        }
        mCache = new HashMap<Integer,Piece>();
    }

    private boolean addPrototype(final Piece prototype) {
        Piece current;
        for (int i = 0; i < 2; i++) {
            current = prototype.clone();
            current.setIsWhite(i % 2 == 0);
            mPrototypes.put(current.fenShort(), current);
        }
        return true;
    }

    private Piece getPrototype(final char fenShort) {
        return mPrototypes.get(fenShort);
    }

    private Piece createPiece(final char fenShort,
                              final Position position,
                              final boolean hasMoved)
    {
        Piece newPiece = getPrototype(fenShort).clone();
        newPiece.setPosition(position);
        newPiece.setHasMoved(hasMoved);
        return newPiece;
    }

    /**
    * Gib die Figur mit den übergebenen Eigenschaften zurück.
    *
    * Falls die Figur nicht existiert, wird sie zunächst erzeugt
    * und im Zwischenspeicher abgelegt.
    *
    * @param fenShort FEN-Kürzel der Figur
    * @param position Feld der Figur
    * @param hasMoved ob die Figur bereits gezogen wurde
    * @return die Figur mit den übergebenen Eigenschaften
    * @throws NullPointerException falls <code>position == null</code>
    * @throws PieceFactoryException falls die Figur nicht erzeugt 
    *           werden kann, z.B. wenn für das FEN-Kürzel kein 
    *           Prototyp hinterlegt ist
    */
    public Piece get(final char fenShort,
                     final Position position,
                     final boolean hasMoved)
    {
        if ( !isFenLetter(fenShort) || !mPrototypes.containsKey(fenShort) )
            throw new IllegalArgumentException("fenShort '" + fenShort + "'");
        if ( position == null )
            throw new NullPointerException("position");

        int index = makeIndex(fenShort, position, hasMoved);
        
        Piece result = mCache.get(index);
        if ( result == null ) {
            result = createPiece(fenShort, position, hasMoved);
            mCache.put(index, result);
        }
        return result;       
    }

    public Piece get(final char fenShort, final Position position) {
        return get(fenShort, position, true);   
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

    private static boolean isFenLetter(char character) {
        final String fenLetters = "[PpQqKkNnBbRr]";
        return (character + "").matches(fenLetters);
    }

    private static final int makeIndex(final char fenShort,
                                       final Position position,
                                       final boolean hasMoved)
    {
        int index = 0;
        index += (hasMoved ? 1 : 0);
        
        index = index << 1;
        boolean isWhite = Character.isUpperCase(fenShort);
        index += (isWhite  ? 1 : 0);

        index = index << 5;
        index += (int) (fenShort - (isWhite ? 'A' : 'a'));

        index = index << 11;
        index += position.hashCode();
        return index;
    }

}
