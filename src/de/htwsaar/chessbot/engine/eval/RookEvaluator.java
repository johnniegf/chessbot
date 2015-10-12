package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.BitBoardUtils;
import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.COLORS;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.invert;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.signOf;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.FILE_MASK;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import de.htwsaar.chessbot.util.Bitwise;

/**
 * Bewertung der Türme auf dem Schachbrett:
 * offene Linie, halb offene Linie und Turm auf 7/2
 * @author Dominik Becker
 *
 */
public class RookEvaluator extends EvaluationFunction {

	//Malus bzw Bonus
	private static final int OPEN_LINE = 15;
	private static final int HALF_OPEN_LINE = 10;
	private static final int ROOK_AT_7 = 20;

	
	private String[] col;
	
	public RookEvaluator(){
		col = new String[8];
		
		col[0] = "a";
		col[1] = "b";
		col[2] = "c";
		col[3] = "d";
		col[4] = "e";
		col[5] = "f";
		col[6] = "g";
		col[7] = "h";
	}
	
	public int evaluate(final Board b) {
		return calculate(b);
	}
    
    private static final long[] RANK_MASKS = new long[2];
    static {
        RANK_MASKS[WHITE] = BitBoardUtils.RANK_MASK[6];
        RANK_MASKS[BLACK] = BitBoardUtils.RANK_MASK[1];
    }
	
	/**
	 * berechnet den Malus bzw den Bonus der Türme
	 * @param Board b
	 * @return malus 
	 */
	private int calculate(final Board b) {
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
}
