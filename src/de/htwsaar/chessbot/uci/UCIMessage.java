package de.htwsaar.chessbot.uci;

public abstract class UCIMessage {

    private String message;

    public UCIMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public abstract void send();

}
