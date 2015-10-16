package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.core.BitBoardUtils;
import static de.htwsaar.chessbot.core.BitBoardUtils.GameStage.ENDGAME;
import static de.htwsaar.chessbot.core.BitBoardUtils.GameStage.MIDGAME;
import static de.htwsaar.chessbot.core.BitBoardUtils.GameStage.OPENING;
import static de.htwsaar.chessbot.core.BitBoardUtils.getGameStage;
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
        DEFAULT.addFunction(new PositionBasedEvaluator(), 70, 50, 60);
        DEFAULT.addFunction(new PawnEvaluator(),          20, 25, 15);
        DEFAULT.addFunction(new RookEvaluator(),          10, 15,  5);
        DEFAULT.addFunction(new MobilityEvaluator(),       0, 10, 20);
    }
    
    private final List<EvaluationFunction> mEvalFunctions;
    private final List<Integer[]> mWeights;
    private int[] weightSums = new int[3]; 
    
    /**
     * Konstruktor der sämtliche Evaluatoren beinhaltet.
     */
    public Evaluator() {
        weightSums = new int[] { 0, 0, 0 };
        mWeights = new ArrayList<>();
        mEvalFunctions = new ArrayList<>();
    }
    
    public void addFunction(final EvaluationFunction func, final int weight) {
        addFunction(func, weight, weight, weight);
    }
    
    public void addFunction(final EvaluationFunction func, 
                            final int openingWeight,
                            final int midgameWeight,
                            final int endgameWeight) 
    {
        checkNull(func);
        mEvalFunctions.add(func);
        mWeights.add(new Integer[] {openingWeight, midgameWeight, endgameWeight});
        weightSums[OPENING.ordinal()] += openingWeight;
        weightSums[MIDGAME.ordinal()] += midgameWeight;
        weightSums[ENDGAME.ordinal()] += endgameWeight;
    }

    /**
     * wertet das Board aus.
     */
    @Override
    public int evaluate(Board b) {
        int sign = b.isWhiteAtMove() ? 1 : -1;
        int score = 0;
        int gameStage = getGameStage(b).ordinal();
        EvaluationFunction func;
        for (int i = 0; i < mEvalFunctions.size(); i++) {
            func = mEvalFunctions.get(i);
            int weight = mWeights.get(i)[gameStage];
            if (func.isAbsolute()) {
                score += sign * weight * func.evaluate(b);
            } else {
                score += weight * func.evaluate(b);
            }
        }
        return score / (weightSums[gameStage] != 0 ? weightSums[gameStage] : 1);
    }
    
    @Override
    public boolean isAbsolute() {
        return false;
    }
}
