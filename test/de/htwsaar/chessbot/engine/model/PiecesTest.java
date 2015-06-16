package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.*;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assume.*;
import static org.junit.Assert.*;
import org.junit.*;

// Klassen für parametrierte Tests
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

/**
 * Testklasse für die Figurenfabrik.
 *
 * @author David Holzapfel
 * @author Dominik Becker
 */
@RunWith(Parameterized.class)
public class PiecesTest {

    // Testvariablen
    private Piece actual;
    private Piece expected;

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][]{
                {
                        Pieces.getInstance().getPiece(Pieces.PAWN, P("c2"), true, false),
                        new Pawn(P("c2"), true, false)

                },
                {
                        Pieces.getInstance().getPiece(Pieces.ROOK, P("a1"), true, false),
                        new Rook(P("a1"), true, false)

                }
        });

    }

    /**
     * Testaufbau.
     * <p/>
     * Allokiert und initialisiert Ressourcen und Testvariablen.
     */
    public PiecesTest(Piece actual, Piece expected) {
        this.actual = actual;
        this.expected = expected;
    }

/* FIXTURES FÜR DIESEN TESTFALL */

// ====================================================
// = Ausnahmetests
// ====================================================

    @Test(expected = IllegalArgumentException.class)
    public void testPieceTypeTooLow() {
        Pieces.getInstance().getPiece(-2, P("c1"), false, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPieceTypeTooHigh() {
        Pieces.getInstance().getPiece(6, P("d3"), false, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionNull() {
        Pieces.getInstance().getPiece(3, null, false, false);
    }

// ====================================================
// = Funktionstests
// ====================================================

    @Test
    public void testPiecesEqual() {
        assert(actual.equals(expected));
    }

    @Test
    public void testPiecesSameReference() {
        Piece existingPiece = Pieces.getInstance().getPiece(getPieceTypeFromName(actual.getName()), actual.getPosition(),
                actual.isWhite(), actual.hasMoved());
        assert(existingPiece == actual);
    }

    private int getPieceTypeFromName(String name) {
        switch(name) {
            case "Bauer":   return Pieces.PAWN;
            case "Turm":    return Pieces.ROOK;
            case "Springer":return Pieces.KNIGHT;
            case "Läufer":  return Pieces.BISHOP;
            case "Dame":    return Pieces.QUEEN;
            case "König":   return Pieces.KING;
            default: return -42;
        }
    }

}


