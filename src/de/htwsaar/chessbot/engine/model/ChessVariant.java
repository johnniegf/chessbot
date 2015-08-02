package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.engine.model.variant.fide.*;

import java.util.Collection;

/**
* Schachspielvariante.
*
*
*
* @author Johannes Haupt
*/
public abstract class ChessVariant {

    private static final ChessVariant DEFAULT = new FideChess();

    private static ChessVariant sCurrent;

    public static Piece PC(final char fenShort,
                           final Position position,
                           final boolean hasMoved)
    {
        Pieces factory = getActive().getPieceFactory();
        Piece pc = factory.get(fenShort,position,hasMoved);
        return pc;
    }

    public static void setActive(final ChessVariant variant) {
        if (variant == null)
            throw new NullPointerException("variant");
        sCurrent = variant;   
    }

    public static ChessVariant getActive() {
        if (sCurrent == null)
            sCurrent = DEFAULT;
        return sCurrent;
    }

    private MoveCache mMoveCache;

    protected ChessVariant() {
        mMoveCache = new MoveCache(getMoveTypes());
    }

    public abstract boolean isLegal(final Board board);

    public abstract Board getBoard();

    /**
    * Gib einen Stellungserbauer für diese Variante zurück.
    *
    * @return einen Stellungserbauer
    */
    public abstract BoardBuilder getBoardBuilder();
 
    public Move getMove(final char flag, 
                        final Position start, 
                        final Position target) 
    {
        return mMoveCache.get(flag,start,target);
    }

    /**
    * Gib die FigurenFabrik dieser Variante zurück.
    *
    * @return <code>true</code>, wenn die Figurenfabrik initialisiert wurde,
    *         sonst false
    */
    public abstract Pieces getPieceFactory();

    protected Collection<Move> getMoveTypes() {
        return Arrays.asList(
            new Move[] { 
                new Move(Position.INVALID, Position.INVALID)
            }
        );
    }
}
