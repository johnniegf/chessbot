package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.P;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.*;

// Java-API
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
// Klassen für parametrierte Tests
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.htwsaar.chessbot.engine.model.variant.fide.FideBoardBuilder;

/**
* Testklasse für ...
*
* @author Henning Walte, Dominik Becker
*/
//TODO: Henning: Dame,Springer.Bauer ;Dominik: Turm,L�ufer,K�nig;Sonstiges: Rochade,En passant, Bauernumwandlung
@RunWith(Parameterized.class)
public class ParameterizedMoveTest { 

    // Testvariablen
	private static FideBoardBuilder builder = new FideBoardBuilder();
	private Board start, expected;
	private Move mv;
	private boolean isPossible;
	

    // Kontrollwerte
   // private static final ...

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            // Ausgangsstellung, Zug, ist möglich, Ergebnis
        	{
            	"8/8/8/8/8/8/4P3/8 w - - 0 1", new Move(P("e2"), P("e3")), true, "8/8/8/8/8/4P3/8/8 b - - 0 1"            	
            },
            {
            	"7p/8/8/8/8/8/8/7R w - - 0 1", new Move(P("h1"), P("h8")), true, "7R/8/8/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/7R/8/8/8 w - - 0 1", new Move(P("h4"), P("i4")), false,"8/8/8/8/8/8/8/8 b - - 0 1"
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

    @Test(expected = MoveException.class)
    public void testIllegalMove() {
        assumeFalse(this.isPossible);
        mv.execute(start);
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testExecute() {
    	
    	assumeTrue(this.isPossible);
    	Board result = mv.execute(start);
    	
    	assertEquals ( "",
    			result, 
    			this.expected);
    	
    }

}


