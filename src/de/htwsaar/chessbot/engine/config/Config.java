package de.htwsaar.chessbot.engine.config;

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
	
	public static final String PAWN_SCORE = "PawnScore";
	public static final String KING_SCORE = "KingScore";
	public static final String QUEEN_SCORE = "QueenScore";
	public static final String KNIGHT_SCORE = "KnightScore";
	public static final String BISHOP_SCORE = "BishopScore";
	public static final String ROOK_SCORE = "RookScore";
	public static final String THREAD_COUNT = "ThreadCount";
	public static final String PONDER = "Ponder";
	
	private HashMap<String, Option> options;
	
	private Config() {
		this.options = new HashMap<String, Option>();
	}
	
	private static Config sInstance;
	private static boolean initialized = false;
	
	public static Config getInstance() {
		if(sInstance == null) {
			sInstance = new Config();
			sInstance.init();
		}
		return sInstance;
	}
	
	//initialisiert die Standardoptionen
	public void init() {
		if (!initialized) {
			int cores = Runtime.getRuntime().availableProcessors();
			addSpinOption(PAWN_SCORE, 100, 0, 10000000);
			addSpinOption(KING_SCORE ,1000000, 0, 1000000);
			addSpinOption(QUEEN_SCORE, 900, 0, 1000000);
			addSpinOption(KNIGHT_SCORE, 300, 0, 1000000);
			addSpinOption(BISHOP_SCORE, 300, 0, 1000000);
			addSpinOption(ROOK_SCORE, 500, 0, 1000000);
			addSpinOption(THREAD_COUNT, 1, 1, cores);
			addCheckOption(PONDER, true);
			initialized = true;
		}
	}
	
	public void addSpinOption(String key, int value, int min, int max){
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
			sb.append(options.get(s).toString()).append(System.lineSeparator());
		}
		return sb.toString().substring(0, sb.length() - 1);
	}

}
