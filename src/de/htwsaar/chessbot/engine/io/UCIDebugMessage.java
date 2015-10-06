package de.htwsaar.chessbot.engine.io;

public class UCIDebugMessage extends UCIMessage {
	
	private static final String FORMAT = "[DEBUG]%s";

	public UCIDebugMessage(String message) {
		super(message);
	}

	@Override
	public void send() {
		System.out.println(String.format(FORMAT, getMessage()));
	}

}
