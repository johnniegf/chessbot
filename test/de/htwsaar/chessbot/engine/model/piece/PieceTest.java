package de.htwsaar.chessbot.engine.model.piece;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.P;

// Java-API
import java.util.*;

// JUnit-API
//import static org.hamcrest.CoreMatchers.*;
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
    private Piece currentPrototype;
    private char fen;
    private boolean isWhite;
    private int pieceId;
    private Position position;
    
    // Kontrollwerte
    private static final Board TEST_BOARD = new Board();
    private static final Position[] TEST_POSITIONS = {
        P("c3"), P("a1"), P("i2"), P("h12")
    };
    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            // piece, fen, isWhite, hasMoved, position
            { new Pawn(),   Pawn.ID,   'P', true,  P("h2") },
            { new Knight(), Knight.ID, 'n', false, P("c3") },
            { new Rook(),   Rook.ID,   'R', true,  P("e8") },
            { new Queen(),  Queen.ID,  'q', false, P("f1") },
            { new Bishop(), Bishop.ID, 'B', true,  P("a6") },
            { new King(),   King.ID,   'k', false, P("e8") }
        });
    }
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public PieceTest(final Piece prototype,
                     final char fen,
                     final int pieceId,
                     final boolean isWhite,
                     final Position position) 
    {
        this.currentPrototype = prototype;
        this.pieceId = pieceId;
        this.fen = fen;
        this.isWhite = isWhite;
        this.position = position;
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
        currentPrototype.move(null);
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testPosition() {
        Piece p = currentPrototype.clone();
        p.setPosition(position);
        assertEquals("",
                     p.getPosition(),
                     position);
    }

    @Test public void testIsWhite() {
        Piece p = currentPrototype.clone();
        p.setIsWhite(isWhite);
        assertEquals("",
                     p.isWhite(),
                     isWhite);
    }

    @Test public void testFen() {
        Piece p = currentPrototype.clone();
        p.setIsWhite(isWhite);
        assertEquals("",
                     p.fenShort(),
                     fen);
    }

    @Test public void testEquality() {
        Piece a = currentPrototype.clone();
        a.setPosition(position);
        a.setIsWhite(isWhite);
        a.setHasMoved(hasMoved);
        
        Piece b = Pieces.PC(pieceId, isWhite, position);
        b.setPosition(position);
        b.setIsWhite(isWhite);
        
        Piece c = a.clone();
        c.setIsWhite(!isWhite);

        assertFalse(a.equals(null));
        assertTrue(a.equals(a));
        assertEquals(a,b);
        assertEquals(a.equals(b), b.equals(a));
        assertEquals(a.equals(b) && b.equals(c),
                     a.equals(c) );                
    }

    @Test public void testCloning() {
        Piece cloned = currentPrototype.clone();
        assertEquals(currentPrototype, cloned);
        assertFalse(currentPrototype == cloned);
    }

    @Test public void testMove() {
        Position pos = P("g6");
        Piece p = currentPrototype.clone();
        Piece pm = p.move(pos);
        assertNotEquals("",
                        p,
                        pm);
        assertNotSame("",
                      p,
                      pm);
        assertEquals("",
                     pm.getPosition(),
                     pos);
        assertTrue("",
                   pm.hasMoved());
    }

}


