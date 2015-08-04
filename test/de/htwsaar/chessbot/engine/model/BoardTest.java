package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.*;
import de.htwsaar.chessbot.engine.exception.BoardException;

// JAVA-Util Packete
import java.util.Arrays;

// JUnit-Pakete
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
* Testklasse für Board
*
* @author Timo Klein
*/
public class BoardTest { 

    // Testvariablen
    private Board       board;
    private Piece       piece;
    private Position    pos;

    // Kontrollwerte
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before 
    public void prepareTest() {
        board   = new Board();
        piece   = new Rook(P("c3"));
        pos     = new Position("a1");
    }

    /**
    * Testabbau.
    *
    * Gibt Ressourcen und Testvariablen frei.
    */
    @After 
    public void cleanUp() {
    }

/* FIXTURES FÜR DIESEN TESTFALL */

    // ====================================================
    // = Ausnahmetests
    // ====================================================

    /*
    @Test(expected = BoardException.class)
    public void testIllegalDimensions() {
    }*/

    // ====================================================
    // = Funktionstests
    // ====================================================
       
    @Test
    public void testIsEmpty() {
        assertTrue("leer", board.isEmpty());
    }

    @Test
    public void testPieceAt() {
        assumeTrue( board.addPiece(piece) );
        assumeThat( board.getPieceCount(), is(1) );
        assertTrue("Figur-Position" , board.pieceAt(piece.getPosition()).equals(piece));
    }
    
    @Test
    public void testClone() {
        Board boardClone = board.clone();
        assertNotSame("Original und Kopie sind dasselbe Objekt",
                      board,
                      boardClone);
        assertTrue("Boardclone", board.equals(boardClone));
    }
    
    @Test
    public void testAddPiece() {
        assertTrue("Hinzufuegen von Figur " + piece + " auf " + 
                   board + "fehlgeschlagen", 
                   board.addPiece(piece));
        assertEquals("Figur " + piece + " ist nicht auf Brett",
                     board.getPiece(piece),
                     piece);
        assertThat("", board.getPieceCount(), is(1));
        assertFalse("Hinzufuegen von Figur", board.addPiece(piece));
    }

    @Test
    public void testRemovePiece() {
        assumeTrue( board.addPiece(piece) );
        assumeThat( board.getPieceCount(), is(1) );
        assertTrue("Loeschen von Figuren", board.removePiece(piece) );
    }
    
    @Ignore @Test
    public void testIsAttacked() {
        piece.setPosition(P("d3"));
        piece.setHasMoved(true);
        piece.setColor(true);
        board.addPiece(piece);
        
        piece.setPosition(P("e5"));
        piece.setHasMoved(true);
        piece.setColor(false);
        board.addPiece(piece);

        for (Position p : PList("a3", "d6")) {
            assertTrue("Feld " + p.toSAN() + " nicht von weiß bedroht", 
                       board.isAttacked(p, true));
        }
        for (Position p : PList("e1", "f5")) {
            assertTrue("Feld " + p.toSAN() + " nicht von schwarz bedroht", 
                       board.isAttacked(p, false));
        }
        for (Position p : PList("a8", "a1", "h8")) {
            assertTrue("Feld " + p.toSAN() + " von weiß bedroht", 
                       board.isAttacked(p, true));
        }
        for (Position p : PList("d3", "h1", "h8")) {
            assertTrue("Feld " + p.toSAN() + " von schwarz bedroht", 
                       board.isAttacked(p, false));
        }
    }
   
    @Test
    public void testIsFree() {
        assumeTrue( board.addPiece(piece) );
        assumeThat( board.getPieceCount(), is(1) );
        assertTrue("Position ist frei", board.isFree(pos));
        assertFalse("Position ist besetzt", board.isFree(piece.getPosition()));
    }

    @Test
    public void testGetPieceCount() {
        assertTrue("Figurenzaehler", board.getPieceCount() == 0);
        assumeTrue( board.addPiece(piece) );
        assertThat("Figurenzaehler", board.getPieceCount(), is(1));
    }


}
