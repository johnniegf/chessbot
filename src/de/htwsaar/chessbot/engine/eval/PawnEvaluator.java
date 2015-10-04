package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.eval.MaterialEvaluator;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import static de.htwsaar.chessbot.engine.model.Position.*;

/**
 * Bewertung der Bauern auf dem Schachbrett:
 * rueckstaendiger Bauer, Doppel-Bauer,
 *  Isolierter Bauer, freier Bauer

 * @author Dominik Becker
 *
 */
public class PawnEvaluator extends MaterialEvaluator{

	private static final int DOUBLE_PAWN = 10;
	private static final int ISOLATED_PAWN = 20;
	private static final int BACKWARD_PAWN = 8;
	private static final int FREE_PAWN_COM = 20;
	private final String[] col;
	
	public PawnEvaluator() {
		col = new String[8];
		for (char i = 0; i < 8; i++) {
			col[i] = ((char) 'a'+i) + "";
			
		}
		col[0] = "a";
		col[1] = "b";
		col[2] = "c";
		col[3] = "d";
		col[4] = "e";
		col[5] = "f";
		col[6] = "g";
		col[7] = "h";
	}
	
	@Override
	public int evaluate(Board b) {
		return -calculate(b);
	}
	
	private int calculate(Board b) {
		int malus = 0;
		int[] pawnCounts = new int[8];
			for(int y = 2; y < 9; y++) {
				//falls in der Spalte a ein weisser Bauer steht
				Piece pa = b.getPieceAt(P(col[0]+y));
				if(pa instanceof Pawn && pa.isWhite()) {
					pawnCounts[0] += 1;
					if(pawnCounts[1] ==  0) {
						malus += BACKWARD_PAWN;
					}
				}
				for(int x = 1; x < col.length-1; x++) {
					Piece p = b.getPieceAt(P(col[x]+y));
					if(p instanceof Pawn && p.isWhite()) {
						pawnCounts[x] += 1;
						
						if(pawnCounts[x-1] == 0 && pawnCounts[x+1] == 0) {
							malus += BACKWARD_PAWN;
						} else {
							Piece pp = b.getPieceAt(P(col[x-1]+y));
							if(pp instanceof Pawn && pp.isWhite()) {
								if(pawnCounts[x-1] == 1 && pawnCounts[x+1] == 0) {
									malus += BACKWARD_PAWN;
								}
							}
						}
						if(pawnCounts[x] > 1) {
							malus += DOUBLE_PAWN;
						}
					} else if(p instanceof Pawn && !p.isWhite()){
								if(x!= 0) {
									Piece pp = b.getPieceAt(P(col[x-1]+y));
									malus += freePawn(pawnCounts, true, x, pp);
								}
					}
				}
				//falls in der Spalte h ein weisser Bauer steht
				Piece ph = b.getPieceAt(P(col[7]+y));
				if(ph instanceof Pawn && ph.isWhite()) {
					pawnCounts[7] += 1;
					if(pawnCounts[7] == 1) {
						malus += BACKWARD_PAWN;
					}
					else if(pawnCounts[7] == 0) {
						malus += BACKWARD_PAWN;
					}
				}
			}
			malus += specialCases(pawnCounts);
			pawnCounts = new int[8];
			for(int y = 7; y > 0; y--) {
				//falls in Spalte a ein schwarzer Bauer steht
				Piece pa = b.getPieceAt(P(col[0]+y));
				if(pa instanceof Pawn && !pa.isWhite()) {
					pawnCounts[0] += 1;
					if(pawnCounts[1] == 0) {
						malus -= BACKWARD_PAWN;
					}
				}
				for(int x = 1; x < col.length-1; x++) {
					Piece p = b.getPieceAt(P(col[x]+y));
					if(p instanceof Pawn && !p.isWhite()) {
						pawnCounts[x] += 1;
						
						if(pawnCounts[x-1] == 0 && pawnCounts[x+1] == 0) {
							malus -= BACKWARD_PAWN;
						} else {
							Piece pp = b.getPieceAt(P(col[x-1]+y));
							if(pp instanceof Pawn && !pp.isWhite()) {
								if(pawnCounts[x-1] == 1 && pawnCounts[x+1] == 0) {
									malus -= BACKWARD_PAWN;
								}
							}
						}
						if(pawnCounts[x] > 1) {
							malus -= DOUBLE_PAWN;
						}
					} else if(p instanceof Pawn && p.isWhite()){
							if(x != 0) {
								Piece pp = b.getPieceAt(P(col[x-1]+y));
								malus -= freePawn(pawnCounts, false, x, pp);
							}
					}
				}
				//falls in Spalte h ein schwarzer Bauer steht
				Piece ph = b.getPieceAt(P(col[7]+y));
				if(ph instanceof Pawn && !ph.isWhite()) {
					pawnCounts[7] += 1;
					if(pawnCounts[7] == 1) {
						malus -= BACKWARD_PAWN;
					}
					else if(pawnCounts[7] == 0) {
						malus -= BACKWARD_PAWN;
					}
				}
				
			}
		malus -= specialCases(pawnCounts);
		//System.out.println("Malus Bauern: "+malus);
		return malus;
	}
	
	private int freePawn(int[] pawnCounts, boolean isWhite, int x, Piece pp) {
		int malus = 0;
		if(pawnCounts[x] == 0) {
			if(x != 7 && pawnCounts[x+1]== 0){
				if(x == 0) {
					malus += FREE_PAWN_COM;
				}
				else if(pp instanceof Pawn &&(pp.isWhite() == isWhite)){
					if(pawnCounts[x-1] == 1)
						malus += FREE_PAWN_COM;
				}
				else if(pawnCounts[x-1] == 0){
					malus += FREE_PAWN_COM;
				}
			}
			else if(x == 7){
				if(pp instanceof Pawn &&(pp.isWhite() == isWhite)){
					if(pawnCounts[x-1] == 1)
						malus += FREE_PAWN_COM;
				}
				else if(pawnCounts[x-1] == 0){
					malus += FREE_PAWN_COM;
				}
			}
		}
		return malus;
	}
	
	private int specialCases(int[] pawnCounts) {
		int malus = 0;
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
		return malus;
	}
	
	
	
}
