package de.htwsaar.chessbot.exception;

/**
*   Brettfehler
*
*   @author Timo Klein
*
*/
public class BoardException 
     extends java.lang.RuntimeException
{

    private static final String EXN_MESSAGE = "Brettfehler";

    public BoardException() {
        super(EXN_MESSAGE);
    }

    public BoardException(String exn_msg) {
        super(exn_msg);
    }
}
