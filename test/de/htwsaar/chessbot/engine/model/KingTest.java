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
public class KingTest { 

    // Testvariablen
    private Piece whiteKingAtE1;

    // Kontrollwerte
    private static List<Position> possibleMovesAtE1;
    private static List<Position> impossibleMovesAtE1;
    
    private static final Board EMPTY_BOARD = null;

    static {
        possibleMovesAtE1 = new ArrayList<Position>(7);
        possibleMovesAtE1.add( new Position(3,1) );
        possibleMovesAtE1.add( new Position(4,1) );
        possibleMovesAtE1.add( new Position(4,2) );
        possibleMovesAtE1.add( new Position(5,2) );
        possibleMovesAtE1.add( new Position(6,2) );
        possibleMovesAtE1.add( new Position(6,1) );
        possibleMovesAtE1.add( new Position(7,1) );
        
        impossibleMovesAtE1 = new ArrayList<Position>(3);
        impossibleMovesAtE1.add( new Position(2,1) );
        impossibleMovesAtE1.add( new Position(8,5) );
        impossibleMovesAtE1.add( new Position(4,3) );
    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        whiteKingAtE1 = new King(new Position(5,1), true, false); 
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

    @Test public void testWhiteKingAtStartingPosition() {
        Collection<Position> possibleMoves = whiteKingAtE1.getValidMoves(EMPTY_BOARD);
        assertTrue("Listen stimmen nicht überein (contains)",
                    possibleMovesAtE1.containsAll(possibleMoves) &&
                    possibleMoves.containsAll(possibleMovesAtE1));
        for (Position i : impossibleMovesAtE1) {
            assertFalse("canMoveTo()",
                       whiteKingAtE1.canMoveTo(i, EMPTY_BOARD));
        }
    }

}


