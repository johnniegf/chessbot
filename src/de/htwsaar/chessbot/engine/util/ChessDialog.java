package de.htwsaar.chessbot.engine.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.htwsaar.chessbot.util.Input;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardBuilder;
import de.htwsaar.chessbot.engine.Engine;
import de.htwsaar.chessbot.engine.Game;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.eval.PawnEvaluator;

public class ChessDialog {
	//Konstanten
	private static final String NEWGAME = "ucinewgame";
	private static final String NEWMOVE = "position";
	private static final String GO = "go";
	private static final String QUIT = "quit";
	private static final String EVALUATE = "evaluate";
	private static final String MOVE = "([a-h][1-8]){2}";
	
    private final Input mInput = new Input();
	
	private Engine engine;
	private PawnEvaluator pEval = new PawnEvaluator();
	private Game game;

	public ChessDialog() {
		this.game = new Game("8/6R1/1R1p4/r7/8/8/P2R2r1/8 w - - 0 1");
	}
	
	private void exeCmd(String cmd) {
		String[] cmds = cmd.split(" ");
		for(int i = 0; i < cmds.length; i++) {
			switch(cmds[i]) {
			case NEWGAME:
				ucinewgame();
				break;
			case NEWMOVE:
				String[] split = cmd.split("position ");
				position(split[1]);
				return;
			case EVALUATE:
				int value = pEval.evaluate(game.getCurrentBoard());
				System.out.println("Bewertung: "+value);
				break;
			case GO:
			case QUIT:
				System.out.println("Spiel wurde beendet.");
			
			default	: ;
		}
		
		}
	}
	
	private void ucinewgame() {
		engine.newGame();
	}
	
	private void position(String position) {
		String[] cmds = position.split(" ");
		List<Move> moves = new ArrayList<Move>();
		if(cmds[0].equals("moves")){
			for(int i = 1; i < cmds.length; i++) {
			}
		}
		if(cmds[0].equals("startpos")) {
			//engine.resetBoard(moves);
		} else if(cmds[0].equals("fen")){
			//engine.setBoard(cmds[1], moves);
		}
	}
	
	private String choseCmd() {
		System.out.println("Kommando wahlen: \n----------------\n");
		System.out.println("fuehre Zug aus: " + NEWMOVE + ";\n"+
						   "neues Spiel: "+ NEWGAME+";\n"+
						   "Stellung bwerten: "+EVALUATE+";\n"+
						   "beenden: " + QUIT +";\n"+
						   "-> ");
		return mInput.readLine().toString();
	}
	
	private void start(){
		String cmd = "";
		while(!cmd.equals(QUIT)) {
			cmd = choseCmd();
			exeCmd(cmd);
		}
	}
	
	public static void main(String[] args) {
		ChessDialog cd = new ChessDialog();
		cd.start();
	}
}
