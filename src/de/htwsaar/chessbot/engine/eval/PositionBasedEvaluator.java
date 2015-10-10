package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.piece.Bishop;
import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.WHITE;
import de.htwsaar.chessbot.engine.model.piece.Knight;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Queen;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import de.htwsaar.chessbot.util.Bitwise;

/**
 * Erweiterung des MaterialEvaluators mit Positionsbewertungen.
 *
 * @author David Holzapfel
 *
 */
public class PositionBasedEvaluator extends EvaluationFunction {

    private static final int[][] PAWN_VALUES = {
        {0,   0,   0,   0,   0,   0,  0,  0},
        {50, 50,  50,  50,  50,  50, 50, 50},
        {10, 10,  20,  30,  30,  20, 10, 10},
        {5,   5,  10,  25,  25,  10,  5,  5},
        {0,   0,   0,  20,  20,   0,  0,  0},
        {5,  -5, -10,   0,   0, -10, -5,  5},
        {5,  10,  10, -20, -20,  10, 10,  5},
        {0,   0,   0,   0,   0,   0,  0,  0}
    };

    private static final int[][] KNIGHT_VALUES = {
        {-50, -40, -30, -30, -30, -30, -40, -50},
        {-40, -20,   0,   0,   0,   0, -20, -40},
        {-30,   0,  10,  15,  15,  10,   0, -30},
        {-30,   5,  15,  20,  20,  15,   5, -30},
        {-30,   0,  15,  20,  20,  15,   0, -30},
        {-30,   5,  10,  15,  15,  10,   5, -30},
        {-40, -20,   0,   5,   5,   0, -20, -40},
        {-50, -40, -30, -30, -30, -30, -40, -50}

    };

    private static final int[][] BISHOP_VALUES = {
        {-20, -10, -10, -10, -10, -10, -10, -20},
        {-10,   0,   0,   0,   0,   0,   0, -10},
        {-10,   0,   5,  10,  10,   5,   0, -10},
        {-10,   5,   5,  10,  10,   5,   5, -10},
        {-10,   0,  10,  10,  10,  10,   0, -10},
        {-10,  10,  10,  10,  10,  10,  10, -10},
        {-10,   5,   0,   0,   0,   0,   5, -10},
        {-20, -10, -10, -10, -10, -10, -10, -20}
    };

    private static final int[][] ROOK_VALUES = {
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 5, 10, 10, 10, 10, 10, 10,  5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        { 0,  0,  0,  5,  5,  0,  0,  0}
    };

    private static final int[][] QUEEN_VALUES = {
        {-20, -10, -10, -5, -5, -10, -10, -20},
        {-10,   0,   0,  0,  0,   0,   0, -10},
        {-10,   0,   5,  5,  5,   5,   0, -10},
        { -5,   0,   5,  5,  5,   5,   0,  -5},
        {  0,   0,   5,  5,  5,   5,   0,  -5},
        {-10,   5,   5,  5,  5,   5,   0, -10},
        {-10,   0,   5,  0,  0,   0,   0, -10},
        {-20, -10, -10, -5, -5, -10, -10, -20}
    };

    private static final int[][] KING_VALUES_MIDGAME = {
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-20, -30, -30, -40, -40, -30, -30, -20},
        {-10, -20, -20, -20, -20, -20, -20, -10},
        { 20,  20,   0,   0,   0,   0,  20,  20},
        { 20,  30,  10,   0,   0,  10,  30,  20}
    };

    private static final int[][] KING_VALUES_ENDGAME = {
        {-50, -40, -30, -20, -20, -30, -40, -50},
        {-30, -20, -10,   0,   0, -10, -20, -30},
        {-30, -10,  20,  30,  30,  20, -10, -30},
        {-30, -10,  30,  40,  40,  30, -10, -30},
        {-30, -10,  30,  40,  40,  30, -10, -30},
        {-30, -10,  20,  30,  30,  20, -10, -30},
        {-30, -30,   0,   0,   0,   0, -30, -30},
        {-50, -30, -30, -30, -30, -30, -30, -50}
    };

    private static final int PIECE_VALUE_WEIGHT = 50;
    private static final int PIECE_POSITION_WEIGHT = 50;
    private static final int WEIGHT_SUM = 
        PIECE_VALUE_WEIGHT + PIECE_POSITION_WEIGHT;

    @Override
    public int evaluate(Board b) {
        int materialCount = 0;
        int sign;
        for (Piece piece : b.getAllPieces()) {
            sign = piece.isWhite() ? 1 : -1;
            materialCount += sign * (
                PIECE_VALUE_WEIGHT * getPieceValue(piece.id()) +
                PIECE_POSITION_WEIGHT * getPositionValue(b, piece)
            );
        }
        return materialCount / WEIGHT_SUM;
    }

    private int getPositionValue(Board b, Piece p) {
        if (p == null) {
            return 0;
        }

        int x = getX(p);
        int y = getY(p, p.isWhite());
        int i = p.id();
        
        if (i == Pawn.ID) {
            return PAWN_VALUES[x][y];
        } else if (i == Knight.ID) {
            return KNIGHT_VALUES[x][y];
        } else if (i == Bishop.ID) {
            return BISHOP_VALUES[x][y];
        } else if (i == Rook.ID) {
            return ROOK_VALUES[x][y];
        } else if (i == Queen.ID) {
            return QUEEN_VALUES[x][y];
        } else {
            if (isEndGame(b)) {
                return KING_VALUES_ENDGAME[x][y];
            } else {
                return KING_VALUES_MIDGAME[x][y];
            }
        }
    }

    private int getX(Piece p) {
        return p.getPosition().file() - 1;
    }

    private int getY(Piece p, boolean whiteMoves) {
        if (whiteMoves) {
            return p.getPosition().rank() - 1;
        } else {
            return 8 - p.getPosition().rank();
        }
    }

    private boolean isEndGame(Board b) {
		//EndGame -> 1) Beide Spieler ohne Dame
        //      oder 2) Jeder Spieler mit Dame hoechstens 1 "kleine" Figur (Laeufer/Springer)
        long whitePieces = b.getPieceBitsForColor(true);
        long queens = b.getPieceBitsForType(Queen.ID);
        if (queens == 0L)
            return true;

        boolean condition2 = true;
        long minorPieces = b.getPieceBitsForType(Knight.ID)
                         | b.getPieceBitsForType(Bishop.ID);
        long rooks = b.getPieceBitsForType(Rook.ID);
        
        if (Bitwise.count(queens) < 2)
            condition2 = false;
        else if (rooks != 0L)
            condition2 = false;
        else if (Bitwise.count(minorPieces) > 2)
            condition2 = false;
        else if (
            Bitwise.count(minorPieces & whitePieces) > 1 
         || Bitwise.count(minorPieces ^ whitePieces) > 1) {
            condition2 = false;
        }
        
        return condition2;
    }

}
