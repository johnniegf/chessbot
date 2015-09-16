package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.*;

public class PawnEvaluator extends MaterialEvaluator{

	private static final int DOUBLE_PAWN = 10;
	private static final int ISOLATED_PAWN = 20;
	private static final int BACKWARD_PAWN = 8;
	private static final int FREE_PAWN_COM = 20;
	private static final String COL = "abcdefgh";
	private String[] col;
	
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
		return super.evaluate(b) - calculate(b) - new RookEvaluator().evaluate(b);
	}
	
	private int calculate(Board b) {
		int malus = 0;
		int[] pawnCounts = new int[8];
			for(int y = 1; y < 9; y++) {
				for(int x = 0; x < col.length; x++) {
					Piece p = b.getPieceAt(P(col[x]+y));
					if(p instanceof Pawn && p.isWhite()) {
						pawnCounts[x] += 1;
						if(x == 0) {
							if(pawnCounts[x+1] == 0) {
								malus += BACKWARD_PAWN;
								System.out.println("back"+ malus);
							}
						}
						else if(x == 7) {
							Piece pp = b.getPieceAt(P(col[x-1]+y));
							if(pp instanceof Pawn && pp.isWhite()) {
								if(pawnCounts[x-1] == 1) {
									malus += BACKWARD_PAWN;
									System.out.println("back"+ malus);
								}
							}
							else if(pawnCounts[x-1] == 0) {
								malus += BACKWARD_PAWN;
								System.out.println("back"+ malus);
							}
						}
						else if(pawnCounts[x-1] == 0 && pawnCounts[x+1] == 0) {
							malus += BACKWARD_PAWN;
							System.out.println("back"+ malus);
						} else {
							Piece pp = b.getPieceAt(P(col[x-1]+y));
							if(pp instanceof Pawn && pp.isWhite()) {
								if(pawnCounts[x-1] == 1 && pawnCounts[x+1] == 0) {
									malus += BACKWARD_PAWN;
									System.out.println("back"+ malus);
								}
							}
						}
						if(pawnCounts[x] > 1) {
							malus += DOUBLE_PAWN;
							System.out.println("double"+ malus);
						}
					} else if(p instanceof Pawn && !p.isWhite()){
								if(x!= 0) {
									Piece pp = b.getPieceAt(P(col[x-1]+y));
									malus += freePawn(pawnCounts, true, x, pp);
									System.out.println("freeBlack"+ malus);
								}
					}
				}
			}
			malus += specialCases(pawnCounts);
			System.out.println("nach weiß"+malus);
			pawnCounts = new int[8];
			for(int y = 8; y > 0; y--) {
				for(int x = 0; x < col.length; x++) {
					Piece p = b.getPieceAt(P(col[x]+y));
					if(p instanceof Pawn && !p.isWhite()) {
						pawnCounts[x] += 1;
						if(x == 0) {
							if(pawnCounts[x+1] == 0) {
								malus -= BACKWARD_PAWN;
								System.out.println("back"+ malus);
							}
						}
						else if(x == 7) {
							Piece pp = b.getPieceAt(P(col[x-1]+y));
							if(pp instanceof Pawn && !pp.isWhite()) {
								if(pawnCounts[x-1] == 1) {
									malus -= BACKWARD_PAWN;
									System.out.println("back"+ malus);
								}
							}
							else if(pawnCounts[x-1] == 0) {
								malus -= BACKWARD_PAWN;
								System.out.println("back"+ malus);
							}
						}
						else if(pawnCounts[x-1] == 0 && pawnCounts[x+1] == 0) {
							malus -= BACKWARD_PAWN;
							System.out.println("back"+malus);
						} else {
							Piece pp = b.getPieceAt(P(col[x-1]+y));
							if(pp instanceof Pawn && !pp.isWhite()) {
								if(pawnCounts[x-1] == 1 && pawnCounts[x+1] == 0) {
									malus -= BACKWARD_PAWN;
									System.out.println("back"+ malus);
								}
							}
						}
						if(pawnCounts[x] > 1) {
							malus -= DOUBLE_PAWN;
							System.out.println("double"+ malus);
						}
					} else if(p instanceof Pawn && p.isWhite()){
							if(x != 0) {
								Piece pp = b.getPieceAt(P(col[x-1]+y));
								malus -= freePawn(pawnCounts, false, x, pp);
								System.out.println("freeWhite"+ malus);
							}
					}
				}
			}
			
		
		malus -= specialCases(pawnCounts);
		System.out.println("nach schwarz"+ malus);
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
