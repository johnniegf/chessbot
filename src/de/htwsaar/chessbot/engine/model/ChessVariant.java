package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.engine.model.variant.fide.*;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
/**
* Schachspielvariante.
*
* <p>Eine Spielvariante besteht aus einem Regelsatz, einem Schachbrett 
* mit festgelegten Maßen sowie einer Menge Figuren. Die Variante kann
* </p>
* <ul>
*   <li>feststellen ob eine Stellung regelkonform ist, </li>
*   <li>feststellen ob die Bedingungen für das Spielende erfüllt sind</li>
*   <li>Figuren erzeugen</li>
*   <li>Züge erzeugen</li>
* </ul>
*
* @author Johannes Haupt
*/
public abstract class ChessVariant {

    private static final ChessVariant DEFAULT = new FideChess();

    private static ChessVariant sCurrent;

    /**
    * Gib die Figur mit den übergebenen Eigenschaften zurück,
    * erzeuge sie, falls sie nicht existiert.
    */
    public static Piece PC(final char fenShort,
                           final Position position,
                           final boolean hasMoved)
    {
        Pieces factory = getActive().getPieceFactory();
        Piece pc = factory.get(fenShort,position,hasMoved);
        return pc;
    }

    /**
    * Gib den Zug mit den übergebenen Eigenschaften zurück, erzeuge
    * ihn, falls er nicht existiert.
    */
    public static Move MV(final Position start,
                          final Position target)
    {
        return MV(start, target, Move.FLAG);
    }

    /**
    * Gib den Zug mit den übergebenen Eigenschaften zurück, erzeuge
    * ihn, falls er nicht existiert.
    */
    public static Move MV(final Position start,
                          final Position target,
                          final char flag)
    {
        return getActive().getMove(start, target, flag);
    }

    /**
    * Lege die aktive Spielvariante fest.
    *
    * Die Spielvariante ist elementarer Bestandteil der Engine und wird
    * von den meisten Modellklassen verwendet, um Objekte (u.a. Figuren, 
    * Züge oder Stellungen) zu erzeugen. Es wird empfohlen, die aktive
    * Spielvariante nur zwischen Partien zu ändern, da das Verhalten der
    * Engine andernfalls undefiniert ist.
    *
    * @param variant die zu aktivierende Spielvariante.
    */
    public static void setActive(final ChessVariant variant) {
        if (variant == null)
            throw new NullPointerException("variant");
        sCurrent = variant;   
    }

    /**
    * Gib die aktive Spielvariante zurück.
    *
    * @return die aktive Spielvariante.
    */
    public static ChessVariant getActive() {
        if (sCurrent == null)
            sCurrent = DEFAULT;
        return sCurrent;
    }

    private Move.Cache mMoveCache;
    private Pieces    mPieceFactory;

    protected ChessVariant() {
        mMoveCache = new Move.Cache(getMoves());
        mPieceFactory = Pieces.getFactory(getPieces());
    }

    /**
    * Gib zurück, ob die übergebene Stellung regelkonform ist.
    *
    * @param board zu prüfende Stellung
    * @return <code>true</code> falls <code>board</code>
    */ 
    public abstract boolean isLegal(final Board board);

    /**
    * Erzeuge ein neues leeres Schachbrett mit den Maßen dieser Variante.
    *
    * @return leeres Schachbrett für diese Variante.
    */
    public abstract Board getBoard();

    /**
    * Gib einen Stellungserbauer für diese Variante zurück.
    *
    * @return einen Stellungserbauer
    */
    public abstract BoardBuilder getBoardBuilder();
 
    /**
    * Gib den Zug der übergebenen Art, Start- und Zielfeld zurück.
    *
    * @param start Startfeld des Zugs
    * @param target Zielfeld des Zugs
    * @param flag Art des Zugs (z.B. Rochade)
    * @return Zug mit den übergebenen Eigenschaften.
    * @throws NullPointerException falls <code>start</code> oder
    *           <code>target == null</code>
    * @throws MoveException falls der Zug nicht erzeugt werden kann, z.B.
    *           wegen falscher Zugart oder illegaler Kombination aus 
    *           Start- und Zielfeld
    */
    public Move getMove(final Position start, 
                        final Position target,
                        final char flag) 
    {
        return mMoveCache.get(flag,start,target);
    }

    /**
    * Gib die FigurenFabrik dieser Variante zurück.
    *
    * @return die Figurenfabrik dieser Variante 
    */
    public Pieces getPieceFactory() {
        return mPieceFactory;
    }

    /**
    * Gib die Figurprototype dieser Variante zurück.
    *
    * @return eine <code>Collection</code> der Figurprototypen dieser
    *         Variante
    */
    protected abstract Set<Piece> getPieces();

    /**
    * Gib die Zugprototypen dieser Variante zurück.
    *
    * @return eine <code>Collection</code> der Zugprototypen dieser 
    *         Variante
    */
    protected Set<Move> getMoves() {
        Set<Move> movePrototypes = new HashSet<Move>();
        movePrototypes.add(new Move());
        return movePrototypes;
    }
}
