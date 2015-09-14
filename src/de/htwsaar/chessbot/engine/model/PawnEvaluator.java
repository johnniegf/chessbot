package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.*;

public class PawnEvaluator extends MaterialEvaluator{

	private static final int DOUBLE_PAWN = 10;
	private static final int ISOLATED_PAWN = 20;
	private static final int BACKWARD_PAWN = 8;
	private static final String COL = "abcdefgh";
	
	@Override
	public int evaluate(Board b) {
		
		return super.evaluate(b) - calculateWhite(b);
	}
	
	private int calculateWhite(Board b) {
		int malus = 0;
		int[] pawnCounts = new int[8];
		
		for(int y = 1; y < 9; y++) {
			for(int x = 0; x < COL.length(); x++) {
				Piece p =b.getPieceAt(P(String.valueOf(COL.charAt(x)+y)));
				if(p instanceof Pawn && p.isWhite()) {
					pawnCounts[x] += 1;
					if(pawnCounts[x-1] == 0 && pawnCounts[x+1] == 0) {
						malus += BACKWARD_PAWN;
					}
					if(pawnCounts[x] > 1) {
						malus += DOUBLE_PAWN;
					}
				}
			}
		}
		for(int i = 1; i < pawnCounts.length-1; i++) {
			if(pawnCounts[i] >= 1) {
				if(pawnCounts[i-1] == 0 && pawnCounts[i+1] == 0) {
					malus += ISOLATED_PAWN * pawnCounts[i];
				}
			}
		}
		if(pawnCounts[0] >= 1) {
			if(pawnCounts[1] == 0) {
				malus += ISOLATED_PAWN * pawnCounts[0];
			}
		}
		if(pawnCounts[7] >= 1) {
			if(pawnCounts[6] == 0) {
				malus += ISOLATED_PAWN * pawnCounts[7];
			}
		}
		
		//TODO Freibauer für schwarz und umgekehrt.
		
		return malus;
	}
	
	
	
}
