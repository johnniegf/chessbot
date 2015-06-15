package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.P;

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

    @Test public void testEquality() {
        Piece a, b, c;
        a = currentPrototype.clone();
        b = a.clone();
        b.setColor(!a.isWhite());
        c = b.clone();
        c.setHasMoved(!b.hasMoved());
        
        assertFalse(a.equals(null));
        assertTrue(a.equals(a));
        assertEquals(a.equals(b), b.equals(a));
        assertEquals(a.equals(b) && b.equals(c),
                     a.equals(c) );                
    }

    @Test public void testHashFunction() {
        Piece a, b, c;
        a = currentPrototype.clone();
        b = a.clone();

        assertTrue("Hashwert ist kleiner als 0 : " + a.hashCode(),
                   a.hashCode() > 0 );
        assertEquals("Hashwerte stimmen nicht überein", 
                     a.hashCode(), 
                     b.hashCode());
    }

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


