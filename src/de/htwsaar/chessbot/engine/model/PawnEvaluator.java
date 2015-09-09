package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.*;

public class PawnEvaluator extends MaterialEvaluator{

	private static final int DOUBLE_PAWN = 10;
	private static final int ISOLATED_PAWN = 20;
	private static final String COL = "abcdefgh";
	
	@Override
	public int evaluate(Board b) {
		
		return super.evaluate(b) + calculateDoublePawnWhite(b)- calculateDoublePawnBlack(b);
	}
	
	
	private int calculateIsolatedPawnWhite(Board b) {
		int malus = 0;
		String currentCol;
		
		
		for(int i = 0; i < COL.length(); i++) {
			for(int j = 1; j < 9; j++) {
				currentCol = String.valueOf(COL.charAt(i));
				Piece p = b.getPieceAt(P(currentCol+j));
				if(p instanceof Pawn && p.isWhite()) {
				}
			}
		}
		return malus;
	}
	
	private int calculateDoublePawnWhite(Board b) {
		int malus = 0;
		String currentCol;
		boolean isDouble = false;
		
		for(int i = 0; i < COL.length(); i++){
			for(int j = 1; j < 9; j++){
				currentCol = String.valueOf(COL.charAt(i));
				Piece p = b.getPieceAt(P(currentCol+j));
				if(p instanceof Pawn && p.isWhite()) {
						if(!isDouble){
							isDouble = true;
						}
						else {
							malus += DOUBLE_PAWN;
						}
				}
			}
		}
		return malus;
	}
	
	private int calculateDoublePawnBlack(Board b) {
		int malus = 0;
		
		String currentCol;
		boolean isDouble = false;
		
		for(int i = 0; i < COL.length(); i++) {
			for(int j = 1; j < 9; j++) {
				currentCol = String.valueOf(COL.charAt(i));
				Piece p = b.getPieceAt(P(currentCol+j));
				if(p instanceof Pawn && !p.isWhite()) {
					if(!isDouble) {
						isDouble = true;
					}
					else {
						malus += DOUBLE_PAWN;
					}
				}
			}
		}
		return malus;
	}

}
