package de.htwsaar.engine.%PKGNAME;

public class %CLASSNAME extends RuntimeException {

    private static final String MESSAGE = "%s";

    /**
    * Erzeuge eine neue Ausnahme.
    */
    public %CLASSNAME() {
        this("");
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    */
    public %CLASSNAME(final String reason) {
        this(reason, null);
    }

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    * @param cause  zugrunde liegende Ausnahme
    */
    public %CLASSNAME(final String reason, final Throwable cause) {
        super(String.format(MESSAGE, reason), cause);
    }
}
