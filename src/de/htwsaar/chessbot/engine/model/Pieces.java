package de.htwsaar.chessbot.engine.model;

import java.util.Set;
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
    
    public static Pieces getFactory(final Set<Piece> prototypes) {
        return new Pieces(prototypes);
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

        index = index << 1;
        index += (hasMoved ? 1 : 0); 

        index = index << 5;
        index += (int) (fenShort - (isWhite ? 'A' : 'a'));

        index = index << 11;
        index += position.hashCode();
        return index;
    }

//---------------------------------------------------------

    private Map<Character,Piece> mPrototypes;
    private Map<Integer,Piece>   mCache;

    private Pieces(final Set<Piece> prototypes) {
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
        if ( !isFenLetter(fenShort) )
            throw new IllegalArgumentException("fenShort");
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
        return (character >= 'A' && character <= 'Z')
            || (character >= 'a' && character <= 'z');
    }
}
