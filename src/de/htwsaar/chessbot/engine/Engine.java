package de.htwsaar.chessbot.engine;

import de.htwsaar.chessbot.engine.config.Config;
import de.htwsaar.chessbot.engine.io.UCI;
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.io.Logger;
import de.htwsaar.chessbot.engine.model.move.Move;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */

public class Engine {

	private Game game;
	private final AlphaBetaSearch moveSearcher;
	private UCI uci;
	
	public Engine() {
		//Initialize Engine
		Thread.currentThread().setName("Engine");
		
		//Initialize Game
		this.game = new Game();
		
		//Initialize Config
		Config.getInstance().init();
		
		//Initialize UCISender
		UCISender.getInstance().sendDebug("Initialized UCISender");
		
		//Initialize AlphaBeta
		moveSearcher = new AlphaBetaSearch(game);
		moveSearcher.start();
		
		//Initialize UCI-Protocoll
		this.uci = new UCI(this);
		this.uci.initialize();
		try {
			this.uci.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//======================================
	//= uci
	//======================================
	
	public Game getGame() {
		return this.game;
	}
	
	public AlphaBetaSearch getSearcher() {
		return moveSearcher;
	}
	
	public void uci() {
		System.out.println("id name chessbot\n");
		System.out.println("id author grpKretschmer\n");
		System.out.println("uciok\n");
	}
	
	//======================================
	//= isready
	//======================================
	
	public void isready() {
		System.out.println("readyok\n");
	}
	
	//======================================
	//= ucinewgame
	//======================================
	/*
	 * erstellt ein neues Spiel.
	 */
	public void newGame() {
		this.game = new Game();
		this.moveSearcher.setGame(game);
	}
	
	
	//======================================
	//= position
	//======================================
	/*
	 * setzt das Spiel auf die Startstellung
	 * fuehrt Zuege aus falls vorhanden.
	 */
	public void resetBoard(List<String> moves) {
		this.game = new Game();
		this.moveSearcher.setGame(game);
		executeMoves(moves);
	}
	
	/*
	 * erzeugt eine Stellung auf Grund des fens
	 * fuehrt die uebergegebenen Zuege aus falls vorhanden.
	 */
	public void setBoard(String fen, List<String> moves) {
		this.game = new Game(fen);
		this.moveSearcher.setGame(game);
		executeMoves(moves);
	}
	
	/**
	 * fuehrt die uebergebenen Zuege aus.
	 * @param moves
	 */
	public void executeMoves(List<String> moves) {
		for(int i = 0; i < moves.size(); i++) {
			game.doMove(moves.get(i).toString());
		}
	}
	
	//========================================
	//= go
	//========================================
	
	public void search(int depth) {
		moveSearcher.resetLimitMoveList();
		moveSearcher.setMaxSearchDepth(depth);
		moveSearcher.startSearch();
	}
	
	public void searchmoves(List<Move> moves, int  depth) {
		moveSearcher.setMaxSearchDepth(depth);
		moveSearcher.setLimitedMoveList(moves);
		moveSearcher.startSearch();
	}
	
	//========================================
	//= stop
	//========================================
	
	public void stop() {
		moveSearcher.stopSearch();
	}
	
	
	//========================================
	//= quit
	//========================================
	
	/**
	 * beendet das Programm
	 */
	public void quit() {
		moveSearcher.interrupt();
		Logger.getInstance().close();
		System.exit(0);
	}
	
	/*
	 * main-Methode der Engine.
	 */
	public static void main(String[] args) {
		//UCIManager anlegen
		new Engine();
	}
	
	
	
}
