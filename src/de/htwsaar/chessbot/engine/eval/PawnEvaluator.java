package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.eval.MaterialEvaluator;
import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.WHITE;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import static de.htwsaar.chessbot.engine.model.Position.*;

/**
 * Bewertung der Bauern auf dem Schachbrett: rueckstaendiger Bauer,
 * Doppel-Bauer, Isolierter Bauer, freier Bauer
 *
 * @author Dominik Becker
 *
 */
public class PawnEvaluator extends EvaluationFunction {

    private static final int DOUBLE_PAWN = 10;
    private static final int ISOLATED_PAWN = 20;
    private static final int BACKWARD_PAWN = 8;
    private static final int FREE_PAWN = 20;

    public PawnEvaluator() {
        
    }
    
    @Override
    public int evaluate(final Board board) {
        long whitePawns = board.getPieceBits(Pawn.ID, WHITE);
        long blackPawns = board.getPieceBits(Pawn.ID, BLACK);
        
        return getIsolatedPawnScore(whitePawns, blackPawns);
    }
    
    private int getIsolatedPawnScore(long whitePawns, long blackPawns) {
        return 0;
    }

}
