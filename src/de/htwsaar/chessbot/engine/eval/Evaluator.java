package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.model.Board;

/**
 * verbindet alle Evaluatoren.
 * beinhaltet die komplette Bewertungsfunktion.
 * @author Dominik Becker
 *
 */
public class Evaluator extends EvaluationFunction {

	PawnEvaluator pEval;
	RookEvaluator rEval;
	MaterialEvaluator mEval;
	PositionBasedEvaluator bEval;
	
	/**
	 * Konstruktor der sämtliche Evaluatoren beinhaltet.
	 */
	public Evaluator() {
		this.pEval = new PawnEvaluator();
		this.rEval = new RookEvaluator();
		this.mEval = new MaterialEvaluator();
		this.bEval = new PositionBasedEvaluator();
	}
	
	/**
	 * wertet das Board aus.
	 */
	public int evaluate(Board b) {
        int sign = 1; //(b.isWhiteAtMove() ? 1 : -1);
		return sign * (
            rEval.evaluate(b) 
          + pEval.evaluate(b) 
          + pEval.evaluate(b)
        );
	}
}
