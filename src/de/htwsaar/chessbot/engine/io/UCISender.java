package de.htwsaar.chessbot.engine.io;

public class UCISender {

	private static UCISender INSTANCE;
	
	private boolean showDebug = true;
	private boolean showError = true;
	
	
	public static UCISender getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new UCISender();
		}
		return INSTANCE;
	}
	
	private UCISender() { }

	public void sendToGUI(String command) {
		Logger.getInstance().log(command, Logger.ENGINE_TO_GUI);
		System.out.println(command);
	}
	
	public void sendDebug(String msg) {
		Logger.getInstance().log(msg, Logger.DEBUG);
		
		if(!this.showDebug) return;
		System.out.println("[DEBUG]" + msg);
	}
	
	public void sendError(String msg) {
//		Logger.getInstance().log(msg, Logger.ERROR);
		
		if(!this.showError) return;
		System.out.println("[ERROR]" + msg);
	}

	public void setShowDebug(boolean showDebug) {
		this.showDebug = showDebug;
	}

	public void setShowError(boolean showError) {
		this.showError = showError;
	}

}
