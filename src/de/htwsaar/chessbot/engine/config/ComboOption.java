package de.htwsaar.chessbot.engine.config;

import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Die ComboOption stellt 3 Auswahlmoeglichkeiten zur Verfuegung: Solid, Normal
 * und Risky Normal ist die default Einstellung
 *
 * @author Dominik Becker
 *
 */
public class ComboOption extends Option {

    //enthaelt die Auswahlmoeglichkeiten
    private List<String> combos;
    private String defValue;
    
    public ComboOption(String key, String value, List<String> options) {
        super(key, value);
        defValue = value;
        this.combos = options;
    }

    /**
     * ueberprueft vorher ob der zu aendernde Wert auch ein gueltiger Wert ist
     *
     * @return true wenn erfolgreich, false wenn fehlgeschlagen
     */
    public boolean setValue(Object value) {
        checkNull(value);
        String sval = value.toString();
        if (combos.contains(sval)) {
            super.setValue(sval);
        }
        return false;
    }
    
    @Override
    public String getValue() {
        return super.getValue().toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("type combo default ");
        sb.append(defValue);
        for (String opt : combos) {
            sb.append(" var ").append(opt);
        }

        return sb.toString();
    }

}
