package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.*;

import java.util.*;

// JUnit-Pakete
import static org.junit.Assert.*;
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
    private Board board;

    // Kontrollwerte
    private static final Board NULL_BOARD = new Board(); 

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            {
                new King(P("e1"),true,false),
                P("f2"),
                true,
                NULL_BOARD
            },
            {
                new Knight(P("d7"),false),
                P("c6"),
                false,
                NULL_BOARD
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
        this.currentMove = new Move(expectedPiece, expectedPosition);
    }

/* FIXTURES FÜR DIESEN TESTFALL */

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testIsMovePossible() {
        assertEquals("",
                     isPossible,
                     currentMove.isPossible(board));
    }

    @Test public void testIsNullMove() {
        assertFalse("Zug " + currentMove + " sollte kein Nullzug sein",
                    currentMove.isNull());
    }
}


