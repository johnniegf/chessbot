package de.htwsaar.chessbot.core.moves;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.BoardBuilder;
import static de.htwsaar.chessbot.core.Position.P;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
* Testklasse 
*
* @author Henning Walte
* @author Dominik Becker
*/
//TODO: 
// --- Henning:   Dame, Springer, Bauer;
// --- Dominik:   Turm, Läufer, König;
// --- Sonstiges: Rochade, En passant, Bauernumwandlung
@RunWith(Parameterized.class)
public class ParameterizedMoveTest { 

    // Testvariablen
	private static BoardBuilder builder = Board.BUILDER;
	private Board start, expected;
	private Move mv;
	private boolean isPossible;
	

    // Kontrollwerte
   // private static final ...

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] {
            // 0
            // Ausgangsstellung, Zug, ist möglich, Ergebnis
           {
        	   "8/8/8/8/8/8/4P3/8 w - - 0 1", 
               new StandardMove(P("e2"), P("e3")), 
               true, 
               "8/8/8/8/8/4P3/8/8 b - - 0 1"            	
           },
        	//Rook
       		//true
            {
        	   "7p/8/8/8/8/8/8/7R w - - 0 1", 
               new StandardMove(P("h1"), P("h8")), 
               true, 
               "7R/8/8/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/8/8/8/3q3R w - - 0 1", 
                new StandardMove(P("h1"), P("d1")), 
                true, 
                "8/8/8/8/8/8/8/3R4 b - - 0 1"
            },
            {
            	"8/8/8/4b3/8/8/8/4R3 w - - 0 1", 
                new StandardMove(P("e1"), P("e5")), 
                true, 
                "8/8/8/4R3/8/8/8/8 b - - 0 1"
            },
            {
        	   	"r3P3/8/8/8/8/8/8/8 b - - 0 1", 
                new StandardMove(P("a8"), P("e8")), 
                true, 
                "4r3/8/8/8/8/8/8/8 w - - 0 2"
            },
            // 5
            {
        	   	"r7/8/8/8/8/8/8/B7 b - - 0 1", 
                new StandardMove(P("a8"), P("a1")), 
                true, 
                "8/8/8/8/8/8/8/r7 w - - 0 2"
            },
            //false
            {
        	   	"8/8/8/8/7R/8/8/8 w - - 0 1", 
                new StandardMove(P("h4"), P("g5")),
                false,
                ""
            },
            {
            	"8/8/8/8/8/8/8/6NR w - - 0 1", 
                new StandardMove(P("h1"), P("g1")), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/7P/7R w - - 0 1", 
                new StandardMove(P("h1"), P("h2")), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/8/3q2PR w - - 0 1", 
                new StandardMove(P("h1"), P("d1")), 
                false, 
                ""
            },
            //King
            //true
            // 10
            {
            	"8/8/8/8/8/8/8/4K3 w - - 0 1", 
                new StandardMove(P("e1"), P("e2")), 
                true, 
                "8/8/8/8/8/8/4K3/8 b - - 1 1"
            },
            {
            	"8/8/8/8/8/8/5p2/4K3 w - - 0 1", 
                new StandardMove(P("e1"), P("f2")), 
                true, 
                "8/8/8/8/8/8/5K2/8 b - - 0 1"
            },
            {
            	"4k3/8/8/8/8/8/8/8 b - - 0 1", 
                new StandardMove(P("e8"), P("d8")), 
                true, 
                "3k4/8/8/8/8/8/8/8 w - - 1 2"
            },
            {
            	"4k3/8/8/8/8/8/8/8 b - - 0 1", 
                new StandardMove(P("e8"), P("e7")), 
                true, 
                "8/4k3/8/8/8/8/8/8 w - - 1 2"
            },
            //Rochade
            {
            	"8/8/8/8/8/8/8/4K2R w K - 0 1", 
                new CastlingMove(CastlingMove.W_KINGSIDE), 
                true, 
                "8/8/8/8/8/8/8/5RK1 b - - 1 1"
            },
            // 15
            {
            	"8/8/8/8/8/8/8/R3K3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE), 
                true, 
                "8/8/8/8/8/8/8/2KR4 b - - 1 1"
            },
            {
            	"4k2r/8/8/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                true, 
                "5rk1/8/8/8/8/8/8/8 w - - 1 2"
            },
            {
            	"3qk2r/8/8/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                true, 
                "3q1rk1/8/8/8/8/8/8/8 w - - 1 2"
            },
            {
            	"4k2r/8/8/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                true, 
                "5rk1/8/8/8/8/8/8/8 w - - 1 2"
            },
            {
            	"r3k3/8/8/8/8/8/8/8 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                true, 
                "2kr4/8/8/8/8/8/8/8 w - - 1 2"
            },
            // 20
            {
            	"r3kbnr/8/8/8/8/8/8/8 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                true, 
                "2kr1bnr/8/8/8/8/8/8/8 w - - 1 2"
            },
            
            //false
            {
            	"8/8/8/8/8/8/7r/4K3 w - - 0 1", 
                new StandardMove(P("e1"), P("e2")), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/8/4K3 w - - 0 1", 
                new StandardMove(P("e1"), P("e3")), 
                false, 
                ""
            },
            {
            	"8/8/8/1r6/K7/8/8/8 w - - 0 1", 
                new StandardMove(P("a4"), P("b4")), 
                false, 
                ""
            },
            {
            	"4k3/8/8/8/8/8/8/8 b - - 0 1", 
                new StandardMove(P("e8"), P("e6")), 
                false, 
                ""
            },
            // 25
            {
            	"4k3/7R/8/8/8/8/8/8 b - - 0 1", 
                new StandardMove(P("e8"), P("e7")), 
                false, 
                ""
            },
            {
            	"4k3/8/8/8/8/7B/8/8 b - - 0 1", 
                new StandardMove(P("e8"), P("d7")), 
                false, 
                ""
            },
            {
            	"4k3/8/8/8/8/8/8/5Q2 b - - 0 1", 
                new StandardMove(P("e8"), P("f7")), 
                false, 
                ""
            },
            {
            	"4k3/8/8/8/8/8/B7/8 b - - 0 1", 
                new StandardMove(P("e8"), P("f7")), 
                false, 
                ""
            },
            //Rochade(false)
            {
            	"8/8/8/8/8/8/8/4K1R1 w K - 0 1", 
                new StandardMove(P("e1"), P("g1")), 
                false, 
                ""
            },
            // 30
            {
            	"8/8/8/8/8/8/8/4K1NR w K - 0 1", 
                new CastlingMove(CastlingMove.W_KINGSIDE), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/8/4KB1R w K - 0 1", 
                 new CastlingMove(CastlingMove.W_KINGSIDE), 
                false,
                ""
            },
            {
            	"8/8/8/8/8/8/6q1/4K2R w K - 0 1", 
                new CastlingMove(CastlingMove.W_KINGSIDE), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/7p/4K2R w K - 0 1", 
                 new CastlingMove(CastlingMove.W_KINGSIDE), 
                 false, 
                 ""
            },
            {
            	"8/8/8/8/8/8/8/3RK3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE), 
                false, 
                ""
            },
            // 35
            {
            	"8/8/8/8/8/8/8/RN2K3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/8/R1B1K3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/1p6/R3K3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/8/2q5/R3K3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE), 
                false, 
                ""
            },
            {
            	"8/8/7b/8/8/8/8/R3K3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE), 
                false, 
                ""
            },
            // 40
            {
            	"2r5/8/8/8/8/8/8/R3K3 w Q - 0 1", 
                new CastlingMove(CastlingMove.W_QUEENSIDE),
                false, 
                ""
            },
            {
            	"4k1nr/8/8/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                false, 
                ""
            },
            {
            	"4kb1r/8/8/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                false, 
                ""
            },
            {
            	"4kbnr/8/8/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                false, 
                ""
            },
            {
            	"4k2r/7P/8/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE),
                false, 
                ""
            },
            // 45
            {
            	"4k2r/8/8/8/8/8/B7/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE),
                false, 
                ""
            },
            {
            	"4k2r/8/8/8/8/B7/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                false, 
                ""
            },
            {
            	"4k2r/8/8/8/8/8/8/5R2 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                false,
                ""
            },
            {
            	"4k2r/8/7Q/8/8/8/8/8 b k - 0 1", 
                new CastlingMove(CastlingMove.B_KINGSIDE), 
                false,
                ""
            },
            {
            	"r2qk3/8/8/8/8/8/8/8 b q - 0 1",
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                false, 
                ""
            },
            // 50
            {
            	"rn2k3/8/8/8/8/8/8/8 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                false,
                ""
            },
            {
            	"r3k3/1P6/8/8/8/8/8/8 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                false, 
                ""
            },
            {
            	"r3k3/8/8/8/8/7B/8/8 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                false, 
                ""
            },
            {
            	"r3k3/8/8/8/8/8/8/3Q4 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                false,
                ""
            },
            {
            	"r3k3/8/3N4/8/8/8/8/8 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                false, 
                ""
            },
            // 55
            {
            	"r3k3/5N2/8/8/8/8/8/8 b q - 0 1", 
                new CastlingMove(CastlingMove.B_QUEENSIDE), 
                false,
                ""
            },
            //Bishop
            
            //true
            {
            	"8/8/8/8/8/8/8/5B2 w - - 0 1", 
                new StandardMove(P("f1"), P("d3")), 
                true, 
                "8/8/8/8/8/3B4/8/8 b - - 1 1"
            },
            {
            	"8/8/8/8/8/3q4/8/5B2 w - - 0 1", 
                new StandardMove(P("f1"), P("d3")), 
                true, 
                "8/8/8/8/8/3B4/8/8 b - - 0 1"
            },
            {
            	"8/8/8/8/8/8/8/B7 w - - 0 1", 
                new StandardMove(P("a1"), P("h8")), 
                true, 
                "7B/8/8/8/8/8/8/8 b - - 1 1"
            },           
            {
        	  	"8/8/8/8/3r4/2B5/8/8 w - - 0 1", 
                new StandardMove(P("c3"), P("d4")), 
                true, 
                "8/8/8/8/3B4/8/8/8 b - - 0 1"
            },
            // 60
            {
        	  	"8/6n1/8/8/8/8/8/B7 w - - 0 1", 
                new StandardMove(P("a1"), P("g7")), 
                true, 
                "8/6B1/8/8/8/8/8/8 b - - 0 1"
            },
            //false
            {
            	"8/8/8/8/8/5B2/8/8 w - - 0 1", 
                new StandardMove(P("f1"), P("f2")), 
                false, 
                ""
            },
            {
            	"8/8/8/8/8/2R5/8/B7 w - - 0 1", 
                new StandardMove(P("a1"), P("d4")), 
                false, 
                ""	
            },
            //en passant
            //true
            {
            	"8/8/8/8/Pp6/8/8/8 b - a3 0 1", 
                new EnPassantMove(P("b4"), P("a3")), 
                true, 
                "8/8/8/8/8/p7/8/8 w - - 0 2"
            },
            {
            	"8/8/8/8/3pP3/8/8/8 b - e3 0 1", 
                new EnPassantMove(P("d4"), P("e3")), 
                true, 
                "8/8/8/8/8/4p3/8/8 w - - 0 2"
            },
            // 65
            {
            	"8/8/8/8/6pP/8/8/8 b - h3 0 1", 
                new EnPassantMove(P("g4"), P("h3")), 
                true, 
                "8/8/8/8/8/7p/8/8 w - - 0 2"
            },
            {
            	"8/8/8/pP6/8/8/8/8 w - a6 0 1", 
                new EnPassantMove(P("b5"), P("a6")), 
                true, 
                "8/8/P7/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/3Pp3/8/8/8/8 w - e6 0 1", 
                new EnPassantMove(P("d5"), P("e6")), 
                true, 
                "8/8/4P3/8/8/8/8/8 b - - 0 1"
            },
            {
            	"8/8/8/5pP1/8/8/8/8 w - f6 0 1", 
                new EnPassantMove(P("g5"), P("f6")), 
                true, 
                "8/8/5P2/8/8/8/8/8 b - - 0 1"
            }
            //false
            
          	});
           

    }

    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public ParameterizedMoveTest(String startPos, Move mv, boolean isPossible, String expected) {
    	this.start = Board.B(startPos); 
    	this.expected = (isPossible ? builder.fromFenString(expected)
                                    : null);
    	this.mv = mv;
    	this.isPossible = isPossible;
    }

/* FIXTURES FÃœR DIESEN TESTFALL */

    // ====================================================
    // = Ausnahmetests
    // ====================================================

    @Test(expected = MoveException.class)
    public void testIllegalMove() {
        assumeFalse(this.isPossible);
        mv.execute(start);
    }

    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testExecute() {	
    	assumeTrue(this.isPossible);
    	
        Board result = mv.execute(start);
        assertNotNull( "Resultierende Stellung ist <null>",
                       result);
    	assertEquals ( "Falsche Stellung erhalten!",
    			this.expected,
    			result);
    	
    }

    @Test public void testPossible() {
        assertEquals("Ausführbarkeit falsch erkannt!",
                this.isPossible,
                mv.isPossible(start) );
    }

}


