package de.htwsaar.chessbot.engine.model.move;

/**
* Ausnahme für ...
*
* Ursachen für diese Ausnahnme.
*
* @author Johannes Haupt
*/
public class MoveException extends RuntimeException {

    private static final String MESSAGE = "%s";

    /**
    * Erzeuge eine neue Ausnahme.
    */
    public MoveException() {
        this("");
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    */
    public MoveException(final String reason) {
        this(reason, null);
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    * @param cause  zugrunde liegende Ausnahme
    */
    public MoveException(final String reason, 
                      final Throwable cause) 
    {
        super(String.format(MESSAGE, reason), cause);
    }
}
