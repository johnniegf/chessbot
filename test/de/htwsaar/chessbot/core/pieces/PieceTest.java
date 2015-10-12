package de.htwsaar.chessbot.core.pieces;

// Interne Referenzen

// Java-API
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.Position;
import static de.htwsaar.chessbot.core.Position.P;
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
    private Piece    currentPiece;
    private char     fen;
    private boolean  isWhite;
    private int      pieceId;
    private Position position;
    
    // Kontrollwerte
    private static final Board TEST_BOARD = new Board();
    private static final Position[] TEST_POSITIONS = {
        P("c3"), P("a1"), P("i2"), P("h12")
    };
    
    private static Piece createPiece(final int pieceId,
                                     final boolean isWhite,
                                     final Position position) 
    {
        switch(pieceId) {
            case King.ID:   return new King(position, isWhite);
            case Queen.ID:  return new Queen(position, isWhite);
            case Rook.ID:   return new Rook(position, isWhite);
            case Knight.ID: return new Knight(position, isWhite);
            case Bishop.ID: return new Bishop(position, isWhite);
            case Pawn.ID:   return new Pawn(position, isWhite);
            
            default: 
                return null;
        }
    }
    
    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            // piece, fen, isWhite, hasMoved, position
            { Pawn.ID,   'P', true,  P("h2") },
            { Knight.ID, 'n', false, P("c3") },
            { Rook.ID,   'R', true,  P("e8") },
            { Queen.ID,  'q', false, P("f1") },
            { Bishop.ID, 'B', true,  P("a6") },
            { King.ID,   'k', false, P("e8") }
        });
    }
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public PieceTest(final int pieceId,
                     final char fen,
                     final boolean isWhite,
                     final Position position) 
    {
        this.currentPiece = createPiece(pieceId, isWhite, position);
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
        currentPiece.setPosition(null);
    }
    @Test(expected = NullPointerException.class)
    public void testMoveToNullPosition() {
        currentPiece.move(null);
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testPosition() {
        Piece p = currentPiece.clone();
        p.setPosition(position);
        assertEquals("",
                     p.getPosition(),
                     position);
    }

    @Test public void testIsWhite() {
        Piece p = currentPiece.clone();
        p.setIsWhite(isWhite);
        assertEquals("",
                     p.isWhite(),
                     isWhite);
    }

    @Test public void testFen() {
        Piece p = currentPiece.clone();
        p.setIsWhite(isWhite);
        assertEquals("",
                     p.fenShort(),
                     fen);
    }

    @Test public void testEquality() {
        Piece a = currentPiece.clone();
        
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
        Piece cloned = currentPiece.clone();
        assertEquals(currentPiece, cloned);
        assertFalse(currentPiece == cloned);
    }

    @Test public void testMove() {
        Position pos = P("g6");
        Piece p = currentPiece.clone();
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
    }

}


