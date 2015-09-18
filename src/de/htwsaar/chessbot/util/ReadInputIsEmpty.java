package de.htwsaar.chessbot.util;

public class ReadInputIsEmpty
     extends java.lang.IllegalStateException
{
    private static final String message =
        "Die gelesene Eingabezeile ist leer!";

    public ReadInputIsEmpty() {
        super(message);
    }
}
