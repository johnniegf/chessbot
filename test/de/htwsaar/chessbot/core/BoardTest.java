package de.htwsaar.chessbot.core;

// Interne Referenzen

// Java-API

// JUnit-API
import de.htwsaar.chessbot.core.pieces.Piece;
import de.htwsaar.chessbot.core.pieces.Pieces;
import static org.junit.Assert.*;
import org.junit.*;

/**
* Testklasse für ...
*
* @author Johannes Haupt
*/
public class BoardTest { 

    // Testvariablen
    private Board mEmptyBoard;
    private Board mTestBoard;
    
    // Kontrollwerte
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        mEmptyBoard = new Board();
        mTestBoard  = Board.B();
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

    @Ignore @Test(expected = BoardException.class)
    public void testFuckIt() {
        // Fehlerhafte Anweisung, die MyException auslöst
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testPutPiece() {
        Position piecePos = Position.P("e5");
        Piece whiteKing = Pieces.PC('K', piecePos);
        mTestBoard = mEmptyBoard.clone();
        mTestBoard.putPiece(whiteKing);

        assertEquals("Anzahl der Figuren ist falsch!",
                     1,
                     mTestBoard.getPieceCount() );
        assertFalse("Feld " + piecePos + "sollte besetzt sein",
                    mTestBoard.isFree(piecePos) );
        assertTrue("Figur " + whiteKing + " sollte auf dem Brett existieren!",
                   mTestBoard.pieceExists(whiteKing) );
    }

}


