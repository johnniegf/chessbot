package de.htwsaar.chessbot.engine.model;

/**
* Ausnahme für ...
*
* Ursachen für diese Ausnahnme.
*
* @author Johannes Haupt
*/
public class InvalidDimensions extends RuntimeException {

    private static final String MESSAGE = "Unzulässige Maße des Schachbretts: %dX%d";

    /**
    * Erzeuge eine neue Ausnahme.
    *
    * @param reason Grund für das Auslösen der Ausnahme
    */
    public InvalidDimensions(final byte width, final byte height) {
        super( String.format(MESSAGE, width, height) );
    }
}
