package de.htwsaar.chessbot.engine.model.variant.fide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardBuilder;
import de.htwsaar.chessbot.engine.model.ChessVariant;
import de.htwsaar.chessbot.engine.model.Move;
import de.htwsaar.chessbot.engine.model.Piece;
import de.htwsaar.chessbot.engine.model.Pieces;

/**
* Beschreibung.
*
* @author
*/
public class FideChess extends ChessVariant {
    
    private static Collection<Piece> PROTOTYPES = new ArrayList<Piece>();


    private static FideChess sInstance;

    public static FideChess getInstance() {
        System.out.println("FideChess.getInstance");
    	if (sInstance == null)
    		sInstance = new FideChess();
    	return sInstance;
    }
    
    static {
        System.out.println("FideChess.<clinit>");
    	Piece[] proto = new Piece[] { new Pawn(), new King(), new Queen(), new Rook(), new Knight(), new Bishop() };
        for (Piece p : proto) {
            PROTOTYPES.add(p);
        }
        System.out.println("FideChess.PROTOTYPES = " + PROTOTYPES);
    	//ChessVariant.setActive(getInstance());    	
    }

    private BoardBuilder mBuilder;
    /**
    * Standardkonstruktor.
    */ 
    private FideChess() {
        System.out.println("FideChess.<init>");
        setPieceFactory(Pieces.getFactory(PROTOTYPES));    
    }

    public Board getBoard() {
        return new Board(8,8);
    }

    public final BoardBuilder getBoardBuilder() {
    	if (mBuilder == null)
    		mBuilder = new FideBoardBuilder();
        return mBuilder;
    }

    public final Collection<Move> getMoves() {
        return Arrays.asList( new Move[]{
            new Move(),
            new DoublePawnMove(),
            new MovePromotion(new Queen()),
            new MovePromotion(new Rook()),
            new MovePromotion(new Bishop()),
            new MovePromotion(new Knight()),
            new MoveEnPassant(),
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
