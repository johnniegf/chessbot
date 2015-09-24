package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.util.Exceptions;
/**
* Bewertungsfunktion für Stellungen.
*
* Eine Bewertungsfunktion kann eine Stellung aufgrund vieler verschiedener
* Kriterien bewerten.
*
* @author Johannes Haupt, Dominik Becker
*/
public abstract class EvaluationFunction {
	private static int[] scores;
	
	static{
		scores = new int[6];
		/*
		scores[0] = (Integer)Config.getInstance().getOption("PawnScore").getValue();
		scores[1] = (Integer)Config.getInstance().getOption("KingScore").getValue();
		scores[2] = (Integer)Config.getInstance().getOption("QueenScore").getValue();
		scores[3] = (Integer)Config.getInstance().getOption("KnightScore").getValue();
		scores[4] = (Integer)Config.getInstance().getOption("BishopScore").getValue();
		scores[5] = (Integer)Config.getInstance().getOption("RookScore").getValue();
		*/
	}
	
	static int getPieceValue(int id) {
		Exceptions.checkInBounds(id, "pieceId", 0, 5);
		switch(id){
		case King.ID:
			return (Integer)Config.getInstance().getOption("KingScore").getValue();
		case Pawn.ID:
			return (Integer)Config.getInstance().getOption("PawnScore").getValue();
		case Queen.ID:
			return (Integer)Config.getInstance().getOption("QueenScore").getValue();
		case Knight.ID:
			return (Integer)Config.getInstance().getOption("KnightScore").getValue();
		case Bishop.ID:
			return (Integer)Config.getInstance().getOption("RookScore").getValue();
		}
		return -1;
	}
	

	/**
	* Bewerte die übergebene Stellung.
	*
	* @param board die zu bewertende Stellung
	* @return die Bewertung der übergebenen Stellung.
 	* @throws NullPointerException falls <code>board == null</code>
	*/
    public abstract int evaluate(Board b);
     

}
