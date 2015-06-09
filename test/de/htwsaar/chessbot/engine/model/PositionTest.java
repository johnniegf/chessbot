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
    private Position lowerLeft, upperRight;
    // Kontrollwerte
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        Position.setMaxDimensions(8,8);
        positions = new ArrayList<Position>(64);

        for (int x = 1; x <= 8; ++x) 
            for (int y = 1; y <= 8; ++y) {
                positions.add( new Position(x,y) );
            }

        lowerLeft  = new Position(1,1);
        upperRight = new Position(8,8);
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

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeRow() {
        new Position(-1,0);
        // Fehlerhafte Anweisung, die MyException auslöst
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCol() {
        new Position(1,-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceedRowBounds() {
        new Position(20,2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceedColBounds() {
        new Position(1,20);
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testCons() {
        for (int x = 1; x <= 8; ++x) {
            for (int y = 1; y <= 8; ++y) {
                Position p = positions.get(8*(x-1)+(y-1));
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
        lowerLeft.translate(1,2);
        assertEquals( "",
                      new Position(2,3),
                      lowerLeft);
        upperRight.translate(-3,-3);
        assertEquals( "",
                      new Position(5,5),
                      upperRight);
    }

}


