package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.core.BitBoardUtils;
import de.htwsaar.chessbot.core.Board;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.COLORS;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.invert;
import static de.htwsaar.chessbot.core.BitBoardUtils.Color.signOf;
import static de.htwsaar.chessbot.core.BitBoardUtils.FILE_MASK;
import de.htwsaar.chessbot.core.Position;
import de.htwsaar.chessbot.core.pieces.Pawn;
import de.htwsaar.chessbot.core.pieces.Rook;
import de.htwsaar.chessbot.util.Bitwise;

/**
 * Bewertung der Türme auf dem Schachbrett:
 * offene Linie, halb offene Linie und Turm auf 7/2
 * @author Dominik Becker
 * @author Johannes Haupt
 */
public class RookEvaluator extends EvaluationFunction {

	private static final int OPEN_LINE = 15;
	private static final int HALF_OPEN_LINE = 10;
	private static final int ROOK_AT_7 = 20;

	public int evaluate(final Board b) {
        int score = 0;
        
        for (int color : COLORS) {
            int sign = signOf(color);
            long rooks = b.getPieceBits(Rook.ID, color);
            score += sign * Bitwise.count(RANK_MASKS[color] & rooks) * ROOK_AT_7;
            long ownPawns = b.getPieceBits(Pawn.ID, color);
            long enemyPawns = b.getPieceBits(Pawn.ID, invert(color));
            while (rooks != 0L) {
                int index = Bitwise.lowestBitIndex(rooks);
                long rook = Bitwise.lowestBit(rooks);
                long file = FILE_MASK[Position.fileOf(index)-1];
                long line = (color == WHITE ? ~(rook | (rook-1)) : (rook-1));
                line &= file;
                if ((line & ownPawns) == 0L) {
                    if ((line & enemyPawns) == 0L) {
                        score += sign * OPEN_LINE;
                    } else {
                        score += sign * HALF_OPEN_LINE;
                    }
                }
                rooks = Bitwise.popLowestBit(rooks);
            }
        }
        return score;
	}
    
    @Override
    public boolean isAbsolute() {
        return true;
    }
    
    private static final long[] RANK_MASKS = new long[2];
    static {
        RANK_MASKS[WHITE] = BitBoardUtils.RANK_MASK[6];
        RANK_MASKS[BLACK] = BitBoardUtils.RANK_MASK[1];
    }
}
