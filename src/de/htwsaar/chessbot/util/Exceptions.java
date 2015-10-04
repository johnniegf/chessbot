package de.htwsaar.chessbot.util;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Exceptions {
    
    /**
    * Standardkonstruktor.
    */ 
    private Exceptions() {

    }

    public static void checkNull(Object param) {
        checkNull(param, "");
    }

    public static void checkNull(Object param, String paramName) {
        if (param == null)
            throw new NullPointerException(
                msg(EXN_NULL_REF, paramName)
            );
    }
    
    public static void checkInBounds(final int value, 
                                     final int lower,
                                     final int upper)
    {
        checkInBounds(value, "", lower, upper);
    }

    public static void checkInBounds(final int value, 
                                     final String paramName,
                                     final int lower,
                                     final int upper)
    {
        if (value < lower || value > upper)
            throw new IndexOutOfBoundsException(
                msg( EXN_INDEX_OOB, paramName, value, lower, upper )
            );
    }
    
    public static void checkCondition(final boolean condition,
                                      final String reason)
    {
        if (!condition) {
            throw new IllegalArgumentException( 
                msg( EXN_CONDITION, reason )
            );
        }
    }

    public static String msg(final String message, Object... params) {
        return String.format(message, params);   
    }
    
    private static final String EXN_CONDITION =
        "Aufrufparameter verletzt folgende Bedingung: %s";

    private static final String EXN_NULL_REF = 
        "Aufrufparameter %s darf nicht <null> sein!";
    private static final String EXN_INDEX_OOB =
        "Aufrufparameter %s außerhalb der Grenzen. Gefunden %d, erwartet [%d,%d]";

}
