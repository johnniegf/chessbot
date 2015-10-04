package de.htwsaar.chessbot.engine.model.piece;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.Position;
import java.util.Collection;
/**
* Schachfigur.
*
* Jede Schachfigur wird durch 2 Haupteigenschaften bestimmt, nämlich
* ihre Farbe und ihre Position. Jede Figurart hat eigene Zugregeln,
* welche in den Unterklassen implementiert werden. Figuren werden
* erzeugt und zwischengespeichert von der Figurenfabrik. Diese 
* wiederrum wird von der aktiven Spielvariante erzeugt und 
* bereitgestellt.
*
* @author Kevin Alberts
* @author Johannes Haupt
* @see Piece
* @see ChessVariant
*/
public interface Piece {
    
    /**
    * Gib die Liste der von der Figur bedrohten Felder zurück.
    *
    * @param context derzeitige Stellung der Figur
    * @return Liste bedrohter Felder.
    */
    Collection<Position> getAttacks(final Board context);
    
    long getAttackBits(final Board context);

    /**
    * Gib zurück, ob in der übergebenen Stellung diese Figur das
    * übergebene Feld angreift.
    *
    * @param context die aktuelle Stellung
    * @param targetSquare das zu prüfende Feld
    * @return <code>true</code>, falls diese Figur <code>targetSquare
    *         </code> angreift, sonst <code>false</code>
    */
    boolean attacks(final Board context, 
                    final Position targetSquare);

    /**
    * Gib die Liste möglicher Züge zurück.
    *
    * @param context die aktuelle Stellung
    * @return ein <code>Collection<code> aller möglichen Züge
    *         in der Stellung
    */
    Collection<Move> getMoves(final Board context);
    
    long getMoveBits(final Board context);

    /**
    * Gib zurück, ob in der übergebenen Stellung diese Figur auf
    * das übergebene Feld ziehen kann
    *
    * @return <code>true</code>, falls die Figur auf das Feld ziehen
    *         kann, sonst false
    */ 
    boolean canMoveTo(final Board context, 
                      final Position targetSquare);

    /**
    * Gib die eindeutige Identifikationsnummer dieser Figurart zurück.
    *
    * @return ID dieser Figurenart
    */
    int id();

    /**
    * Gib zurück, ob die Figur weiß ist.
    *
    * @return <code>true</code> falls die Figur weiß ist,
    *         sonst <code>false</code>
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
    * Gib die aktuelle Position der Figur aus.
    *
    * @return das Feld, auf dem die Figur derzeit steht.
    */
    Position getPosition();

    /**
    * Lege die Position dieser Figur fest.
    *
    * @param newPosition Zielfeld der Figur
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
    *
    * @return eine Kopie dieser Figur
    */
    Piece clone();

    /**
    * Ziehe diese Figur auf das übergebene Feld.
    *
    * @param targetSquare Zielfeld.
    * @return die gezogene Figur
    */
    Piece move(final Position targetSquare);

    /**
    * Gib den Zobrist-Hash dieser Figur zurück.
    *
    * @return Hashwert der Figur
    */
    long hash();

    String toString();
}
