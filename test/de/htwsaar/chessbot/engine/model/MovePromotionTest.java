package de.htwsaar.chessbot.engine.model;

// Interne Referenzen
import static de.htwsaar.chessbot.engine.model.Position.*;

// Java-API
import java.util.*;

// JUnit-API
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.*;
import static org.junit.Assert.*;
import org.junit.*;

// Klassen für parametrierte Tests
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

/**
* Testklasse für ...
*
* @author
*/
@RunWith(Parameterized.class)
public class MovePromotionTest { 

    @Parameters
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][] { 
            {
            	new MovePromotion(new Pawn(P("e7")), P("e8"), new Queen()),
            	new Board(),
            	true
            },
            {
            	new MovePromotion(new Pawn(P("d2"), false), P("d1"), new Bishop()),
            	new Board(),
            	true
            }
        });

    }
    
    private Move promotion;
    private boolean isPossible;
    private Board board;
    /**
    * Testaufbau.
    *
    * Allokiert und initialisiert Ressourcen und Testvariablen.
    */
    public MovePromotionTest(Move promotion, Board board, boolean isPossible) {
    	this.promotion = promotion;
    	this.board = board;
    	this.isPossible = isPossible;
    }

/* FIXTURES FÜR DIESEN TESTFALL */

    // ====================================================
    // = Ausnahmetests
    // ====================================================
/*
    @Test(expected = MyException.class)
    public void test...() {
        // Fehlerhafte Anweisung, die MyException auslöst
    }
*/
    // ====================================================
    // = Funktionstests
    // ====================================================

    @Test public void testIsPossible() {
    	assertEquals("",)
    				 isPossible,
    				 promotion.isPossible(board))
    }

}



