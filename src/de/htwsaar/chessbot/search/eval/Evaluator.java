package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.core.Board;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Verbindet mehrerer Bewertungsfunktionen.
 * 
 * Teilfunktionen können mit einem Gewicht eingefügt werden. Die Gesamtbewertung
 * errechnet sich wie folgt:
 * <code> (e1 * w1 + e2 * w2 + ... + en * wn) / (w1 + ... + wn) </code>
 *
 * @author Dominik Becker
 */
public class Evaluator extends EvaluationFunction {

    public static final Evaluator DEFAULT = new Evaluator();
    
    static {
        DEFAULT.addFunction(new PositionBasedEvaluator(), 50);
        DEFAULT.addFunction(new PawnEvaluator(), 20);
        DEFAULT.addFunction(new RookEvaluator(), 20);
        DEFAULT.addFunction(new MobilityEvaluator(), 10);
    }
    
    private List<EvaluationFunction> mEvalFunctions;
    private List<Integer> mWeights;
    private int weightSum;
    
    /**
     * Konstruktor der sämtliche Evaluatoren beinhaltet.
     */
    public Evaluator() {
        weightSum = 0;
        mWeights = new ArrayList<Integer>();
        mEvalFunctions = new ArrayList<EvaluationFunction>();
    }
    
    public void addFunction(final EvaluationFunction func, final int weight) {
        checkNull(func);
        mEvalFunctions.add(func);
        mWeights.add(weight);
        weightSum += weight;
    }

    /**
     * wertet das Board aus.
     */
    @Override
    public int evaluate(Board b) {
        int sign = b.isWhiteAtMove() ? 1 : -1;
        int score = 0;
        EvaluationFunction func;
        for (int i = 0; i < mEvalFunctions.size(); i++) {
            func = mEvalFunctions.get(i);
            int weight = mWeights.get(i);
            if (func.isAbsolute()) {
                score += sign * weight * func.evaluate(b);
            } else {
                score += weight * func.evaluate(b);
            }
        }
        return score / (weightSum != 0 ? weightSum : 1);
    }
    
    @Override
    public boolean isAbsolute() {
        return false;
    }
}
