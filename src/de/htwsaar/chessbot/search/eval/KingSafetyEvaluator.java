package de.htwsaar.chessbot.search.eval;

import de.htwsaar.chessbot.core.Board;

/**
 *
 * @author Johannes Haupt
 */
public class KingSafetyEvaluator extends EvaluationFunction {
    
    public KingSafetyEvaluator() {
        
    }
    
    @Override
    public int evaluate(final Board board) {
        return 0;
    }
    
    @Override
    public boolean isAbsolute() {
        return true;
    }
    
}
