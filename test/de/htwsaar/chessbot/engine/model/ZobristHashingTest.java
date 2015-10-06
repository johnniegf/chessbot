package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import de.htwsaar.chessbot.*;
import static de.htwsaar.chessbot.engine.model.BoardUtils.Color.WHITE;
import static de.htwsaar.chessbot.engine.model.BoardUtils.toBitBoard;

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
* @author Johannes Haupt <johnniegf@fsfe.org>
*/
public class ZobristHashingTest { 

    // Testvariablen
    private Board emptyBoard;
    private Board emptyBoardFromFen;
    
    // Kontrollwerte
    private static final String EMPTY_FEN =
        "8/8/8/8/8/8/8/8 w - - 0 1";
    private static final ZobristHasher HASHER = ZobristHasher.getInstance();
    
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    @Before public void prepareTest() {
        emptyBoard = new Board();
        emptyBoardFromFen = Board.B(EMPTY_FEN);
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
    // = Funktionstests
    // ====================================================

    @Test public void testEmptyBoards() {
        assertEquals(
            "Hashes für leeres Schachbrett stimmen nicht überein",
            emptyBoard.hash(),
            emptyBoardFromFen.hash()
        );
    }
    
    @Test public void testExecuteSomeMoves() {
        Board b = Board.B();
    }
    
    @Test public void testPutPieces() {
        Board tmpBoard;
        Board tmpFenBoard;
        for (int t = 0; t < 6; t++) 
            for (int c = 0; c < 2; c++) 
                for (int p = 0; p < 63; p++) {
                    tmpBoard = new Board();
                    boolean isWhite = c == WHITE;
                    long pieceHash = ZobristHasher.getInstance().hashPiece(t, c == WHITE, p);
                    long boardHashBefore = tmpBoard.hash();
                    long boardHashAfter = boardHashBefore ^ pieceHash;
                    tmpBoard.putPiece(t, c, toBitBoard(p));
                    assertEquals(
                        "Hash für Stellung falsch",
                        boardHashAfter,
                        tmpBoard.hash()
                    );
                    tmpFenBoard = Board.B(tmpBoard.toFenString());
                    assertEquals(
                        "Hash <neu> vs <fen> verschieden für Stellung " + tmpFenBoard,
                        tmpBoard.hash(),
                        tmpFenBoard.hash()
                    );
                }
    }
    
    @Test public void testCastlings() {
        Board tmpBoard;
        for (byte cstl = 0; cstl < 16; cstl++) {
            
        }
    }

}


