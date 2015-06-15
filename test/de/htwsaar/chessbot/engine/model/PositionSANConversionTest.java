package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.*;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

/**
* Testklasse für ...
*
* @author
*/
@RunWith(Parameterized.class)
public class PositionSANConversionTest { 

    // Testvariablen
    private Position pos;
    private final String input;
    private final String expected;

    // Kontrollwerte

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            {"a1", "a1"}, {"b7 ", "b7"}, {"k11", "k11"}, 
            {"c9", "c9"}, {"\tz2", "z2"}, {"g3", "g3"}, 
            {"d5", "d5"}, {"  g6", "g6"}, {"\n f8", "f8"}
        });
    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public PositionSANConversionTest(final String input, 
                                     final String expected) 
    {
        this.input    = input;
        this.expected = expected;
    }

/* FIXTURES FÜR DIESEN TESTFALL */

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testSanConversion() {
        pos = P(input);
        assertEquals("Algebraische Notationen stimmen nicht überein",
                     expected,
                     pos.toSAN() );
    
    }

}


