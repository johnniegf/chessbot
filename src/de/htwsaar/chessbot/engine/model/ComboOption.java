package de.htwsaar.chessbot.engine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Die ComboOption stellt 3 Auswahlmoeglichkeiten zur Verfuegung:
 * Solid, Normal und Risky
 * Normal ist die default Einstellung
 * @author Dominik Becker
 *
 */
public class ComboOption extends Option{
	
	//enthaelt die Auswahlmoeglichkeiten
	private List<String> combos;

	public ComboOption(String key, Object value) {
		super(key, value);
		this.combos = new ArrayList<String>();
		combos.add("Solid");
		combos.add("Normal");
		combos.add("Risky");
	}
	
	/**
	 * ueberprueft vorher ob der zu aendernde Wert 
	 * auch ein gueltiger Wert ist
	 * @return true wenn erfolgreich, false wenn fehlgeschlagen
	 */
	public boolean setValue(Object value) {
		for(String s : combos){
			if(value instanceof String && s.equals((String)value)){
				super.setValue(value);
				return true;
			}
		}
		return false;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("type combo default ");
		sb.append(Config.getInstance().getOption(getKey()).getValue());
		sb.append(" var ").append(combos.get(0));
		sb.append(" var ").append(combos.get(1));
		sb.append(" var ").append(combos.get(2));
		
		return sb.toString();
	}

}
