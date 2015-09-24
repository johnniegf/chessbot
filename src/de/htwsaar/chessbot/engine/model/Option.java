package de.htwsaar.chessbot.engine.model;

public abstract class Option {
	
	private String key;
	private Object value;

	public Option(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("option name ").append(key).append(" ");
		return sb.toString();
	}
}
