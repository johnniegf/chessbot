package de.htwsaar.chessbot.engine.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.htwsaar.chessbot.util.Input;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardBuilder;
import de.htwsaar.chessbot.engine.model.Game;

public class ChessDialog {
	//Konstanten
	private static final int MOVE = 1;
	private static final String NEWGAME = "ucinewgame";
	private static final String NEWMOVE = "position";
	private static final String QUIT = "quit";
	
    private final Input mInput = new Input();
	
	private static BoardBuilder builder = Board.BUILDER;
	private Game game;

	public ChessDialog() {
		this.game = new Game();
	}
	
	private void exeCmd(String cmd) {
		String[] cmds = cmd.split(" ");
		for(int i = 0; i < cmds.length; i++) {
			switch(cmds[i]) {
			case NEWGAME:
				this.game = new Game();
				break;
			case NEWMOVE:
				List<String> moves;
				if(cmds[i+1].equals("moves")) {
					moves = new ArrayList<String>();
					for(int j = i+2; j < cmds.length; j++) {
						moves.add(cmds[j]);
					}
					move(moves);
				}
				else
				if(cmds[i+1].equals("startpos")) {
					this.game = new Game();
					moves = new ArrayList<String>();
					for(int j = i+3; j < cmds.length; j++) {
						moves.add(cmds[j]);
					}
					move(moves);
				} else {
					this.game = new Game(cmds[i+2]);
					moves = new ArrayList<String>();
					for(int j = i+4; j < cmds.length; j++) {
						moves.add(cmds[j]);
					}
					move(moves);
				}
				return;
			case QUIT:
				System.out.println("Spiel wurde beendet.");
			
			default	: ;
		}
		
		}
	}
	
	private String choseCmd() {
		System.out.println("Kommando wahlen: \n----------------\n");
		System.out.println("fuehre Zug aus: " + NEWMOVE + ";\n"+
						   "neues Spiel: "+ NEWGAME+";\n"+
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
	
	private void move(List<String> moves) {
		for(int i = 0; i < moves.size(); i++) {
			game.doMove(moves.get(i));
		}
		System.out.println(game.getCurrentBoard()+"\n\n\n");
	}
	
	public static void main(String[] args) {
		ChessDialog cd = new ChessDialog();
		cd.start();
	}
}
