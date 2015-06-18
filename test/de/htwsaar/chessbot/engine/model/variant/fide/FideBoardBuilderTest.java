package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;
import static de.htwsaar.chessbot.engine.model.Position.*;

// Interne Referenzen
import de.htwsaar.chessbot.*;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;

/**
* Testklasse für ...
*
* @author
*/
public class FideBoardBuilderTest { 

    // Testvariablen
    private BoardBuilder boardBuilder = VARIANT.getBoardBuilder();

    // Kontrollwerte
    private static final ChessVariant VARIANT = new FideChess();
    private static final Board INITIAL_BOARD;
    static {
        Board board = VARIANT.getBoard(); 
        for (int i = 1; i <= 8; i++) {
            board.addPiece( new Pawn(P(i,2), true, false) );
            board.addPiece( new Pawn(P(i,7), false, false) );
        }
        board.addPiece( new Rook(P("a1"), true, false) );
        board.addPiece( new Rook(P("h1"), true, false) );
        board.addPiece( new Rook(P("a8"), false, false) );
        board.addPiece( new Rook(P("h8"), false, false) );

        board.addPiece( new Knight(P("b1"), true) );
        board.addPiece( new Knight(P("g1"), true) );
        board.addPiece( new Knight(P("b8"), false) );
        board.addPiece( new Knight(P("g8"), false) );

        board.addPiece( new Bishop(P("c1"), true) );
        board.addPiece( new Bishop(P("f1"), true) );
        board.addPiece( new Bishop(P("c8"), false) );
        board.addPiece( new Bishop(P("f8"), false) );

        board.addPiece( new Queen(P("d1"), true) );
        board.addPiece( new Queen(P("d8"), false) );

        board.addPiece( new King(P("e1"), true, false) );
        board.addPiece( new King(P("e8"), false, false) );

        INITIAL_BOARD = board;
    }
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
    
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

    @Test(expected = FenStringParseException.class)
    public void testEmptyString() {
        boardBuilder.fromFenString("");
        // Fehlerhafte Anweisung, die MyException auslöst
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testFideStartingPosition() {
        Board b = boardBuilder.getStartingPosition();
        assertEquals(INITIAL_BOARD, b);
    }

}


