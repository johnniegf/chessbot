package de.htwsaar.chessbot.engine.model;

/**
* Ausnahme für ...
*
* Ursachen für diese Ausnahnme.
*
* @author
*/
public class NullMoveException extends RuntimeException {

    private static final String MESSAGE = "%s";

    /**
    * Erzeuge eine neue Ausnahme.
    */
    public NullMoveException() {
        this("");
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    */
    public NullMoveException(final String reason) {
        this(reason, null);
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    * @param cause  zugrunde liegende Ausnahme
    */
    public NullMoveException(final String reason, 
                      final Throwable cause) 
    {
        super(String.format(MESSAGE, reason), cause);
    }
}
