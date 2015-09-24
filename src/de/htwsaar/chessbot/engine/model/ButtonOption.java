package de.htwsaar.chessbot.engine.model;

/**
 * ButtonOption beschreibt dass es einen Button gibt
 * fuer diese Option
 * @author Dominik Becker
 *
 */
public class ButtonOption extends Option{

	public ButtonOption(String key, Object value) {
		super(key, value);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("type button");
		return sb.toString();
	}

}
