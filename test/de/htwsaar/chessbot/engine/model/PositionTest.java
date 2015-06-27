package de.htwsaar.chessbot.engine.model;

import java.util.*;

// JUnit-Pakete
import org.junit.*;
import static org.junit.Assert.*;

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
    private static final Position[] sanTestInput = {
        new Position(5,2),
        new Position(3,8),
        new Position(1,1),
        new Position(6,2),
        new Position(26,17)
    };
    private static final String[] sanExpecteds = {
        "e2", "c8", "a1", "f2", "z17"
    };
    private static final Board EMPTY_BOARD = null;
    
    private static final Position[] validPositions = {
        new Position(1,2), new Position(5,7),
        new Position(12,8), new Position(5,32),
        new Position(15,4)
    };
    private static final Position[] invalidPositions = {
        new Position(-1,5), new Position(200,40),
        new Position(0,0), new Position(7,-42),
        new Position(5,51)
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
                positions.add( new Position(y,x) );
            }
        }

        a1  = new Position(1,1);
        h8 = new Position(8,8);
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
/*
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCol() {
        new Position(-1,0);
        // Fehlerhafte Anweisung, die MyException auslöst
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeRow() {
        new Position(1,-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceedColBounds() {
        new Position(20,2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceedRowBounds() {
        new Position(1,20);
    }
*/
    // ====================================================
    // = Funktionstests
    // ====================================================

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
        Position p1 = a1.translate(1,2);
        assertEquals( "",
                      new Position(3,2),
                      p1);

        Position p2 = h8.translate(-3,-3);
        assertEquals( "",
                      new Position(5,5),
                      p2);
    }

    @Test public void testSanConversion() {
        for (int i = 0; i < sanTestInput.length; i++) {
            assertEquals("",
                         sanExpecteds[i],
                         sanTestInput[i].toSAN() );
        }
    }

    @Test public void testConstructFromSanString() {
        Position pe = new Position(1,1);
        Position pa = new Position("a1");
        assertEquals("", pe, pa);
    }

    @Test public void testClone() {
        Position orig = new Position(5,5);
        Position clon = orig.clone();
        assertEquals("Position wurde beim Kopieren verändert: ",
                     orig,
                     clon);
        assertFalse("Original und Kopie sind dasselbe Objekt",
                    orig == clon );
        orig = orig.setColumn(2);
        assertFalse("Änderung am Original wirkt sich auf Kopie aus",
                    orig.equals(clon) );
                    
    }

    @Test public void testValidPositions() {
        for (Position v : validPositions) {
            assertTrue("Position als falsch gewertet: " + v,
                       v.isValid() ); 
        }
    }

    @Test public void testInvalidPositions() {
        for (Position i : invalidPositions) {
            assertFalse("Position als gültig eingestuft: " + i,
                        i.isValid() );
        }   
    }

}


