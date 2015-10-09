package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.BLACK;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.COLORS;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.invert;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import de.htwsaar.chessbot.util.Bitwise;
import java.util.ArrayList;

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
		return -calculate(b);
	}
    
    private static final long[] RANK_MASKS = new long[2];
    private static final long[] FILE_MASKS = new long[8];
    static {
        RANK_MASKS[WHITE] = 0x00ff_0000_0000_0000L;
        RANK_MASKS[BLACK] = 0x0000_0000_0000_ff00L;
        long fileMask = 0x0101_0101_0101_0101L;
        for (int s = 0; s < 8; s++) {
            FILE_MASKS[s] = fileMask << s;
        }
    }
	
	/**
	 * berechnet den Malus bzw den Bonus der Türme
	 * @param Board b
	 * @return malus 
	 */
	private int calculate(final Board b) {
		int malus = 0;
		boolean open = true;
		boolean halfOpen = false;
        
        int score = 0;
        
        for (int color : COLORS) {
            int sign = (color == WHITE ? 1 : -1);
            long rooks = b.getPieceBits(Rook.ID, color);
            score += sign * Bitwise.count(RANK_MASKS[color] & rooks) * ROOK_AT_7;
            long ownPawns = b.getPieceBits(Pawn.ID, color);
            long enemyPawns = b.getPieceBits(Pawn.ID, invert(color));
            while (rooks != 0L) {
                int index = Bitwise.lowestBitIndex(rooks);
                long rook = Bitwise.lowestBit(rooks);
                long file = FILE_MASKS[Position.fileOf(index)-1];
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
        
//		for(Piece r : rooks) {
//			if(r.getPosition().rank() == 7 && r.isWhite()) {
//				malus -= ROOK_AT_7;
//			}
//			else if(r.getPosition().rank() == 2 && !r.isWhite()) {
//				malus += ROOK_AT_7;
//			}
//			for(Piece p : b.getAllPieces(Pawn.ID)) {
//				if(r.getPosition().file() == p.getPosition().file()) {
//					open = false;
//					if(r.isWhite() == !p.isWhite()) {
//						halfOpen = true;
//					}
//					else {
//						halfOpen = false;
//						break;
//					}
//				}
//			}
//			if(open) {
//				if(r.isWhite())
//					malus -= OPEN_LINE;
//				else malus += OPEN_LINE;
//			}
//			if(halfOpen)
//				if(r.isWhite())
//					malus -= HALF_OPEN_LINE;
//				else malus += HALF_OPEN_LINE;
//			
//			open = true;
//			halfOpen = false;
//		}
//		//System.out.println("Malus Türme: "+malus);
//		return malus;
	}
}
