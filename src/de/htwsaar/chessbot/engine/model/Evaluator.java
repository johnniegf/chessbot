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
	
	/**
	 * Konstruktor der sämtliche Evaluatoren beinhaltet.
	 */
	public Evaluator() {
		this.pEval = new PawnEvaluator();
		this.rEval = new RookEvaluator();
		this.mEval = new MaterialEvaluator();
	}
	
	/**
	 * wertet das Board aus.
	 */
	public int evaluate(Board b) {
		return mEval.evaluate(b) - pEval.evaluate(b) - rEval.evaluate(b);
	}
}