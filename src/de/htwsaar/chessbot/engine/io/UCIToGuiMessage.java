package de.htwsaar.chessbot.engine.io;

public class UCIToGuiMessage extends UCIMessage {

    public UCIToGuiMessage(String message) {
        super(message);
    }

    @Override
    public void send() {
        System.out.println(getMessage());
        System.out.flush();
    }

}
