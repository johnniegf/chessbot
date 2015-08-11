package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.P;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.*;

// Java-API
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
// Klassen f√ºr parametrierte Tests
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.htwsaar.chessbot.engine.model.variant.fide.FideBoardBuilder;

/**
* Testklasse f√ºr ...
*
* @author Henning Walte, Dominik Becker
*/
//TODO: Henning: Dame,Springer.Bauer ;Dominik: Turm,L‰ufer,Kˆnig;Sonstiges: Rochade,En passant, Bauernumwandlung
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
            // Ausgangsstellung, Zug, ist m√∂glich, Ergebnis
        	{
            	"8/8/8/8/8/8/4P3/8 w - - 0 1", new Move(P("e2"), P("e3")), true, "8/8/8/8/8/4P3/8/8 b - - 0 1"            	
            },
        	//Rook
        	//true
            {
            	"7p/8/8/8/8/8/8/7R w - - 0 1", new Move(P("h1"), P("h8")), true, "7R/8/8/8/8/8/8/8 b - - 0 1"
            },
            //{
            	//"8/8/8/8/8/8/8/3q3R w - - 0 1", new Move(P("h1"), P("d1")), true, "8/8/8/8/8/8/8/3R4 b - - 0 1"
            //},
            {
            	"8/8/8/4b3/8/8/8/4R3 w - - 0 1", new Move(P("e1"), P("e5")), true, "8/8/8/4R3/8/8/8/8 b - - 0 1"
            },
            {
        	   	"r3P3/8/8/8/8/8/8/8 b - - 0 1", new Move(P("a8"), P("e8")), true, "4r3/8/8/8/8/8/8/8 w - - 0 2"
            },
            //false
            {
        	   	"8/8/8/8/7R/8/8/8 w - - 0 1", new Move(P("h4"), P("i4")), false, "8/8/8/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/8/8/8/6NR w - - 0 1", new Move(P("h1"), P("g1")), false, "8/8/8/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/8/8/7P/7R w - - 0 1", new Move(P("h1"), P("h2")), false, "8/8/8/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/8/8/8/3q2PR w - - 0 1", new Move(P("h1"), P("d1")), false, "8/8/8/8/8/8/8/8 b - - 0 1"
            },
            //King
            //true
            {
            	"8/8/8/8/8/8/8/4K3 w - - 0 1", new Move(P("e1"), P("e2")), true, "8/8/8/8/8/8/4K3/8 b - - 0 1"//0 zu 1 ‰ndern
            },
            {
            	"8/8/8/8/8/8/5p2/4K3 w - - 0 1", new Move(P("e1"), P("f2")), true, "8/8/8/8/8/8/5K2/8 b - - 0 1"
            },
            //false
            {
            	"8/8/8/8/8/8/7r/4K3 w - - 0 1", new Move(P("e1"), P("e2")), false, ""
            },
           //Bishop
           //true
            {
            	"8/8/8/8/8/8/8/5B2 w - - 0 1", new Move(P("f1"), P("d3")), true, "8/8/8/8/8/3B4/8/8 b - - 0 1"//0 zu 1 ‰ndern
            },
            {
            	"8/8/8/8/8/3q4/8/5B2 w - - 0 1", new Move(P("f1"), P("d3")), true, "8/8/8/8/8/3R4/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/8/8/8/B7 w - - 0 1", new Move(P("a1"), P("h8")), true, "7B/8/8/8/8/8/8/8 b - - 0 1"
            },            {
        	  	"8/8/8/8/3r4/2B5/8/8 w - - 0 1", new Move(P("c3"), P("d4")), true, "8/8/8/8/3B4/8/8/8 b - - 0 1"
            },
           //false
            {
            	"8/8/8/8/8/5B2/8/8 w - - 0 1", new Move(P("f1"), P("f2")), false, "8/8/8/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/8/2R5/8/B7 w - - 0 1", new Move(P("a1"), P("d4")), false, "8/8/8/8/8/8/8/8 b - - 0 1"	
            },
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

/* FIXTURES F√úR DIESEN TESTFALL */

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


