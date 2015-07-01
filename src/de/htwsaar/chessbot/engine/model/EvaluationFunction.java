package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class EvaluationFunction {

    private BoardChecker checker;
    private List<Filter> filters;

    /**
    * Standardkonstruktor.
    */ 
    public EvaluationFunction(final BoardChecker checker, 
                              final List<Filter> filters) 
    {
        this.checker = checker;
        this.filters = filters;
    }

    public long evaluate(final Board board) {
        if (board == null)
            throw new NullPointerException();
        if (!checker.isOk(board))
            throw new RuleViolation();

        long result = 0;
        for (Filter f : filters) {
            result += f.evaluate(board);
        }
        return result;
    }

    public static interface Filter {
        
        long evaluate(final Board board);

    }

}
