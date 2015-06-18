package de.htwsaar.chessbot.engine.model.variant.fide;

// Interne Referenzen
import de.htwsaar.chessbot.*;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assume.*;
import static org.junit.Assert.*;
import org.junit.*;

// Klassen für parametrierte Tests
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

/**
* Testklasse für ...
*
* @author
*/
@RunWith(Parameterized.class)
public class FidePieceFactoryTest { 

    // Testvariablen

    // Kontrollwerte

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            {

            },
            {

            }
        });

    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public FidePieceFactoryTest() {
    
    }

/* FIXTURES FÜR DIESEN TESTFALL */

    // ====================================================
    // = Ausnahmetests
    // ====================================================

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Ignore
    @Test public void testNothing() {
    
    }

}


