package de.htwsaar.chessbot.engine.config;

/**
 * ButtonOption beschreibt dass es einen Button gibt
 * fuer diese Option
 * @author Dominik Becker
 *
 */
public class ButtonOption extends Option {

	public ButtonOption(String key, Object value) {
		super(key, null);
	}
    
    public Object getValue() {
        return null;
    }
    
    
    
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("type button");
		return sb.toString();
	}

}
