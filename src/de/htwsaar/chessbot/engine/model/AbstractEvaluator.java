package de.htwsaar.chessbot.engine.model;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEvaluator {
	
	//Wertigkeiten der Figuren.
	@SuppressWarnings("unchecked")
	public static Map<Class<? extends Piece>,Integer> sPieceValues =
				(Map<Class<? extends Piece>,Integer>) new HashMap();
	
	public static final int KING_SCORE = 1000000;
	public static final int QUEEN_SCORE = 900;
	public static final int PAWN_SCORE = 100;
	public static final int KNIGHT_SCORE = 300;
	public static final int BISHOP_SCORE = 300;
	public static final int ROOK_SCORE = 500;
	
	static {
		sPieceValues.put(King.class, KING_SCORE);
		sPieceValues.put(Queen.class, QUEEN_SCORE);
		sPieceValues.put(Pawn.class, PAWN_SCORE);
		sPieceValues.put(Knight.class, KNIGHT_SCORE);
		sPieceValues.put(Bishop.class, BISHOP_SCORE);
		sPieceValues.put(Rook.class, ROOK_SCORE);
	}
	

    
    public abstract int evaluate(Board b);
     

}

