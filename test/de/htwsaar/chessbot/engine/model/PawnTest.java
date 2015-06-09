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
public class PawnTest { 

    // Testvariablen
    private Piece whitePawnAtA2;
    private Piece whitePawnAtE5;
    private Piece whitePawnAtF2;
    private Piece blackPawnAtD7;
    private Piece blackPawnAtH7;
    private Piece blackPawnAtC5;

    // Kontrollwerte
    private static List<Position> possibleMovesAtA2;
    private static List<Position> possibleMovesAtE5;
    private static List<Position> possibleMovesAtF2;
    private static List<Position> possibleMovesAtD7;
    private static List<Position> possibleMovesAtH7;
    private static List<Position> possibleMovesAtC5;
    
    static {
        possibleMovesAtA2 = new ArrayList<Position>(3);
        possibleMovesAtA2.add( new Position(1,3) );
        possibleMovesAtA2.add( new Position(1,4) );
        possibleMovesAtA2.add( new Position(2,3) );

        possibleMovesAtF2 = new ArrayList<Position>(4);
        possibleMovesAtF2.add( new Position(5,3) );
        possibleMovesAtF2.add( new Position(6,3) );
        possibleMovesAtF2.add( new Position(7,3) );
        possibleMovesAtF2.add( new Position(6,4) );

        possibleMovesAtE5 = new ArrayList<Position>(3);
        possibleMovesAtE5.add( new Position(4,6) );
        possibleMovesAtE5.add( new Position(5,6) );
        possibleMovesAtE5.add( new Position(6,6) );

        possibleMovesAtD7 = new ArrayList<Position>(4);
        possibleMovesAtD7.add( new Position(3,6) );
        possibleMovesAtD7.add( new Position(4,6) );
        possibleMovesAtD7.add( new Position(5,6) );
        possibleMovesAtD7.add( new Position(4,5) );
        
        possibleMovesAtH7 = new ArrayList<Position>(3);
        possibleMovesAtH7.add( new Position(7,6) );
        possibleMovesAtH7.add( new Position(8,6) );
        possibleMovesAtH7.add( new Position(8,5) );
        
        possibleMovesAtC5 = new ArrayList<Position>(3);
        possibleMovesAtC5.add( new Position(2,4) );
        possibleMovesAtC5.add( new Position(3,4) );
        possibleMovesAtC5.add( new Position(4,4) );
    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        whitePawnAtA2 = new Pawn( new Position(1,2), true, false );
        whitePawnAtE5 = new Pawn( new Position(5,5), true, true  );
        whitePawnAtF2 = new Pawn( new Position(6,2), true, false );
        blackPawnAtD7 = new Pawn( new Position(4,7), false, false );
        blackPawnAtH7 = new Pawn( new Position(8,7), false, false );
        blackPawnAtC5 = new Pawn( new Position(3,5), false, true  );
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

    @Test public void testPossibleMovesAtWhiteStartingPositionAtBorder() {
        Collection<Position> possibleMoves =  whitePawnAtA2.getValidMoves(null);
        assertTrue("",
                   possibleMovesAtA2.containsAll(possibleMoves)
                   && possibleMoves.containsAll(possibleMovesAtA2));
    }
    
    @Test public void testPossibleMovesAtWhiteStartingPosition() {
        Collection<Position> possibleMoves =  whitePawnAtF2.getValidMoves(null);
        assertTrue("",
                   possibleMovesAtF2.containsAll(possibleMoves)
                   && possibleMoves.containsAll(possibleMovesAtF2));
    }

    @Test public void testPossibleMovesForWhite() {
        Collection<Position> possibleMoves =  whitePawnAtE5.getValidMoves(null);
        assertTrue("",
                   possibleMovesAtE5.containsAll(possibleMoves)
                   && possibleMoves.containsAll(possibleMovesAtE5));
    }
    
    @Test public void testPossibleMovesAtBlackStartingPositionAtBorder() {
        Collection<Position> possibleMoves =  blackPawnAtH7.getValidMoves(null);
        assertTrue("",
                   possibleMovesAtH7.containsAll(possibleMoves)
                   && possibleMoves.containsAll(possibleMovesAtH7));
    }
    
    @Test public void testPossibleMovesAtBlackStartingPosition() {
        Collection<Position> possibleMoves =  blackPawnAtD7.getValidMoves(null);
        assertTrue("",
                   possibleMovesAtD7.containsAll(possibleMoves)
                   && possibleMoves.containsAll(possibleMovesAtD7));
    }

    @Test public void testPossibleMovesForBlack() {
        Collection<Position> possibleMoves =  blackPawnAtC5.getValidMoves(null);
        assertTrue("",
                   possibleMovesAtC5.containsAll(possibleMoves)
                   && possibleMoves.containsAll(possibleMovesAtC5));
    }
}
