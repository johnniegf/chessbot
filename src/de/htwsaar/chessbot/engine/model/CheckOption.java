package de.htwsaar.chessbot.engine.model;

public class CheckOption extends Option{

	private boolean check;
	
	public CheckOption(String key, Object value) {
		super(key, value);
		this.check = check;
	}

	
	public boolean isCheck() {
		return check;
	}
	
	public String toSring() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("type check default ");
		sb.append(Config.getInstance().getOption(getKey()).getValue());
		return sb.toString();
	}
	
}
