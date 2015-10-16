package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.core.BitBoardUtils;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.toColor;
import static de.htwsaar.chessbot.core.BitBoardUtils.getGameStage;
import de.htwsaar.chessbot.core.pieces.Bishop;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.pieces.King;
import de.htwsaar.chessbot.core.pieces.Knight;
import de.htwsaar.chessbot.core.pieces.Pawn;
import de.htwsaar.chessbot.core.pieces.Piece;
import de.htwsaar.chessbot.core.pieces.Queen;
import de.htwsaar.chessbot.core.pieces.Rook;
import de.htwsaar.chessbot.util.Bitwise;
import static de.htwsaar.chessbot.util.Exceptions.checkCondition;

/**
 * Erweiterung des MaterialEvaluators mit Positionsbewertungen.
 *
 * @author David Holzapfel
 *
 */
public class PositionBasedEvaluator extends EvaluationFunction {
    
    private static final int OPENING = BitBoardUtils.GameStage.OPENING.ordinal();
    private static final int MIDGAME = BitBoardUtils.GameStage.MIDGAME.ordinal();
    private static final int ENDGAME = BitBoardUtils.GameStage.ENDGAME.ordinal();

    private static final int[] PAWN_VALUES_OPENING = {
     //  a    b    c    d    e    f   g   h
         0,   0,   0,   0,   0,   0,  0,  0,  // 1
         5,  10,   5, -20, -20,   5, 10,  5,  // 2
         5,  -5,  15,   0,   0,  15, -5,  5,  // 3
         0,   0,   0,  20,  20,   0,  0,  0,  // 4
         5,   5,  10,  25,  25,  10,  5,  5,  // 5
        10,  10,  20,  30,  30,  20, 10, 10,  // 6
        50,  50,  50,  50,  50,  50, 50, 50,  // 7
         0,   0,   0,   0,   0,   0,  0,  0   // 8
    };
    
    private static final int[] PAWN_VALUES_MIDGAME = {
     //  a    b    c    d    e    f   g   h
         0,   0,   0,   0,   0,   0,  0,  0,  // 1
         5,  10,  10, -20, -20,  10, 10,  5,  // 2
         5,  -5,  15,   0,   0,  15, -5,  5,  // 3
         0,   0,   0,  20,  20,   0,  0,  0,  // 4
         5,   5,  10,  25,  25,  10,  5,  5,  // 5
        10,  10,  20,  30,  30,  20, 10, 10,  // 6
        50,  50,  50,  50,  50,  50, 50, 50,  // 7
         0,   0,   0,   0,   0,   0,  0,  0   // 8
    };
    
    private static final int[] PAWN_VALUES_ENDGAME = {
     //  a    b    c    d    e    f   g   h
         0,   0,   0,   0,   0,   0,  0,  0,  // 1
         0,   0,   0,   0,   0,   0,  0,  0,  // 2
         0,   0,   0,   0,   0,   0,  0,  0,  // 3
         0,   0,   0,   0,   0,   0,  0,  0,  // 4
         0,   0,   0,   0,   0,   0,  0,  0,  // 5
        30,  30,  30,  30,  30,  30, 30, 30,  // 6
        50,  50,  50,  50,  50,  50, 50, 50,  // 7
         0,   0,   0,   0,   0,   0,  0,  0   // 8
    };

    private static final int[] KNIGHT_VALUES = {
        -50, -40, -30, -30, -30, -30, -40, -50,
        -40, -20,   0,   5,   5,   0, -20, -40,
        -30,   5,  20,  15,  15,  20,   5, -30,
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
        10, 10, 10, 10, 10, 10, 10, 10,
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
        -20, -10, -10, -5, -5, -10, -10, -20
    };
        
    private static final int[] KING_VALUES_OPENING_WHITE = {
         20,  20,  30, -20,   0, -20,  30,  20,
         20,  20, -10, -20, -20, -20,  20,  20,
        -10, -20, -20, -20, -20, -20, -20, -10,
        -20, -30, -30, -40, -40, -30, -30, -20,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30
    };
    
    private static final int[] KING_VALUES_OPENING_BLACK = {
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -20, -30, -30, -40, -40, -30, -30, -20,
        -10, -20, -20, -20, -20, -20, -20, -10,
         20,  20, -20, -20, -20, -20,  20,  20,
         20,  30,  30, -20,   0, -20,  30,  20
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
    
    private static final int[][][][] PIECE_VALUES = new int[6][2][3][];

    private static final int PIECE_VALUE_WEIGHT = 50;
    private static final int PIECE_POSITION_WEIGHT = 50;
    private static final int WEIGHT_SUM = 
        PIECE_VALUE_WEIGHT + PIECE_POSITION_WEIGHT;
    
    static {
        PIECE_VALUES[King.ID][WHITE][OPENING]   = KING_VALUES_OPENING_WHITE;
        PIECE_VALUES[King.ID][BLACK][OPENING]   = KING_VALUES_OPENING_BLACK;
        PIECE_VALUES[King.ID][WHITE][MIDGAME]   = KING_VALUES_OPENING_WHITE;
        PIECE_VALUES[King.ID][BLACK][MIDGAME]   = KING_VALUES_OPENING_BLACK;
        PIECE_VALUES[King.ID][WHITE][ENDGAME]   = KING_VALUES_ENDGAME;
        PIECE_VALUES[King.ID][BLACK][ENDGAME]   = reverse(KING_VALUES_ENDGAME);
        
        for (int i = 0; i < 3; i++) {
            PIECE_VALUES[Queen.ID][WHITE][i]  = QUEEN_VALUES;
            PIECE_VALUES[Queen.ID][BLACK][i]  = reverse(QUEEN_VALUES);
            PIECE_VALUES[Knight.ID][WHITE][i]  = KNIGHT_VALUES;
            PIECE_VALUES[Knight.ID][BLACK][i]  = reverse(KNIGHT_VALUES);
            PIECE_VALUES[Bishop.ID][WHITE][i]  = BISHOP_VALUES;
            PIECE_VALUES[Bishop.ID][BLACK][i]  = reverse(BISHOP_VALUES);
            PIECE_VALUES[Rook.ID][WHITE][i]  = ROOK_VALUES;
            PIECE_VALUES[Rook.ID][BLACK][i]  = reverse(ROOK_VALUES);
        }
        
        PIECE_VALUES[Pawn.ID][WHITE][OPENING]   = PAWN_VALUES_OPENING;
        PIECE_VALUES[Pawn.ID][BLACK][OPENING]   = reverse(PAWN_VALUES_OPENING);
        PIECE_VALUES[Pawn.ID][WHITE][MIDGAME]   = PAWN_VALUES_MIDGAME;
        PIECE_VALUES[Pawn.ID][BLACK][MIDGAME]   = reverse(PAWN_VALUES_MIDGAME);
        PIECE_VALUES[Pawn.ID][WHITE][ENDGAME]   = PAWN_VALUES_ENDGAME;
        PIECE_VALUES[Pawn.ID][BLACK][ENDGAME]   = reverse(PAWN_VALUES_ENDGAME);
    }

    private static int[] reverse(int[] array) {
        checkCondition(array.length == 64);
        int[] result = new int[64];
        for (int i = 0; i < 32; i++) {
            result[i] = array[63-i];
            result[63-i] = array[i];
        }
        return result;
    }

    @Override
    public int evaluate(Board b) {
        int materialCount = 0;
        int sign;
        int gameStage = getGameStage(b).ordinal();
        for (Piece piece : b.getAllPieces()) {
            sign = piece.isWhite() ? 1 : -1;
            materialCount += sign * (
                getPieceValue(piece.id()) +
                getPositionValue(piece, gameStage)
            );
        }
        return materialCount;
    }
    
    @Override
    public boolean isAbsolute() {
        return true;
    }

    private int getPositionValue(Piece p, int gameStage) {
        int position = p.getPosition().index();
        int color = toColor(p.isWhite());
        int pid = p.id();
        return PIECE_VALUES[pid][color][gameStage][position];
    }
}
