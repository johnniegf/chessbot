package de.htwsaar.chessbot.util;

public class CantConvertInputToGivenType
     extends java.lang.RuntimeException
{
    private static final String message =
        "Konnte die Eingabe '%s' nicht zum typ <%s> umwandeln!";

    public CantConvertInputToGivenType(String input, String type) {
        this(input, type, null);
    }

    public CantConvertInputToGivenType(String    input,
                                       String    type,
                                       Throwable reason)
    {
        super(
              String.format(message, input, type),
              reason
              );
    }
}
