package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.*;
import static de.htwsaar.chessbot.engine.model.Pieces.PC;
//import static de.htwsaar.chessbot.engine.model.Move.MV;
import static de.htwsaar.chessbot.engine.model.Board.B;

import java.util.*;
import static java.util.Arrays.asList;

// JUnit-Pakete
//import static org.hamcrest.CoreMatchers.*;
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
    private Collection<Position> attacks;
    private Collection<Position> impossibleMoves;
    private Board board;

    // Kontrollwerte
    private static final Board EMPTY_BOARD = new Board();
    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] {
            {
                PC('Q', P("d1")),
                PList("a1","b1","c1","e1","f1","g1","h1",
                      "d2","d3","d4","d5","d6","d7","d8",
                      "a4","b3","c2",
                      "e2","f3","g4","h5"),
                PList("a2","b2","c3","e3","f2"),
                null
            },
            {
                PC('q', P("h8")),
                PList("a8","b8","c8","d8","e8","f8","g8",
                      "h1","h2","h3","h4","h5","h6","h7",
                      "a1","b2","c3","d4","e5","f6","g7"),
                PList("f7","g6","e6"),
                null
            },
            {
                PC('B', P("f1")),
                PList("a6","b5", "c4", "d3","e2","g2","h3"),
                PList("e1", "f2", "i4", "g1"),
                null
            },
            {
                PC('b', P("c6")),
                PList("a8","b7", "d5", "e4","f3","g2","h1",
                      "a4","b5","d7","e8"),
                PList("f9", "c3", "d6", "c5"),
                null
            },
            {
                PC('K', P("e1"), false),
                PList("d1", "d2", "e2","f2","f1"),
                PList("c1", "c2", "h1", "e3", "f3"),
                PList("d1", "d2", "e2","f2","f1"),
            },
            {
                PC('K', P("b5"), true),
                PList("a4","a5", "a6", "b4","b6","c4","c5","c6"),
                PList("b7", "d5", "d4", "b8"),
                null
            },
            {
                PC('k', P("h8"), true),
                PList("g7","g8", "h7"),
                PList("h9", "g9", "i8", "i9"),
                null
            },
            {
                PC('N', P("b1")),
                PList("a3", "c3", "d2"),
                PList("b3", "a1", "d1", "c2"),
                null
            },
            {
                PC('N', P("c3")),
                PList("a2","a4","b1","b5","d1","d5","e2","e4"),
                PList("b2"),
                null
            },
            {
                PC('n', P("h8")),
                PList("f7", "g6"),
                PList("i6","j7","f9","j9","i10"),
                null
            },
            {
                PC('n', P("e7")),
                PList("c6","c8","d5","f5","g6","g8"),
                PList("d9","f9","c7","f6"),
                null
            },
            {
                PC('P', P("a2"), false),
                PList("a3","a4"),
                PList("b2","a1","b1"),
                PList("b3")
            }, 
            {
                PC('P', P("e5"), true),
                PList("e6"),
                PList("e7","e4","d5"),
                PList("d6", "f6")
            }, 
            {
                PC('P', P("f2"), false),
                PList("f3","f4"),
                PList("e2","e4","f1"),
                PList("e3","g3"),
            },
            {
                PC('p', P("f2"), true),
                PList("f1"),
                PList("e1","g1"),
                PList("e1","g1")
            },
            {
                PC('p', P("d7"), false),
                PList("d6","d5"),
                PList("d8","c7","e5"),
                PList("e6","c6")
            }, 
            {
                PC('p', P("h7"), false),
                PList("h6","h5"),
                PList("h8","g6","g7"),
                PList("g6")
            }, 
            {
                PC('p', P("c5"), true),
                PList("c4"),
                PList("b4","c5","c6"),
                PList("b4", "d4")
            },
            {
                PC('p', P("a4"), true),
                PList("a3"),
                PList("a2","b3"),
                PList("b3")
            },
            {
                PC('R', P("a1"), false),
                PList("a2","a3","a4","a5","a6","a7","a8",
                      "b1","c1","d1","e1","f1","g1","h1"),
                PList("b2","a9","i1"),
                null
            },
            {
                PC('R', P("g3"), true),
                PList("g1","g2","g4","g5","g6","g7","g8",
                      "a3","b3","c3","d3","e3","f3","h3"),
                PList("f2","h4","i3","g9"),
                null
            },
            {
                PC('r', P("e8"), true),
                PList("e1","e2","e3","e4","e5","e6","e7",
                      "a8","b8","c8","d8","f8","g8","h8"),
                PList("f7","e9","i8"),
                null
            }
        });
    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public ParameterizedPieceTest(
        final Piece currentPiece, 
        final Collection<Position> possible, 
        final Collection<Position> impossible,
        final Collection<Position> attacks
    ) 
    {
        this.currentPiece    = currentPiece;
        this.possibleMoves   = possible;
        this.impossibleMoves = impossible;
        this.attacks         = (attacks == null ? possibleMoves : attacks);
        this.board           = EMPTY_BOARD.clone();
        this.board.setWhiteAtMove(currentPiece.isWhite());
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
            assertTrue(currentPiece.getClass().getName() + " auf " + 
                       currentPiece.getPosition().toString() +
                       " sollte nach " + p.toString() + " ziehen können",
                       currentPiece.canMoveTo(this.board, p) );
        }
        for (Position i : this.impossibleMoves) {
            assertFalse(currentPiece.getClass().getName() + " auf " + 
                        currentPiece.getPosition().toString() +
                        " sollte nicht nach " + i.toString() + " ziehen können",
                        currentPiece.canMoveTo(this.board, i) );
        }
    }
   
    @Test public void testMoveList() {
        Collection<Position> possibleMoves = new ArrayList<Position>();
        for (Move m : currentPiece.getMoves(this.board)) {
            Position p = m.getTarget();
            if (!possibleMoves.contains(p))
                possibleMoves.add(p);
        }

        assertEquals("Länge der Zugliste ist falsch!",
                     this.possibleMoves.size(),
                     possibleMoves.size() );
        for (Position p : this.possibleMoves) 
            assertTrue("Position " + p.toString() + " nicht in der Zugliste",
                       possibleMoves.contains(p));
        for (Position i : impossibleMoves) 
            assertFalse("Position " + i.toString() + 
                        " sollte nicht in der Zugliste sein",
                        possibleMoves.contains(i));
    }
    
    @Test public void testAttacks() {
        for (Position p : this.attacks) {
            assertTrue(currentPiece + " sollte " + p + " angreifen",
                       currentPiece.attacks(this.board, p) );
        }
    }

    @Test public void testNullMove() {
        assertFalse(currentPiece + " darf nicht auf seinem Startfeld ankommen!",
                    currentPiece.canMoveTo( this.board,    
                                            currentPiece.getPosition() 
                    ) );
    }

}


