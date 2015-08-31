package de.htwsaar.chessbot.engine.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.htwsaar.chessbot.util.Input;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardBuilder;
import de.htwsaar.chessbot.engine.model.Engine;
import de.htwsaar.chessbot.engine.model.Game;

public class ChessDialog {
	//Konstanten
	private static final int MOVE = 1;
	private static final String NEWGAME = "ucinewgame";
	private static final String NEWMOVE = "position";
	private static final String GO = "go";
	private static final String QUIT = "quit";
	
    private final Input mInput = new Input();
	
	private static BoardBuilder builder = Board.BUILDER;
	private Engine engine;

	public ChessDialog() {
		this.engine = new Engine();
	}
	
	private void exeCmd(String cmd) {
		String[] cmds = cmd.split(" ");
		for(int i = 0; i < cmds.length; i++) {
			switch(cmds[i]) {
			case NEWGAME:
				engine.ucinewgame();
				break;
			case NEWMOVE:
				engine.position(cmds, i);
				return;
			case GO:
				engine.go(cmds, i);
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
						   "go: " + GO+";\n"+
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
