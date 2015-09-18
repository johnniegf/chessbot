package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.engine.model.Position.P;
import static de.htwsaar.chessbot.engine.model.Board.B;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MovePromotionTest {

	private static final String testPositionFen1 =
		"8/P3P2P/8/4P3/8/8/p1p4p/8 w - - 0 1";
	private static final String tetsPositionFen2 =
		"1r6/P7/8/8/8/8/7p/6N1 w - - 0 1";
	
	
	private Board board1;
	private Board board2;
	
	@Before
	public void setUp() throws Exception {
		board1 = B(testPositionFen1);
		board2 = B(tetsPositionFen2);
		
	}

	@Test
	public void testPromotionMove() {
		Board expected = B("Q7/4P2P/8/4P3/8/8/p1p4p/8 b - - 0 1");
		MovePromotion mQ = new MovePromotion (P("a7"),P("a8"), new Queen());
		Board board = board1.clone();
		assertTrue("",
				mQ.isPossible(board));
		Board actual = mQ.execute(board);
		assertEquals("",
				expected,
				actual);
	}
	
	@Test
	public void testPromotionTake() {
		Board expected = B("1N6/8/8/8/8/8/7p/6N1 b - - 0 1");
		MovePromotion mN = new MovePromotion(P("a7"),P("b8"), new Knight());
		assertTrue("",
				mN.isPossible(board2));
		Board actual = mN.execute(board2);
		assertEquals("",
				expected,
				actual);
	}
	
	@Test(expected = MoveException.class)
	public void testPositionError() {
		MovePromotion mE = new MovePromotion (P("e5"),P("e6"), new Queen());
		mE.execute(board1);
	}
	@Test(expected = MoveException.class)
	public void testKingPromotionError() {
		MovePromotion mE = new MovePromotion (P("h7"),P("h8"), new King());
		mE.execute(board1);
	}
	@Test(expected = MoveException.class)
	public void testPawnPromotionError() {
		MovePromotion mE = new MovePromotion (P("h7"),P("h8"), new Pawn());
		mE.execute(board1);
	}

}
