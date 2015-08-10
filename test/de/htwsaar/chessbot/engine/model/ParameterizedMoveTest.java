package de.htwsaar.chessbot.engine.model;

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
* @author Henning Walte
*/
@RunWith(Parameterized.class)
public class ParameterizedMoveTest { 

    // Testvariablen
	private static FideBoardBuilder builder = new FideBoardBuilder();
	private Board start, expected;
	private Move mv;
	private boolean isPossible;
	
    private ...

    // Kontrollwerte
    private static final ...

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            // Ausgangsstellung, Zug, ist möglich, Ergebnis
        	{
            	"8/8/8/8/8/8/4P3/8 w - - 0 1", new Move(P("e2"), P("e3")), true, "8/8/8/8/8/4P3/8/8 b - - 0 1"            	
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
    public ParameterizedMoveTest(String startPos, Move mv, boolean isPossible, String expected) {
    	this.start = builder.fromFenString(startPos);
    	this.expected = builder.fromFenString(expected);
    	this.mv = mv;
    	this.isPossible = isPossible;
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

    @Test public void test1() {
    	
    	if( ! this.isPossible ) return; 
    	Board result = mv.execute(start);
    	
    	assertEquals ( "",
    			result, 
    			this.expected);
    	
    }

}


