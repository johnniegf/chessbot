package de.htwsaar.chessbot.engine.model;

import java.util.List;

/**
 * 
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */

public class Engine {

	private Game game;
	private AlphaBetaSearch moveSearcher;
	private UCI uci;
	
	public Engine() {
		this.game = new Game();
		moveSearcher = new AlphaBetaSearch(game);
		uci = new UCI(this);
	}
	
	//======================================
	//= uci
	//======================================
	
	public Game getGame() {
		return this.game;
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
	//= newucigame
	//======================================
	/*
	 * erstellt ein neues Spiel.
	 */
	public void newGame() {
		this.game = new Game();
		this.moveSearcher = new AlphaBetaSearch(game);
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
		this.moveSearcher = new AlphaBetaSearch(game);
			executeMoves(moves);
	}
	
	/*
	 * erzeugt eine Stellung auf Grund des fens
	 * fuehrt die uebergegebenen Zuege aus falls vorhanden.
	 */
	public void setBoard(String fen, List<String> moves) {
		this.game = new Game(fen);
		this.moveSearcher = new AlphaBetaSearch(game);
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
		//System.out.println(game.getCurrentBoard());
		moveSearcher.setMaxSearchDepth(depth);
		moveSearcher.run();
	}
	
	
	//========================================
	//= stop
	//========================================
	
	public void stop() {
		moveSearcher.stop();
		//System.out.println("bestmove: "+moveSearcher.getCurrentBestMove());
	}
	
	
	//========================================
	//= quit
	//========================================
	
	/**
	 * beendet das Programm
	 */
	public void quit() {
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
