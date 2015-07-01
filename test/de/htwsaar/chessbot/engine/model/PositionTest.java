package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.*;

// Java-API
import java.util.*;

// JUnit-Pakete
import static org.hamcrest.CoreMatchers.*;
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
    private static final Board EMPTY_BOARD    = null;
    private static final Board STANDARD_BOARD = null;
    
    private static final Position[] validPositions = {
        P(1,2), P(5,7), P(12,8), P(5,32), P(15,4)
    };
    private static final Position[] standardPositions = {
        P(1,2), P(5,7), P(4,8), P(5,6), P(2,3)
    };
    private static final Position[] translationDeltas = {
        P(1,2), P(-2,-5), P(2,-2), P(-4,1), P(6,0)
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

    @Test(expected = SanStringParseException.class)
    public void testMalformedSanReverse() {
        P("1a");
    }

    @Test(expected = SanStringParseException.class)
    public void testMalformedSanMissingNumber() {
        P("a");
    }

    @Test(expected = SanStringParseException.class)
    public void testMalformedSanLeadingZero() {
        P("a01");
    }

    @Test(expected = SanStringParseException.class)
    public void testMalformedSanMissingLetter() {
        P("12");
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

    @Test public void testCons() {
        for (int y = 1; y <= 8; ++y) {
            for (int x = 1; x <= 8; ++x) {
                Position p = positions.get(8*(y-1)+(x-1));
                assertEquals("Zeile stimmt nicht",
                             x,
                             p.getRow());
                assertEquals("Spalte stimmt nicht",
                             y,
                             p.getColumn());
            }
        }
    }

    @Test public void testTranslation() {
        Position source, delta, result;
        for (int i = 0; i < standardPositions.length; i++) {
            source = standardPositions[i];
            delta  = translationDeltas[i];
            result = source.transpose(delta);
            assertEquals("",
                         translationResults[i],
                         result);
        }
    }

    @Test public void testClone() {
        Position orig = P(5,5);
        Position clon = orig.clone();
        assertEquals("Position wurde beim Kopieren verändert: ",
                     orig,
                     clon);
        assertNotSame("Original und Kopie sind dasselbe Objekt",
                      orig,
                      clon );
        orig = orig.setColumn(2);
        assertNotEquals("Änderung am Original wirkt sich auf Kopie aus",
                        orig,
                        clon );
                    
    }

}


