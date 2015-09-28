package de.htwsaar.chessbot.engine.model;

import java.util.Collection;

public class MobilityEvaluator extends EvaluationFunction{

	@Override
	public int evaluate(Board b) {
		return -(calculateBishopMobility(b)
				+ calculateKnightMobility(b)+ calculateRookMobility(b));
	}
	
	private int calculateBishopMobility(Board b) {
		int malus = 0;
		int count = 0;
		
		for(Piece bh : b.getPiecesByType(Bishop.ID)) {
			count = 0;
			Collection<Move> moves = bh.getMoves(b);
			count = moves.size();
			if(bh.isWhite()) {
				malus -= Math.round(count * 11.5);
			}
			else { malus += Math.round(count * 11.5);}
		}
		return malus;
	}
	
	private int calculateKnightMobility(Board b) {
		int malus = 0;
		int count = 0;
		
		for(Piece k : b.getPiecesByType(Knight.ID)) {
			count = 0;
			Collection<Move> moves = k.getMoves(b);
			count = moves.size();
			if(k.isWhite()) {
				malus -= count * 19;
			}
			else { malus += count * 19;}
		}
		
		return malus;
	}
	
	private int calculateRookMobility(Board b) {
		int malus = 0;
		int count = 0;
		
		for(Piece r : b.getPiecesByType(Rook.ID)) {
			count = 0;
			Collection<Move> moves = r.getMoves(b);
			count =  moves.size();
			if(r.isWhite()) {
				malus -= count * 15;
			}
			else { malus += count * 15;}
		}
		
		return malus;
	}

}
