package de.htwsaar.chessbot.engine.model;

import java.util.ArrayList;

public class RookEvaluator {

	private static final int OPEN_LINE = 15;
	private static final int HALF_OPEN_LINE = 10;
	private static final int ROOK_AT_7 = 20;
	
	private static final long ROOK = 0x7916b7a5c26f1e7bL;
	private static final long PAWN = 0x7916b7a5c26f1e7bL;
	
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
	
	public int evaluate(Board b) {
		return calculate(b);
	}
	
	private int calculate(Board b) {
		int malus = 0;
		boolean open = true;
		for(Piece r : b.getPiecesByType(ROOK)) {
			if(r.getPosition().rank() == 7 && r.isWhite()) {
				malus -= ROOK_AT_7;
			}
			else if(r.getPosition().rank() == 1 && !r.isWhite()) {
				malus += ROOK_AT_7;
			}
			for(Piece p : b.getPiecesByType(PAWN)) {
				if(r.getPosition().file() == p.getPosition().file()) {
					open = false;
					if(r.isWhite() == !p.isWhite()) {
						if(r.isWhite())
							malus -= HALF_OPEN_LINE;
						else malus += HALF_OPEN_LINE;
						System.out.println("half open");
						break;
					}
				}
			}
			if(open) {
				if(r.isWhite())
					malus -= OPEN_LINE;
				else malus += OPEN_LINE;
			}
			open = true;
			System.out.println("Malus nach : "+r.getPosition()+" "+ malus);
		}
		System.out.println("Rook malus: "+ malus);
		return malus;
	}
}
