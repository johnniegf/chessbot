package de.htwsaar.chessbot.engine.model;

import java.util.HashMap;

/**
 * Config Klasse zur Verwaltung von Optionen die die Engine
 * der GUI zur Verfuegung stellt.
 * es gibt festgelegte Optionen, es kann aber auch
 * Optionen hinzugefuegt werden.
 * @author Dominik Becker
 *
 */
public class Config {
	
	private HashMap<String, Option> options;
	
	private Config() {
		this.options = new HashMap<String, Option>();
	}
	
	private static Config sInstance;
	
	public static Config getInstance() {
		if(sInstance == null)
			sInstance = new Config();
		return sInstance;
	}
	
	//initialisiert die Standardoptionen
	public void init() {
		
		addSpinOption("PawnScore", 100, 0, 10000000);
		addSpinOption("KingScore" ,1000000, 0, 1000000);
		addSpinOption("QueenScore", 900, 0, 1000000);
		addSpinOption("KnightScore", 300, 0, 1000000);
		addSpinOption("BishopScore", 300, 0, 1000000);
		addSpinOption("RookScore", 500, 0, 1000000);
		addCheckOption("Ponder", true);
	}
	
	public void addSpinOption(String key, Object value, int min, int max){
		options.put(key, new SpinOption(key, value, min, max));
	}
	
	public void addCheckOption(String key, Object value) {
		options.put(key, new CheckOption(key, value));
	}
	
	public void addButtonOption(String key, Object value) {
		options.put(key, new ButtonOption(key, value));
	}
	
	public void addComboOption(String key, Object value) {
		options.put(key, new ComboOption(key, value));
	}
	
	public void setOption(String key, Object value) {
		options.get(key).setValue(value);
	}
	
	public Option getOption(String key) {
		return options.get(key);
	}
	
	public boolean containsKey(String key){
		return options.containsKey(key);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(String s : options.keySet()) {
			sb.append(options.get(s).toString()).append("\n");
		}
		return sb.toString();
	}

}
