package de.htwsaar.chessbot.engine.model;

/**
* Ausnahme für ungültige Züge.
*
* Eine InvalidMoveException wird ausgelöst, wenn
* <ul>
    <li>ein Nullzug übergeben wurde, wo keiner erwartet/erlaubt ist</li>
    <li>ein Zug in einer Stellung ausgeführt werden soll, in der er
        nicht möglich ist.</li>
* </ul>
*
* @author scripts/newFile.sh
*/
public class InvalidMoveException extends RuntimeException {

    private static final String MESSAGE = "%s";

    /**
    * Erzeuge eine neue Ausnahme.
    */
    public InvalidMoveException() {
        this("");
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    */
    public InvalidMoveException(final String reason) {
        this(reason, null);
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    * @param cause  zugrunde liegende Ausnahme
    */
    public InvalidMoveException(final String reason, 
                      final Throwable cause) 
    {
        super(String.format(MESSAGE, reason), cause);
    }
}
