package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.Position.P;
import static de.htwsaar.chessbot.engine.model.Board.B;
import de.htwsaar.chessbot.engine.model.piece.Queen;
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
		PromotionMove mQ = new PromotionMove(P("a7"),P("a8"), 
                                             PromotionMove.TO_QUEEN);
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
		PromotionMove mN = new PromotionMove(P("a7"),P("b8"), 
                                             PromotionMove.TO_KNIGHT);
		assertTrue("",
				mN.isPossible(board2));
		Board actual = mN.execute(board2);
		assertEquals("",
				expected,
				actual);
	}
	
	@Test(expected = MoveException.class)
	public void testPositionError() {
		PromotionMove mE = new PromotionMove (P("e5"),P("e6"), PromotionMove.TO_QUEEN);
		mE.execute(board1);
	}
	@Test(expected = MoveException.class)
	public void testKingPromotionError() {
		PromotionMove mE = new PromotionMove (P("h7"),P("h8"), (byte) 20);
		mE.execute(board1);
	}

}
