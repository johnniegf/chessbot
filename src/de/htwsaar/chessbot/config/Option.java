package de.htwsaar.chessbot.config;

/**
 * Abstrakte Klasse die eine Option beschreibt, die der GUI
 * zu Beginn mitgeteilt wird.
 * @author Dominik Becker
 *
 */
public abstract class Option {
	
	private String key;
	private Object value;
    private final Object defaultValue;

	//Konstruktor mit Key und Wert der Option
	public Option(String key, Object value) {
		this.key = key;
		this.value = value;
        this.defaultValue = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
	public boolean setValue(Object value) {
		this.value = value;
		return true;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("option name ").append(key).append(" ");
		return sb.toString();
	}
}
