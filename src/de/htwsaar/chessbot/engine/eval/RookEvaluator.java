package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import java.util.ArrayList;

/**
 * Bewertung der Türme auf dem Schachbrett:
 * offene Linie, halb offene Linie und Turm auf 7/2
 * @author Dominik Becker
 *
 */
public class RookEvaluator {

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
	
	public int evaluate(Board b) {
		return calculate(b);
	}
	
	/**
	 * berechnet den Malus bzw den Bonus der Türme
	 * @param Board b
	 * @return malus 
	 */
	private int calculate(Board b) {
		int malus = 0;
		boolean open = true;
		boolean halfOpen = false;
		for(Piece r : b.getPieces(Rook.ID)) {
			if(r.getPosition().rank() == 7 && r.isWhite()) {
				malus -= ROOK_AT_7;
			}
			else if(r.getPosition().rank() == 2 && !r.isWhite()) {
				malus += ROOK_AT_7;
			}
			for(Piece p : b.getPieces(Pawn.ID)) {
				if(r.getPosition().file() == p.getPosition().file()) {
					open = false;
					if(r.isWhite() == !p.isWhite()) {
						halfOpen = true;
					}
					else {
						halfOpen = false;
						break;
					}
				}
			}
			if(open) {
				if(r.isWhite())
					malus -= OPEN_LINE;
				else malus += OPEN_LINE;
			}
			if(halfOpen)
				if(r.isWhite())
					malus -= HALF_OPEN_LINE;
				else malus += HALF_OPEN_LINE;
			
			open = true;
			halfOpen = false;
		}
		System.out.println("Malus Türme: "+malus);
		return malus;
	}
}
