package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

import java.util.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class FideBoardChecker implements BoardChecker{
    
    private static Map<String, Integer> maxima;
    private static Map<String, Integer> starting;

    static {
        maxima = new HashMap<String, Integer>();
        maxima.put("P", 8);  maxima.put("p", 8);
        maxima.put("B", 10); maxima.put("b", 10);
        maxima.put("N", 10); maxima.put("n", 10);
        maxima.put("R", 10); maxima.put("r", 10);
        maxima.put("Q", 9);  maxima.put("q", 9);
        maxima.put("K", 1);  maxima.put("k", 1);
        
        starting = new HashMap<String, Integer>();
        starting.put("P", 8); starting.put("p", 8);
        starting.put("B", 2); starting.put("b", 2);
        starting.put("N", 2); starting.put("n", 2);
        starting.put("R", 2); starting.put("r", 2);
        starting.put("Q", 1); starting.put("q", 1);
        starting.put("K", 1); starting.put("k", 1);
    }
    
    private void inCheck(final Board board) {
        
        String kingFen = board.isWhiteMoving() ? "k" : "K";
        King king = null;
        for (Piece p : board.getPieces()) {
             if (p.toFEN().equals(kingFen)) {
                 king = (King) p;
                 break;
             }
        }
        if (king != null) {
            if (board.isAttacked(king.getPosition(), !board.isWhiteMoving()))
                throw new RuleViolation(EXN_IN_CHECK);
        } else
             throw new RuleViolation(EXN_NO_KING);
    }

    private static void badPieceCount(final Board board) {
        Map<String, Integer> pieceCount = new HashMap<String, Integer>();
        pieceCount.put("P", 0); pieceCount.put("p", 0);
        pieceCount.put("B", 0); pieceCount.put("b", 0);
        pieceCount.put("N", 0); pieceCount.put("n", 0);
        pieceCount.put("R", 0); pieceCount.put("r", 0);
        pieceCount.put("Q", 0); pieceCount.put("q", 0);
        pieceCount.put("K", 0); pieceCount.put("k", 0);

        for (Piece p : board.getPieces()) {
            String fen = p.toFEN();
            pieceCount.put(fen, pieceCount.get(fen) + 1);
        }
        
        int convertedPawnCountWhite = 0;
        int convertedPawnCountBlack = 0;
        for (String key : maxima.keySet()) {
            if (pieceCount.get(key) > maxima.get(key))
                throw new RuleViolation(EXN_PIECE_COUNT);

            if (!key.matches("[Pp]")) {
                int convertedPawnCount = ( pieceCount.get(key) - starting.get(key) );
                if (convertedPawnCount > 0)
                    if (Character.isUpperCase(key.charAt(0)))
                        convertedPawnCountWhite += convertedPawnCount;
                    else    
                        convertedPawnCountBlack += convertedPawnCount;
            }
        }
        if (convertedPawnCountWhite + pieceCount.get("P") > maxima.get("P"))
            throw new RuleViolation(EXN_PIECE_COUNT);

        if (convertedPawnCountBlack + pieceCount.get("p") > maxima.get("p"))
            throw new RuleViolation(EXN_PIECE_COUNT);
    }

    public void check(final Board board) {
        badPieceCount(board);
        inCheck(board);
    }

    public boolean isOk(final Board board) {
        try {
            check(board);
            return true;
        } catch (RuleViolation rv) {
            return false;
        }
    }

    private static final String EXN_NO_KING =
        "Es existiert kein König auf dem Brett!";
    private static final String EXN_PIECE_COUNT =
        "Zu viele Figuren auf dem Brett!";
    private static final String EXN_IN_CHECK =
        "König wurde nicht aus dem Schach gezogen!";
}
