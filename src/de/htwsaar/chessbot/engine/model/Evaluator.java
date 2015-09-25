package de.htwsaar.chessbot.engine.model;

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
	TrappedEvaluator tEval;
	
	/**
	 * Konstruktor der sämtliche Evaluatoren beinhaltet.
	 */
	public Evaluator() {
		this.pEval = new PawnEvaluator();
		this.rEval = new RookEvaluator();
		this.mEval = new MaterialEvaluator();
		this.bEval = new PositionBasedEvaluator();
		this.tEval = new TrappedEvaluator();
	}
	
	/**
	 * wertet das Board aus.
	 */
	public int evaluate(Board b) {
		return pEval.evaluate(b) + rEval.evaluate(b) + bEval.evaluate(b)
				+ tEval.evaluate(b);
	}
}
