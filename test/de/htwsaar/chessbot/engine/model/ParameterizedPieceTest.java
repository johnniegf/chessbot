package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.*;
import java.util.*;
import static java.util.Arrays.asList;

// JUnit-Pakete
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
* Testklasse für ...
*
* @author
*/

@RunWith(Parameterized.class)
public class ParameterizedPieceTest { 

    // Testvariablen
    private Piece currentPiece;
    private Collection<Position> possibleMoves;
    private Collection<Position> impossibleMoves;

    // Kontrollwerte
    private static final Board EMPTY_BOARD = new Board();
    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] {
            {
                new Queen(P("d1"),true),
                PList("a1","b1","c1","e1","f1","g1","h1",
                      "d2","d3","d4","d5","d6","d7","d8",
                      "a4","b3","c2",
                      "e2","f3","g4","h5"),
                PList("i1","d9","i5","b2","c3")
            },
            {
                new Bishop(P("f1"),true),
                PList("a6","b5", "c4", "d3","e2","g2","h3"),
                PList("e1", "f2", "i4", "g1")
            },
            {
                new Bishop(P("c6"),false),
                PList("a8","b7", "d5", "e4","f3","g2","h1",
                      "a4","b5","d7","e8"),
                PList("f9", "c3", "d6", "c5")
            },
            {
                new King(P("e1"),true,false),
                PList("c1","d1", "d2", "e2","f2","f1","g1"),
                PList("c2", "h1", "e3", "f3")
            },
            {
                new King(P("b5"),true,true),
                PList("a4","a5", "a6", "b4","b6","c4","c5","c6"),
                PList("b7", "d5", "d4", "b8")
            },
            {
                new King(P("h8"),false,true),
                PList("g7","g8", "h7"),
                PList("h9", "g9", "i8", "i9")
            },
            {
                new Knight(P("b1"), true),
                PList("a3", "c3", "d2"),
                PList("b3", "a1", "d1", "c2")
            },
            {
                new Knight(P("c3"), true),
                PList("a2","a4","b1","b5","d1","d5","e2","e4"),
                PList("b2")
            },
            {
                new Knight(P("h8"), false),
                PList("f7", "g6"),
                PList("i6","j7","f9","j9","i10")
            },
            {
                new Knight(P("e7"), false),
                PList("c6","c8","d5","f5","g6","g8"),
                PList("d9","f9","c7","f6")
            },
            {
                new Pawn(P("a2"),true,false),
                PList("a3","a4"),
                PList("b2","a1","b1")
            }, 
            {
                new Pawn(P("e5"),true,true),
                PList("e6"),
                PList("e7","e4","d5")
            }, 
            {
                new Pawn(P("f2"),true,false),
                PList("f3","f4"),
                PList("e2","e4","f1")
            }, 
            {
                new Pawn(P("d7"),false,false),
                PList("d6","d5"),
                PList("d8","c7","e5")
            }, 
            {
                new Pawn(P("h7"),false,false),
                PList("h6","h5"),
                PList("h8","i7","g7")
            }, 
            {
                new Pawn(P("c5"),false,true),
                PList("c4"),
                PList("b4","c5","c6") 
            },
            {
                new Rook(P("a1"),true,false),
                PList("a2","a3","a4","a5","a6","a7","a8",
                      "b1","c1","d1","e1","f1","g1","h1"),
                PList("b2","a9","i1")
            },
            {
                new Rook(P("g3"),true,true),
                PList("g1","g2","g4","g5","g6","g7","g8",
                      "a3","b3","c3","d3","e3","f3","h3"),
                PList("f2","h4","i3","g9")
            },
            {
                new Rook(P("e8"),false,true),
                PList("e1","e2","e3","e4","e5","e6","e7",
                      "a8","b8","c8","d8","f8","g8","h8"),
                PList("f7","e9","i8")
            }
        });
    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public ParameterizedPieceTest(
        Piece currentPiece, 
        Collection<Position> possible, 
        Collection<Position> impossible
    ) 
    {
        this.currentPiece    = currentPiece;
        this.possibleMoves   = possible;
        this.impossibleMoves = impossible;
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
    
    @Test public void testPossibleMoves() {
        for (Position p : this.possibleMoves) {
            assertTrue(currentPiece.getName() + " auf " + 
                       currentPiece.getPosition().toSAN() +
                       " sollte nach " + p.toSAN() + " ziehen können",
                       currentPiece.canMoveTo(p, EMPTY_BOARD) );
        }
        for (Position i : this.impossibleMoves) {
            assertFalse(currentPiece.getName() + " auf " + 
                        currentPiece.getPosition().toSAN() +
                        " sollte nicht nach " + i.toSAN() + " ziehen können",
                        currentPiece.canMoveTo(i, EMPTY_BOARD) );
        }
    }
    
    @Test public void testMoveList() {
        Collection<Position> possibleMoves = 
            currentPiece.getValidMoves(EMPTY_BOARD);

        assertEquals("Länge der Zugliste ist falsch!",
                     this.possibleMoves.size(),
                     possibleMoves.size() );
        for (Position p : this.possibleMoves) 
            assertTrue("Position " + p.toSAN() + " nicht in der Zugliste",
                       possibleMoves.contains(p));
        for (Position i : impossibleMoves) 
            assertFalse("Position " + i.toSAN() + 
                        " sollte nicht in der Zugliste sein",
                        possibleMoves.contains(i));
    }

}


