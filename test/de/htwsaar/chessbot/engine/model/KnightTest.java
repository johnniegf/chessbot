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
public class KnightTest { 

    // Testvariablen
    private Piece whiteKnightAtB1;

    // Kontrollwerte
    private static List<Position> possibleMovesAtB1;

    static {
        possibleMovesAtB1 = new ArrayList<Position>(3);
        possibleMovesAtB1.add( new Position(1,3) );
        possibleMovesAtB1.add( new Position(3,3) );
        possibleMovesAtB1.add( new Position(4,2) );
        

    }
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        whiteKnightAtB1 = new Knight(new Position(2,1), true);
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
    
    @Test public void testWhiteKnightAtBorder() {
        Collection<Position> possibleMoves = whiteKnightAtB1.getValidMoves(null);
        assertTrue("",
                   possibleMoves.containsAll(possibleMovesAtB1) &&
                   possibleMovesAtB1.containsAll(possibleMoves) );
                   
    }

}


