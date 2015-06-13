package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.P;

import java.util.*;
// JUnit-Pakete
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
/**
* Testklasse für ...
*
* @author
*/
@RunWith(Parameterized.class)
public class PieceTest { 

    // Testvariablen
    Piece currentPrototype;
    
    // Kontrollwerte
    private static final Board TEST_BOARD = null;
    private static final Position[] TEST_POSITIONS = {
        P("c3"), P("a1"), P("i2"), P("h12")
    };
    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            { new Pawn()   },
            { new Knight() },
            { new Rook()   },
            { new Queen()  },
            { new Bishop() },
            { new King()   }
        });
    }
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public PieceTest(Piece prototype) {
        currentPrototype = prototype;
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
    public void testSetNullPosition() {
        currentPrototype.setPosition(null);
    }
    @Test(expected = NullPointerException.class)
    public void testMoveToNullPosition() {
        currentPrototype.move(null, TEST_BOARD);
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testCloning() {
        Piece cloned = currentPrototype.clone();
        assertEquals(currentPrototype, cloned);
        assertFalse(currentPrototype == cloned);
    }

    @Test public void testColor() {
        Piece piece = currentPrototype.clone();
        piece.setColor(true);
        assertTrue("Die Figur " + piece + " sollte weiß sein",
                   piece.isWhite());
        piece.setColor(false);
        assertFalse("Die Figur " + piece + " sollte schwarz sein",
                    piece.isWhite());
    }

    @Test public void testPositions() {
        for (Position p : TEST_POSITIONS) {
            currentPrototype.setPosition(p);
            assertEquals(p,
                         currentPrototype.getPosition());
        }
    }

    @Test public void testMove() {
        assertFalse(currentPrototype.canMoveTo(null, TEST_BOARD));
    }

    @Test public void testHasMovedFlag() {
        Piece piece = currentPrototype.clone();
        piece.setHasMoved(true);
        assertTrue("",
                   piece.hasMoved() );
        piece.setHasMoved(false);
        assertFalse("",
                    piece.hasMoved() );
    }

}


