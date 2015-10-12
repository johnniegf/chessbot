package de.htwsaar.chessbot.config;

/**
 * SpinOption stellt eine Reichweite an,
 * in der der Wert der Option festgelegt werden kann.
 * Dabei gibt es einen Default Wert.
 * @author Dominik Becker
 *
 */
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
	
	/**
	 * checkt ob sich der festgelegte Wert in der Reichweite befindet.
	 * @return true wenn in der Reichweite, false wenn nicht
	 */
	public boolean setValue(Object value){
		if((Integer)value >= min && (Integer)value <= max){
			super.setValue(value);
			return true;
		}
		else return false;
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
