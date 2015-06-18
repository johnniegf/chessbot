package de.htwsaar.chessbot.engine.model;

public class BoardException extends RuntimeException {

    private static final String MESSAGE = "%s";

    /**
    * Erzeuge eine neue Ausnahme.
    */
    public BoardException() {
        this("");
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    */
    public BoardException(final String reason) {
        this(reason, null);
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    * @param cause  zugrunde liegende Ausnahme
    */
    public BoardException(final String reason, final Throwable cause) {
        super(String.format(MESSAGE, reason), cause);
    }
}
