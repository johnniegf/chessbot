package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import de.htwsaar.chessbot.engine.model.Position.*;
import de.htwsaar.chessbot.engine.model.variant.fide.*;

// Java-API
import java.util.*;

// JUnit-API
//import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;

/**
* Testklasse für ...
*
* @author
*/
public class MoveTest { 

    private static ChessVariant VARIANT;

    @BeforeClass
    public static void setUpClass() {
        VARIANT = FideChess.getInstance();
    }

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
    // = Funktionstests
    // ====================================================

    @Ignore
    @Test public void testNothing() {
    
    }

}


