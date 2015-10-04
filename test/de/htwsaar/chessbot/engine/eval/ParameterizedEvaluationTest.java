package de.htwsaar.chessbot.engine.eval;

import de.htwsaar.chessbot.engine.eval.PawnEvaluator;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardBuilder;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Testklasse für Bewertungsfunktion
 * @author Dominik Becker
 *
 */
@RunWith(Parameterized.class)
public class ParameterizedEvaluationTest {

	private static BoardBuilder builder = Board.BUILDER;
	private Board board;
	private static PawnEvaluator pEval = new PawnEvaluator();
	private int expected;
	@Parameters
	public static Collection<Object[]> getTestData() {
		return Arrays.asList(new Object[][] {
			{
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKNBR w QKqk - 0 1",
				0,
			},
			{
				"rnbqkbnr/pppppppp/8/8/8/3P4/PPP1PPPP/RNBQKNBR w QKqk - 0 1",
				8,
			},
			{
				"rnbqkbnr/p3ppp1/8/2p3P1/8/3P4/PPP1PP1P/RNBQKNBR w QKqk - 0 1",
				322,
			},
			{
				"rnbqkbnr/p1pppppp/8/8/PR6/8/2PPPPPP/1NBQKNBR w QKqk - 0 1",
				15,
			},
			{
				"8/8/1p1P1p2/2PpP3/4R3/8/7r/8 w - - 0 1",
				33,
			},
			{
				"8/1R6/1p3p2/2pP1p2/2P1P3/3Pr3/8/8 w - - 0 1",
				96,
			},
			{
				"8/8/p3p3/3p3P/8/2P4P/r7/7R w - - 0 1",
				-80,
			}
		});
	}
	
	public ParameterizedEvaluationTest(String fen, int expected) {
		this.board = Board.B(fen);
		this.expected = expected;
	}
	
	
	//====================
	//= Funktionstests
	//====================
	
	@Test
	public void testEvaluate() {
		int value = pEval.evaluate(board);
		System.out.println("Malus: "+value);
		assertEquals("Bewertung fehlgeschlagen!",
				this.expected,
				value);
	}

}
