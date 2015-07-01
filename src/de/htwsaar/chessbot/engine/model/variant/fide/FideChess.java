package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

import java.util.*;

/**
* Beschreibung.
*
* @author
*/
public class FideChess implements ChessVariant {
    
    private static final BoardChecker CHECKER = new FideBoardChecker();

    /**
    * Standardkonstruktor.
    */ 
    public FideChess() {

    }

    public final Collection<Piece> getPiecePrototypes() {
        return Arrays.asList(new Piece[] {
            new Pawn(),
            new King(),
            new Queen(),
            new Rook(),
            new Knight(),
            new Bishop()
        });
    }

    public final Pieces getPieceFactory() {
        return Pieces.getFactoryForPrototypes(getPiecePrototypes());
    }

    public final Board getBoard() {
        return new Board(8,8);
    }

    public final BoardBuilder getBoardBuilder() {
        return new FideBoardBuilder();
    }

    public final void checkBoard(final Board board) {
        CHECKER.check(board);
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
}
