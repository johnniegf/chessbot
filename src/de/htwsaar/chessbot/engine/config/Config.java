package de.htwsaar.chessbot.engine.config;

import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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
	private static boolean initialized = false;
	
	public static Config getInstance() {
		if(sInstance == null) {
			sInstance = new Config();
			sInstance.init();
            initialized = true;
		}
		return sInstance;
	}
	
	//initialisiert die Standardoptionen
	private void init() {
		if (!initialized) {
			addCheckOption("Ponder", true);
			initialized = true;
		}
	}
	
	public void addSpinOption(String key, int defValue, int min, int max){
		options.put(key, new SpinOption(key, defValue, min, max));
	}
	
	public void addCheckOption(String key, boolean defValue) {
		options.put(key, new CheckOption(key, defValue));
	}
	
	public void addComboOption(String key, String defValue, List<String> cboOptions) {
		options.put(key, new ComboOption(key, defValue, cboOptions));
	}
    
    public void addOption(final Option option) {
        checkNull(option);
        if (!options.containsKey(option.getKey()))
            options.put(option.getKey(), option);
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
		return sb.toString().substring(0, sb.length() - 1);
	}

}
