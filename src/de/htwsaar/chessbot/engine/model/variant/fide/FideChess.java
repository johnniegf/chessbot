package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

import java.util.*;

/**
* Beschreibung.
*
* @author
*/
public class FideChess extends ChessVariant {
    
    private static final Collection<Piece> PROTOTYPES;
    static {
        PROTOTYPES = Arrays.asList(new Piece[] {
            new Pawn(),
            new King(),
            new Queen(),
            new Rook(),
            new Knight(),
            new Bishop()
        });
    }

    private final Pieces mFactory;
    private final BoardBuilder mBuilder;
    /**
    * Standardkonstruktor.
    */ 
    public FideChess() {
        mFactory = Pieces.getFactory(PROTOTYPES);
        mBuilder = new FideBoardBuilder();
    }

    public Board getBoard() {
        return new Board(8,8);
    }

    public Pieces getPieceFactory() {
        return mFactory;
    }

    public final BoardBuilder getBoardBuilder() {
        return mBuilder;
    }

    public final Collection<Move> getMoveTypes() {
        return Arrays.asList( new Move[]{
            new Move(),
            new DoublePawnMove(),
            new PawnConversion(),
            new EnPassant(),
            new Castling()
        });
    }

    public final boolean isLegal(final Board board) {
        if (isKingInCheck(board))
            return false;
        if (!isPieceCountOk(board))
            return false;

        return true;
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Standardspiel nach FIDE-Regeln");
        return sb.toString();
    }

    private boolean isKingInCheck(final Board board) {
        Collection<Piece> kings = board.getPiecesByType(new King().id());
        boolean w = board.isWhiteAtMove();
        for (Piece pc : kings) {
            if (pc.isWhite() != w) {
                if (board.isAttacked(w, pc.getPosition()) > 0)
                    return true;
            }
        }
        return false;
    }

    private boolean isPieceCountOk(final Board board) {
        return true;
    }
}
