package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.P;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;

/**
* Testklasse für ...
*
* @author Johannes Haupt
*/
public class PositionTest { 

    // Testvariablen

    // Kontrollwerte
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
    
    }

    /**
    * Testabbau.
    *
    * Gibt Ressourcen und Testvariablen frei.
    */
    @After public void cleanUp() {

    }

/* FIXTURES FÜR DIESEN TESTFALL */

    // ====================================================
    // = Ausnahmetests
    // ====================================================

    @Test(expected = MyException.class)
    public void test...() {
        // Fehlerhafte Anweisung, die MyException auslöst
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testInvalidSanString() {
        final String[] invalidPositions = {
            "", "55", " b 7 ", "c88", "3d", "? ", "c8 )", "ab7"
        };
        for (String san : invalidPositions) {
            assertFalse("SAN-String '" + san + "' darf nicht gültig sein.",
                        P(san).isValid());
        }
    }

    @Test public void testInvalidIndices() {
        final byte[] invalidFiles = { 0, -4, 100, 27 };
        final byte[] invalidRanks = { 0, -4, 100, 41 };
        for (byte f : invalidFiles) {
            for (byte r : invalidRanks) {
                assertFalse("Position " + f + "," + r + 
                            " darf nicht gültig sein.",
                            P(f,r).isValid());
                
            }
        }
    }

}


