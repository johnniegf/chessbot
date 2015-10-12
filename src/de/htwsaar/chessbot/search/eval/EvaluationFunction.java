package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.config.Config;
import de.htwsaar.chessbot.util.Exceptions;
import de.htwsaar.chessbot.core.pieces.Bishop;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.pieces.King;
import de.htwsaar.chessbot.core.pieces.Knight;
import de.htwsaar.chessbot.core.pieces.Pawn;
import de.htwsaar.chessbot.core.pieces.Queen;
import de.htwsaar.chessbot.core.pieces.Rook;

/**
 * Bewertungsfunktion für Stellungen.
 * 
* Eine Bewertungsfunktion kann eine Stellung aufgrund vieler verschiedener
 * Kriterien bewerten.
 * 
* @author Johannes Haupt, Dominik Becker
 */
public abstract class EvaluationFunction {

    /**
     * Obere Schranke für alle Bewertungen.
     */
    public static final int INFINITE = 1_000_000;
    
    /**
     * Figurbewertungen in centi-pawns.
     */
    private static int[] scores;

    static {
        scores = new int[6];
        scores[King.ID]   = 300_000;
        scores[Queen.ID]  = 900;
        scores[Rook.ID]   = 500;
        scores[Knight.ID] = 300;
        scores[Bishop.ID] = 300;
        scores[Pawn.ID]   = 100;
        final Config cfg = Config.getInstance();
        cfg.addSpinOption("KingScore" ,  scores[King.ID],   0, INFINITE);
        cfg.addSpinOption("QueenScore",  scores[Queen.ID],  0, INFINITE);
        cfg.addSpinOption("RookScore",   scores[Rook.ID],   0, INFINITE);
        cfg.addSpinOption("KnightScore", scores[Knight.ID], 0, INFINITE);
        cfg.addSpinOption("BishopScore", scores[Bishop.ID], 0, INFINITE);
        cfg.addSpinOption("PawnScore",   scores[Pawn.ID],   0, INFINITE);
    }

    public static int getPieceValue(final int id) {
        Exceptions.checkInBounds(id, "pieceId", 0, 5);
        return scores[id];
    }

    public static void updatePieceValues() {
        Config cfg = Config.getInstance();
        scores[King.ID] = (Integer) cfg.getOption("KingScore").getValue();
        scores[Queen.ID] = (Integer) cfg.getOption("QueenScore").getValue();
        scores[Rook.ID] = (Integer) cfg.getOption("RookScore").getValue();
        scores[Knight.ID] = (Integer) cfg.getOption("KnightScore").getValue();
        scores[Bishop.ID] = (Integer) cfg.getOption("BishopScore").getValue();
        scores[Pawn.ID] = (Integer) cfg.getOption("PawnScore").getValue();
    }

    /**
     * Bewerte die übergebene Stellung.
     *
     * @param board die zu bewertende Stellung
     * @return die Bewertung der übergebenen Stellung.
     * @throws NullPointerException falls <code>board == null</code>
     */
    public abstract int evaluate(final Board b);

}
