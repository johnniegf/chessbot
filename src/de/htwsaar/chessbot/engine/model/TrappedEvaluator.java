package de.htwsaar.chessbot.engine.model;


/**
 * ueberprueft ob eine Figur trapped ist, d.h.
 * ob sie sich nur auf eins oder kein Feld bewegen kann,
 * welches nicht bedroht wird.
 * @author Dominik Becker
 *
 */
public class TrappedEvaluator extends EvaluationFunction{

	private static final int TRAPPED_PIECE = 150;
	@Override
	public int evaluate(Board b) {
		return -(trappedBishop(b) + trappedKnight(b));
	}
	
	private int trappedBishop(Board b) {
		int malus = 0;
		for(Piece bh : b.getPiecesByType(Bishop.ID)) {
			if(isTrapped(bh, b)) {
				if(bh.isWhite())
					malus += TRAPPED_PIECE;
				else malus -= TRAPPED_PIECE;
			}
		}
		return malus;
	}
	
	private int trappedKnight(Board b) {
		int malus = 0;
		for(Piece k : b.getPiecesByType(Knight.ID)) {
			if(isTrapped(k, b)) {
				if(k.isWhite())
					malus += TRAPPED_PIECE;
				else malus -= TRAPPED_PIECE;
			}
		}
		
		return malus;
	}
	
	/**
	 * ueberprueft ob trapped.
	 * @param p Figur
	 * @param b Board
	 * @return true, false
	 */
	private boolean isTrapped(Piece p, Board b) {
		int count = 0;
		for(Move m : p.getMoves(b)) {
			if(b.isAttacked(p.isWhite(), m.getTarget()) == 0) {
				count++;
			}
		}
		if(count > 1)
			return false;
		else return true;
	}
	
}
