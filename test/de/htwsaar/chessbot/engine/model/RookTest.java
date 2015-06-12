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
public class RookTest { 

    // Testvariablen
    private Piece whiteRookAtH1;

    // Kontrollwerte
    private static List<Position> possibleMovesAtH1;
    
    static {
        possibleMovesAtH1 = new ArrayList<Position>(14);
        for (int i = 1; i < 8; ++i) {
            possibleMovesAtH1.add( new Position(8,i+1));
            possibleMovesAtH1.add( new Position(i,1));
        }
    }
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        whiteRookAtH1 = new Rook( new Position("h1"), true, false );
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

    @Test public void testWhiteRookAtStartingPosition() {
        for (Position p : possibleMovesAtH1) {
            assertTrue("Turm sollte von " + whiteRookAtH1.getPosition() +
                       " nach " + p.toSAN() + " ziehen können" 
                       + whiteRookAtH1.getValidMoves(null), 
                       whiteRookAtH1.canMoveTo(p, null));
        }
    
    }

}


