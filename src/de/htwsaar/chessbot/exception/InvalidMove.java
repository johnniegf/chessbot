package de.htwsaar.chessbot.exception;

public class InvalidMove extends RuntimeException {
    
    private static final String MESSAGE =
        "Dieser Zug kann nicht ausgeführt werden. %s";

    public InvalidMove() {
        this("");
    }

    public InvalidMove(String reason) {
        super(
            String.format(MESSAGE, reason)
        );
    }
}
