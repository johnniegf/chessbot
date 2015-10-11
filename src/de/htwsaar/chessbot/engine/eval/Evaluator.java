package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;

/**
 * verbindet alle Evaluatoren.
 * beinhaltet die komplette Bewertungsfunktion.
 * @author Dominik Becker
 *
 */
public class Evaluator extends EvaluationFunction {

	EvaluationFunction pawnEval;
	EvaluationFunction rookEval;
	EvaluationFunction materialEval;
	EvaluationFunction pieceSquareEval;
    EvaluationFunction mobilityEval;
    
	
	/**
	 * Konstruktor der sämtliche Evaluatoren beinhaltet.
	 */
	public Evaluator() {
		this.pawnEval = new PawnEvaluator();
		this.rookEval = new RookEvaluator();
		this.materialEval = new MaterialEvaluator();
		this.pieceSquareEval = new PositionBasedEvaluator();
        this.mobilityEval = new MobilityEvaluator();
	}
	
	/**
	 * wertet das Board aus.
	 */
	public int evaluate(Board b) {
        int sign = b.isWhiteAtMove() ? 1 : -1;
		return sign * (
            mobilityEval.evaluate(b) 
          + rookEval.evaluate(b) 
          + pawnEval.evaluate(b) 
          + materialEval.evaluate(b)
        );
	}
}
