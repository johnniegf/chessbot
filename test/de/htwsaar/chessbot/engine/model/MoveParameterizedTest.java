package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.*;
import de.htwsaar.chessbot.engine.model.variant.fide.*;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

/**
* Testklasse für ...
*
* @author
*/
@RunWith(Parameterized.class)
public class MoveParameterizedTest { 

    // Testvariablen
    private Move currentMove;
    private Piece expectedPiece;
    private Position expectedPosition;
    private boolean isPossible;
    private Board expectedBoard;
    private Board board;

    // Kontrollwerte
    private static final Board EMPTY_STANDARD_BOARD = new Board(); 

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            {
                new King(P("e1"), true, false),
                P("f2"),
                true,
                new Board()
            },
            {
                new Pawn(P("h7"), false, false),
                P("h6"),
                true,
                new Board()
            },
            {
                new Bishop(P("c7"), true),
                P("f4"),
                true,
                new Board()
            },
            {
                new Queen(P("d1"), true),
                P("i6"),
                false,
                new Board()
            },
            {
                new Knight(P("d7"),false),
                P("c6"),
                false,
                new Board()
            }
        });

    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public MoveParameterizedTest(final Piece expectedPiece,
                                 final Position expectedTarget,
                                 final boolean possible,
                                 final Board board) 
    {
        this.expectedPiece = expectedPiece;
        this.expectedPosition = expectedTarget;
        this.isPossible = possible;
        this.board = board;
        this.board.addPiece(expectedPiece);
        if ( isPossible ) {
            Piece expectedPieceAfterMove = expectedPiece.clone();
            expectedPieceAfterMove.setPosition(expectedPosition);
            this.expectedBoard = new Board();
            this.expectedBoard.addPiece(expectedPieceAfterMove);
        } else {
            this.expectedBoard = null;
        }
        this.currentMove = new Move(expectedPiece, expectedPosition);
    }

/* FIXTURES FÜR DIESEN TESTFALL */

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testIsMovePossible() {
        assumeThat(board.getPieceCount(), is(1));
        assertEquals("Zug " + currentMove + " sollte möglich sein",
                     isPossible,
                     currentMove.isPossible(board));
    }

    @Test public void testIsNullMove() {
        assertFalse("Zug " + currentMove + " sollte kein Nullzug sein",
                    currentMove.isNull());
    }

    @Test public void testConstructionFromBoard() {
        assumeThat(board.getPieceCount(), is(1));
        Move move = new Move(board, 
                             expectedPiece.getPosition(),
                             expectedPosition);

        assertEquals("Zugkonstruktion aus Schachbrett fehlgeschlagen",
                     currentMove,
                     move);
    }

    @Test public void testExecuteMove() {
        Board moveResult = currentMove.execute(board);
        assertEquals("Stellung nach Zug " + currentMove + " falsch",
                     expectedBoard,
                     moveResult);
    }
}


