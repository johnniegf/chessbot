package de.htwsaar.chessbot.%PKGNAME;

/**
* Beschreibung.
*
* @author %AUTHOR
*/
public class %CLASSNAME {
    
    /**
    * Standardkonstruktor.
    */ 
    public %CLASSNAME() {

    }

    /**
    * Gib den Hashwert dieses Objekts aus.
    *
    * @return Hashwert dieses Objekts.
    */
    public int hashCode() {
        int hash = 0;
        // Berechnungen

        return hash;
    }

    /**
    * Prüfe das Objekt auf Gleichheit mit einem anderen Objekt.
    *
    * @param other das zu prüfende Objekt.
    * @return <code>true</code> genau dann, wenn die Objekte gleich sind,
    *         sonst <code>false</code>
    */
    public boolean equals(final Object other) {
        // Triviale Fälle
        if (other == null) return false;
        if (other == this) return true;

        if (other instanceof %CLASSNAME) {
            final %CLASSNAME o = (%CLASSNAME) other;
            // Prüfe die Attribute auf Gleichheit
            
            return true;
        } else {
            return false;
        }
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
}
