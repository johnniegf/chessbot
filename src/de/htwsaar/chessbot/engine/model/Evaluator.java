package de.htwsaar.chessbot.engine.model;
/**
 * Simple Bewertungsfunktion zählt Figurwerte.
 * 
 * @author Dominik Becker
 *
 */
public class Evaluator extends AbstractEvaluator{

	@Override
	public int evaluate(Board b) {
		int materialCount = 0;
		int sign;
		for (Piece piece : b.getPieces()) {
			sign = piece.isWhite() ? 1 : -1;
			materialCount += sign * sPieceValues.get(piece.getClass());
		}
		return materialCount;
	}
	
}


