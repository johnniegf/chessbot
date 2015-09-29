package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.piece.Bishop;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.piece.King;
import de.htwsaar.chessbot.engine.model.piece.Knight;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Queen;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import java.util.HashMap;
import java.util.Map;
/**
* Bewertungsfunktion für Stellungen.
*
* Eine Bewertungsfunktion kann eine Stellung aufgrund vieler verschiedener
* Kriterien bewerten.
*
* @author Johannes Haupt, Dominik Becker
*/
public abstract class EvaluationFunction {
	
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
	

	/**
	* Bewerte die übergebene Stellung.
	*
	* @param board die zu bewertende Stellung
	* @return die Bewertung der übergebenen Stellung.
 	* @throws NullPointerException falls <code>board == null</code>
	*/
    public abstract int evaluate(Board b);
     

}
