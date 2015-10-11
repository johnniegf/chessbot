package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.COLORS;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.signOf;
import de.htwsaar.chessbot.engine.model.piece.Bishop;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.util.Bitwise;

/**
 * Simple Bewertungsfunktion zählt Figurwerte.
 * 
 * @author Dominik Becker
 *
 */
public class MaterialEvaluator extends EvaluationFunction{

    private final int TWO_BISHOPS = 50;
    
	@Override
	public int evaluate(Board b) {
		int materialCount = 0;
		int sign;
        long whitePieces = b.getPieceBitsForColor(true);
        for (int pieceType = 0; pieceType < 6; pieceType++) {
            long pieces = b.getPieceBitsForType(pieceType);
            int val = getPieceValue(pieceType);
            int count = Bitwise.count(pieces & whitePieces);
            materialCount += val * count;
            if (pieceType == Bishop.ID) {
                if (count >= 2)
                    materialCount += TWO_BISHOPS;
            }
            count = Bitwise.count(pieces & ~whitePieces);
            materialCount -= val * count;
            if (pieceType == Bishop.ID) {
                if (count >= 2)
                    materialCount -= TWO_BISHOPS;
            }
		}
		return materialCount;
	}
	
}