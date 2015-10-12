package de.htwsaar.chessbot.uci;

public class UCIErrorMessage extends UCIMessage {

    private static final String FORMAT = "[ERROR]%s";

    public UCIErrorMessage(String message) {
        super(message);
    }

    @Override
    public void send() {
        System.out.println(String.format(FORMAT, getMessage()));
    }

}
