package de.htwsaar.chessbot.config;

public class CheckOption extends Option{

	
	public CheckOption(String key, Object value) {
		super(key, value);
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("type check default ");
		sb.append(Config.getInstance().getOption(getKey()).getValue());
		return sb.toString();
	}
	
}
