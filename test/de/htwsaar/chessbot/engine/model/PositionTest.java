package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.P;
import static de.htwsaar.chessbot.engine.model.Board.B;

// Java-API
import java.util.*;

// JUnit-Pakete
//import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;

/**
* Testklasse für ...
*
* @author
*/
public class PositionTest { 
    
    // Testvariablen
    private List<Position> positions;
    private Position a1, h8;
    
    // Kontrollwerte
    private static final Board EMPTY_BOARD    = new Board();
    private static final Board STANDARD_BOARD = null;
    
    private static final Position[] validPositions = {
        P(1,2), P(5,7), P(8,8), P(5,3), P(4,6)
    };
    private static final Position[] standardPositions = {
        P(1,2), P(5,7), P(4,8), P(5,6), P(2,3)
    };
    private static final int[] translationDeltas = {
        1,2, -2,-5, 2,-2, -4,1, 6,0
    };
    private static final Position[] translationResults = {
        P(2,4), P(3,2), P(6,6), P(1,7), P(8,3)
    };
    private static final Position[] invalidPositions = {
        P(-1,5),    // negative x-Koordinate
        P(7,-42),   // negative y-Koordinate
        P(200,40),  // zu große x-Koordinate
        P(5,51),    // zu große y-Koordinate
        P(0,4),     // x-Koordinate ist 0
        P(2,0)      // y-Koordinate ist 0
    };
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        positions = new ArrayList<Position>(64);

        for (int y = 1; y <= 8; ++y) {
            for (int x = 1; x <= 8; ++x) {
                positions.add( P(y,x) );
            }
        }

        a1  = P(1,1);
        h8 = P(8,8);
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

    @Test(expected = NullPointerException.class)
    public void test() {
        P(null);
    }

    @Test public void testMalformedSanMissingNumber() {
        final Position[] invalids = new Position[]{
            P("a"), P(""), P("e 4"), P("7a"), P("h41"), P("A11")
        };
        for (Position p : invalids) {
            assertFalse("",
                        p.isValid());
            assertFalse("",
                        p.existsOn(STANDARD_BOARD));
            assertEquals("",
                         p,
                         Position.INVALID);
            assertSame("",
                       p,
                       Position.INVALID);
        }          
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testValidPositions() {
        for (Position v : validPositions) {
            assertTrue("Position als falsch gewertet: " + v,
                       v.isValid() ); 
        }
    }

    @Test public void testInvalidPositions() {
        for (Position p : invalidPositions) {
            assertFalse("Position " + p + " als gültig erkannt",
                        p.isValid() );
        }
    }

    @Test public void testExistingPosition() {
        Board b = STANDARD_BOARD;
        assumeNotNull(b);
        for (Position p : standardPositions) {
            assertTrue("Position " + p + " sollte auf dem "
                     + "Standardschachbrett existieren",
                       p.existsOn(b));
        }
    }

    @Test public void testRank() {
        
    }

    @Test public void testTranslation() {
        int deltaX, deltaY;
        Position source, result;
        for (int i = 0; i < standardPositions.length; i++) {
            source = standardPositions[i];
            deltaX = translationDeltas[2*i];
            deltaY = translationDeltas[2*i+1];
            result = source.transpose(deltaX, deltaY);
            assertTrue("",
                       result.isValid());
            assertEquals("",
                         translationResults[i],
                         result);
            assertSame("",
                       translationResults[i],
                       result);
        }
    }

    @Test public void testIdentity() {
        final String pos = "e4";
        Position a = P(pos);
        Position b = P(pos);
        Position c = a.transpose(-2,1);
        assertSame("a und b sollten auf dasselbe Objekt zeigen",
                   a,
                   b);
        assertNotSame("a und c sollte auf verschiedene Objekte zeigen",
                      a,
                      c);
    }

    @Test public void testEquality() {
        final String pos = "e4";
        Position a = P(pos);
        Position b = P(pos);
        Position c = a.transpose(-2,1);
        Position d = Position.INVALID;
     
        assertEquals("a und b sollten gleich sein",
                     a,
                     b);
        assertNotEquals("a und c sollten verschieden sein",
                        a,
                        c);
        assertNotEquals("",
                        a,
                        d);
        assertEquals("",
                     d,
                     P(-1,50));
        assertSame("",  
                   d,
                   P("e42"));
    }

}


