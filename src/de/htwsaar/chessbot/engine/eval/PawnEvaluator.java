package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.East;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.FILE_MASK;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.West;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.shift;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.util.Bitwise;

/**
 * Bewertung der Bauern auf dem Schachbrett: rueckstaendiger Bauer,
 * Doppel-Bauer, Isolierter Bauer, freier Bauer
 *
 * @author Dominik Becker
 *
 */
public class PawnEvaluator extends EvaluationFunction {

    private static final int DOUBLED_PAWN = -10;
    private static final int ISOLATED_PAWN = -20;
    private static final int BACKWARD_PAWN = -8;
    private static final int PASSED_PAWN = +20;
    private static final int FREE_LINE = +15;

    
    public PawnEvaluator() {
        
    }
    
    @Override
    public int evaluate(final Board board) {
        long whitePawns = board.getPieceBits(Pawn.ID, WHITE);
        long blackPawns = board.getPieceBits(Pawn.ID, BLACK);
        long allPieces  = board.getOccupiedBits();
        
        return getIsolatedPawnScore(whitePawns, blackPawns)
             + getDoubledPawnScore(whitePawns, blackPawns)
             + getBackwardPawnBlack(blackPawns)
             + getBackwardPawnWhite(whitePawns)
             + getPassedPawnScore(whitePawns, blackPawns, allPieces);
    }
    
    private int getBackwardPawnWhite(long whitePawns) {
        int score = 0;
        for (int file = 0; file < 8; file++) {
            long pawns = FILE_MASK[file] & whitePawns;
            while (pawns != 0L) {
                long pawn = Bitwise.lowestBit(pawns);
                long neighbors = (pawn-1) & FILE_MASK[file];
                neighbors = shift(West, neighbors) | shift(East, neighbors);
                if ((neighbors & whitePawns) == 0L) {
                    score += BACKWARD_PAWN;
                }
                pawns ^= pawn;
            }
        }
        return score;    
    }
    
    private int getBackwardPawnBlack(long blackPawns) {
        int score = 0;
        for (int file = 0; file < 8; file++) {
            long pawns = FILE_MASK[file] & blackPawns;
            while (pawns != 0L) {
                long pawn = Bitwise.highestBit(pawns);
                long neighbors = ~(pawn | (pawn-1)) & FILE_MASK[file];
                neighbors = shift(West, neighbors) | shift(East, neighbors);
                if ((neighbors & blackPawns) == 0L) {
                    score -= BACKWARD_PAWN;
                }
                pawns ^= pawn;
            }
        }
        return score;
    }
    
    private int getIsolatedPawnScore(long whitePawns, long blackPawns) {
        int score = 0;
        long currentFile;
        long neighbors;
        for (int file = 0; file < 8; file++) {
            currentFile = FILE_MASK[file];
            neighbors = shift(West, currentFile) | shift(East, currentFile);
            if ((currentFile & whitePawns) != 0L) {
                if ((neighbors & whitePawns) == 0L) {
                    score += ISOLATED_PAWN;
                }
            }
            if ((currentFile & blackPawns) != 0L) {
                if ((neighbors & blackPawns) == 0L) {
                    score -= ISOLATED_PAWN;
                }
            }
        }
        return score;
    }
    
    private int getDoubledPawnScore(long whitePawns, long blackPawns) {
        int score = 0;
        for (int file = 0; file < 8; file++) {
            if (Bitwise.count(FILE_MASK[file] & whitePawns) > 1)
                score += DOUBLED_PAWN;
            if (Bitwise.count(FILE_MASK[file] & blackPawns) > 1)
                score -= DOUBLED_PAWN;
        }
        return score;
    }
    
    private int getPawnOnFreeLineScore(long whitePawns, long blackPawns, long pieces) {
        int score = 0;
        for (int file = 0; file < 8; file++) {
            long pawn = Bitwise.highestBit(FILE_MASK[file] & whitePawns);
            long inFront;
            if (pawn != 0L) {
                inFront = ~(pawn | (pawn-1)) & FILE_MASK[file];
                if ((inFront & pieces) == 0L)
                    score += FREE_LINE;
            }
            pawn = Bitwise.lowestBit(FILE_MASK[file] & blackPawns);
            if (pawn != 0L) {
                inFront = (pawn-1) & FILE_MASK[file];
                if ((inFront & pieces) == 0L)
                    score -= FREE_LINE;
            }
        }
        return score;
    }
    
    private int getPassedPawnScore(long whitePawns, long blackPawns, long occupation) {
        int score = 0;
        long pawns, pawn, inFront, neighbors;
        for (int file = 0; file < 8; file++) {
            pawns = FILE_MASK[file] & whitePawns;
            while (pawns != 0L) {
                pawn = Bitwise.highestBit(pawns);
                inFront = ~(pawn | (pawn-1)) & FILE_MASK[file];
                neighbors = shift(West, inFront) | shift(East, inFront);
                if ((neighbors & blackPawns) == 0L) {
                    score += PASSED_PAWN;
                    if ((inFront & occupation) == 0L) {
                        score += FREE_LINE;
                    }
                }
                pawns ^= pawn;   
            }
            pawns = FILE_MASK[file] & blackPawns;
            while (pawns != 0L) {
                pawn = Bitwise.highestBit(pawns);
                inFront = (pawn-1) & FILE_MASK[file];
                neighbors = shift(West, inFront) | shift(East, inFront);
                if ((neighbors & whitePawns) == 0L) {
                    score -= PASSED_PAWN;
                    if ((inFront & occupation) == 0L) {
                        score += FREE_LINE;
                    }
                }
                pawns ^= pawn;   
            }
        }
        return score;
    }

}
