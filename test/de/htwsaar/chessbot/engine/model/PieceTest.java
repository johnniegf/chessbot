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
public class PieceTest { 

    // Testvariablen
    private List<Piece> whitePieces, blackPieces;
    // Kontrollwerte
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        whitePieces.add( new Knight(new Position(1,2), true)  );
        blackPieces.add( new Knight(new Position(8,7), false) );
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
    public void testNullPosition() {
        // Fehlerhafte Anweisung, die MyException auslöst
        new Knight(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullPosition() {
        for ( Piece w : whitePieces ) {
            w.setPosition(null);
        }
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testColor() {
        for ( Piece w : whitePieces ) {
            assertTrue("Figur hat falsche Farbe " + w,
                       w.isWhite() );
        }

        for ( Piece b : blackPieces ) {
            assertFalse("Figur hat falsche Farbe " + b,
                       b.isWhite() );
        }  
    }

}


