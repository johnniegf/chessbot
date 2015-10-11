package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.piece.Bishop;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.piece.King;
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
    
    private static final int MIDGAME = 0;
    private static final int ENDGAME = 1;

    private static final int[] PAWN_VALUES = {
     //  a    b    c    d    e    f   g   h
         0,   0,   0,   0,   0,   0,  0,  0,  // 1
         5,  10,  10, -20, -20,  10, 10,  5,  // 2
         5,  -5, -10,   0,   0, -10, -5,  5,  // 3
         0,   0,   0,  20,  20,   0,  0,  0,  // 4
         5,   5,  10,  25,  25,  10,  5,  5,  // 5
        10,  10,  20,  30,  30,  20, 10, 10,  // 6
        50,  50,  50,  50,  50,  50, 50, 50,  // 7
         0,   0,   0,   0,   0,   0,  0,  0   // 8
    };

    private static final int[] KNIGHT_VALUES = {
        -50, -40, -30, -30, -30, -30, -40, -50,
        -40, -20,   0,   5,   5,   0, -20, -40,
        -30,   5,  10,  15,  15,  10,   5, -30,
        -30,   0,  15,  20,  20,  15,   0, -30,
        -30,   5,  15,  20,  20,  15,   5, -30,
        -30,   0,  10,  15,  15,  10,   0, -30,
        -40, -20,   0,   0,   0,   0, -20, -40,
        -50, -40, -30, -30, -30, -30, -40, -50
    };

    private static final int[] BISHOP_VALUES = {
        -20, -10, -10, -10, -10, -10, -10, -20,
        -10,   5,   0,   0,   0,   0,   5, -10,
        -10,  10,  10,  10,  10,  10,  10, -10,
        -10,   0,  10,  10,  10,  10,   0, -10,
        -10,   5,   5,  10,  10,   5,   5, -10,
        -10,   0,   5,  10,  10,   5,   0, -10,
        -10,   0,   0,   0,   0,   0,   0, -10,
        -20, -10, -10, -10, -10, -10, -10, -20
    };

    private static final int[] ROOK_VALUES = {
         0,  0,  0,  5,  5,  0,  0,  0,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
         5, 10, 10, 10, 10, 10, 10,  5,
         0,  0,  0,  0,  0,  0,  0,  0
    };

    private static final int[] QUEEN_VALUES = {
        -20, -10, -10, -5, -5, -10, -10, -20,
        -10,   0,   5,  0,  0,   0,   0, -10,
        -10,   5,   5,  5,  5,   5,   0, -10,
          0,   0,   5,  5,  5,   5,   0,  -5,
         -5,   0,   5,  5,  5,   5,   0,  -5,
        -10,   0,   5,  5,  5,   5,   0, -10,
        -10,   0,   0,  0,  0,   0,   0, -10,
        -20, -10, -10, -5, -5, -10, -10, -20,
    };

    private static final int[] KING_VALUES_MIDGAME = {
         20,  30,  10,   0,   0,  10,  30,  20,
         20,  20,   0,   0,   0,   0,  20,  20,
        -10, -20, -20, -20, -20, -20, -20, -10,
        -20, -30, -30, -40, -40, -30, -30, -20,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30
    };

    private static final int[] KING_VALUES_ENDGAME = {
        -50, -30, -30, -30, -30, -30, -30, -50,
        -30, -30,   0,   0,   0,   0, -30, -30,
        -30, -10,  20,  30,  30,  20, -10, -30,
        -30, -10,  30,  40,  40,  30, -10, -30,
        -30, -10,  30,  40,  40,  30, -10, -30,
        -30, -10,  20,  30,  30,  20, -10, -30,
        -30, -20, -10,   0,   0, -10, -20, -30,
        -50, -40, -30, -20, -20, -30, -40, -50
    };
    
    private static final int[][][] PIECE_VALUES = new int[6][2][];

    private static final int PIECE_VALUE_WEIGHT = 80;
    private static final int PIECE_POSITION_WEIGHT = 20;
    private static final int WEIGHT_SUM = 
        PIECE_VALUE_WEIGHT + PIECE_POSITION_WEIGHT;
    
    static {
        PIECE_VALUES[King.ID][MIDGAME]   = KING_VALUES_MIDGAME;
        PIECE_VALUES[King.ID][ENDGAME]   = KING_VALUES_ENDGAME;
        PIECE_VALUES[Queen.ID][MIDGAME]  = QUEEN_VALUES;
        PIECE_VALUES[Queen.ID][ENDGAME]  = QUEEN_VALUES;
        PIECE_VALUES[Rook.ID][MIDGAME]   = ROOK_VALUES;
        PIECE_VALUES[Rook.ID][ENDGAME]   = ROOK_VALUES;
        PIECE_VALUES[Knight.ID][MIDGAME] = KNIGHT_VALUES;
        PIECE_VALUES[Knight.ID][ENDGAME] = KNIGHT_VALUES;
        PIECE_VALUES[Bishop.ID][MIDGAME] = BISHOP_VALUES;
        PIECE_VALUES[Bishop.ID][ENDGAME] = BISHOP_VALUES;
        PIECE_VALUES[Pawn.ID][MIDGAME]   = PAWN_VALUES;
        PIECE_VALUES[Pawn.ID][ENDGAME]   = PAWN_VALUES;
    }

    @Override
    public int evaluate(Board b) {
        int materialCount = 0;
        int sign;
        int endgame = isEndGame(b);
        for (Piece piece : b.getAllPieces()) {
            sign = piece.isWhite() ? 1 : -1;
            materialCount += sign * (
                PIECE_VALUE_WEIGHT * getPieceValue(piece.id()) +
                PIECE_POSITION_WEIGHT * getPositionValue(b, piece, endgame)
            );
        }
        return materialCount / WEIGHT_SUM;
    }

    private int getPositionValue(Board b, Piece p, int endgame) {
        int i = p.getPosition().index();
        if (!p.isWhite())
            i = 63 - i;
       
        int pid = p.id();
        return PIECE_VALUES[pid][endgame][i];
    }

    private int isEndGame(Board b) {
		//EndGame -> 1) Beide Spieler ohne Dame
        //      oder 2) Jeder Spieler mit Dame hoechstens 1 "kleine" Figur (Laeufer/Springer)
        long whitePieces = b.getPieceBitsForColor(true);
        long queens = b.getPieceBitsForType(Queen.ID);
        if (queens == 0L)
            return ENDGAME;

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
        
        return (condition2 ? ENDGAME : MIDGAME);
    }

}
