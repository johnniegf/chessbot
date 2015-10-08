package de.htwsaar.chessbot.engine.io;

import java.util.LinkedList;

/**
 * Kontrolliert die Kommunikation zwischen Engine und GUI.
 * 
 * @author David Holzapfel
 *
 */
public class UCISender extends Thread {

	private static UCISender INSTANCE;

	private boolean showDebug = true;
	private boolean showError = true;

	private volatile LinkedList<UCIMessage> messageQueue;


	public static UCISender getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new UCISender();
			INSTANCE.setName("UCISender");
			INSTANCE.setPriority(7);
		}
		return INSTANCE;
	}

	private UCISender() {
		this.messageQueue = new LinkedList<UCIMessage>();
	}

	@Override
	public void run() {
		while(true) {
			UCIMessage message = pollMessage();
			if(message != null) {
				message.send();
			}
			try {
				if(messageQueue.isEmpty()) {
					Thread.sleep(5);
				}
				else {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void queueMessage(UCIMessage message) {
		this.messageQueue.addLast(message);
	}

	private synchronized UCIMessage pollMessage() {
		return this.messageQueue.pollFirst();
	}

	public void sendToGUI(String command) {
		Logger.getInstance().log(command, Logger.ENGINE_TO_GUI);

		queueMessage(new UCIToGuiMessage(command));
	}

	public void sendDebug(String msg) {
		Logger.getInstance().log(msg, Logger.DEBUG);

		if(!this.showDebug) return;
		queueMessage(new UCIDebugMessage(msg));
	}

	public void sendError(String msg) {
		//Logger.getInstance().log(msg, Logger.ERROR);

		if(!this.showError) return;
		queueMessage(new UCIErrorMessage(msg));
	}

	public void setShowDebug(boolean showDebug) {
		this.showDebug = showDebug;
	}

	public void setShowError(boolean showError) {
		this.showError = showError;
	}

}
