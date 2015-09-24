package de.htwsaar.chessbot.engine.model;

public class SpinOption extends Option{

	private int min;
	private int max;
	
	public SpinOption(String key, Object value, int min, int max) {
		super(key, value);
		this.min = min;
		this.max = max;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("type spin default ");
		sb.append(Config.getInstance().getOption(getKey()).getValue());
		sb.append(" min ").append(min);
		sb.append(" max ").append(max);
		return sb.toString();
	}
}
