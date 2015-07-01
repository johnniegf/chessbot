package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

import java.util.*;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class FilterMaterialCount implements EvaluationFunction.Filter {
    
    private static Map<String, Long> values;

    static {
        values = new HashMap<String, Long>();
        values.put("P", 100L);
        values.put("B", 300L);
        values.put("N", 300L);
        values.put("R", 500L);
        values.put("Q", 900L);
        values.put("K", 0L);
    }
    /**
    * Standardkonstruktor.
    */ 
    public FilterMaterialCount() {
    }

    public long evaluate(final Board board) {
        long whiteCount = 0, blackCount = 0;
        long value = 0;
        for (Piece p : board.getPieces()) {
            value = values.get(p.toFEN().trim().toUpperCase());
            if (p.isWhite())
                whiteCount += value;
            else
                blackCount += value;
        }
        return whiteCount - blackCount;
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
}
