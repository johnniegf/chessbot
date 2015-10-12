package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.core.Board;

/**
 * verbindet alle Evaluatoren. beinhaltet die komplette Bewertungsfunktion.
 *
 * @author Dominik Becker
 *
 */
public class Evaluator extends EvaluationFunction {

    private EvaluationFunction pawnEval;
    private EvaluationFunction rookEval;
    private EvaluationFunction pieceSquareEval;
    private EvaluationFunction mobilityEval;

    /**
     * Konstruktor der sämtliche Evaluatoren beinhaltet.
     */
    public Evaluator() {
        this.pawnEval = new PawnEvaluator();
        this.rookEval = new RookEvaluator();
        this.pieceSquareEval = new PositionBasedEvaluator();
        this.mobilityEval = new MobilityEvaluator();
    }

    /**
     * wertet das Board aus.
     */
    public int evaluate(Board b) {
        int sign = b.isWhiteAtMove() ? 1 : -1;
        return sign * ( 0
                + rookEval.evaluate(b)
                + pawnEval.evaluate(b)
                + pieceSquareEval.evaluate(b)
            ) + mobilityEval.evaluate(b)
        ;
    }
}
