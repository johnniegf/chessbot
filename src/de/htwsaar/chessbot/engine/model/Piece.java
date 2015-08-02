package de.htwsaar.chessbot.engine.model;

import java.util.Collection;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public interface Piece {
    
    /**
    * Gib die Liste der von der Figur bedrohten Felder zurück.
    *
    * @param context derzeitige Stellung
    * @return 
    */
    Collection<Position> getAttacks(final Board context);

    boolean attacks(final Board context, final Position targetSquare);

    /**
    * Gib die Liste möglicher Züge zurück.
    */
    Collection<Move> getMoves(final Board context);

    boolean canMoveTo(final Board context, final Position targetSquare);

    /**
    * Gib die eindeutige Identifikationsnummer dieser Figurart zurück.
    */
    long id();

    /**
    * Gib zurück, ob die Figur weiß ist.
    */
    boolean isWhite();

    /**
    * Lege die Farbe der Figur fest.
    *
    * Binär kodiert, <code>true</code> steht für weiß, 
    * <code>false</code> für schwarz.
    *
    * @param isWhite Farbe der Figur
    */
    void setIsWhite(final boolean isWhite);

    /**
    * Gib zurück, ob die Figur bereits bewegt wurde.
    *
    * @return <code>true</code> falls die Figur bewegt wurde,
    *         sonst <code>false</code>
    */
    boolean hasMoved();

    /**
    * Lege fest, ob die Figur bewegt wurde.
    *
    * @param hasMoved wurde die Figur bewegt?
    */
    void setHasMoved(final boolean hasMoved);

    /**
    * Gib die aktuelle Position der Figur aus.
    *
    * @return
    */
    Position getPosition();

    /**
    *
    */
    void setPosition(final Position newPosition);

    /**
    * Gib das Kürzel der Figur aus, wie es in einem FEN-String erscheint.
    *
    * @return FEN-Kürzel der Figur.
    */
    char fenShort();

    /**
    * Kopiere diese Figur.
    */
    Piece clone();

    Piece move(final Position targetSquare);

    long hash();

}
