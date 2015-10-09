package de.htwsaar.chessbot.engine;

import java.io.IOException;
import java.util.List;

import de.htwsaar.chessbot.engine.config.Config;
import de.htwsaar.chessbot.engine.eval.Evaluator;
import de.htwsaar.chessbot.engine.io.Logger;
import de.htwsaar.chessbot.engine.io.UCI;
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.search.AlphaBetaSearcher;

/**
 * 
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */

public class Engine {

	private Game game;
	private final AlphaBetaSearcher moveSearcher;
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
		moveSearcher = new AlphaBetaSearcher(game.getCurrentBoard(), new Evaluator());
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
	
	public AlphaBetaSearcher getSearcher() {
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
		this.moveSearcher.setBoard(game.getCurrentBoard());
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
		this.moveSearcher.setBoard(game.getCurrentBoard());
		executeMoves(moves);
	}
	
	/*
	 * erzeugt eine Stellung auf Grund des fens
	 * fuehrt die uebergegebenen Zuege aus falls vorhanden.
	 */
	public void setBoard(String fen, List<String> moves) {
		this.game = new Game(fen);
		this.moveSearcher.setBoard(game.getCurrentBoard());
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
		moveSearcher.setDepth(depth);
		moveSearcher.setBoard(game.getCurrentBoard());
		moveSearcher.go();
	}
	
	public void searchmoves(List<Move> moves, int  depth) {
		search(depth);
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
		stop();
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

	public UCI getUCI() {
		return this.uci;
	}
	
	
	
}
