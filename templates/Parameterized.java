package de.htwsaar.chessbot.%PKGNAME;

// Interne Referenzen
import de.htwsaar.chessbot.*;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.Matchers.*;
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
public class %CLASSNAME { 

    // Testvariablen
    private ...

    // Kontrollwerte
    private static final ...

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
    public %CLASSNAME {
    
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

    @Test public void test...() {
    
    }

}


