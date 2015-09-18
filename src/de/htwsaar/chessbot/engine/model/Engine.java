<<<<<<< HEAD
package de.htwsaar.chessbot.engine.model;

import java.util.List;

/**
 * Enthaelt Main-Methode des Programms.
 * erzeugt Game,UCI und AlphaBetaSearch-Objekt.
 * bekommt verarbeitete UCI-Kommandos vom Parser gesendet und fuehrt diese aus.
 * 
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */

public class Engine {

	private Game game;
	private AlphaBetaSearch moveSearcher;
	private UCI uci;
	
	/**
	 * Konstruktor 
	 * erzeugt Game,UCI und AlphaBetaSearch
	 */
	public Engine() {
		this.game = new Game();
		moveSearcher = new AlphaBetaSearch(game);
		moveSearcher.setTimeLimit(5000);
		uci = new UCI(this);
	}
	
	//======================================
	//= uci
	//======================================
	
	/**
	 * gibt das aktuelle Spiel zurueck.
	 * @return game
	 */
	public Game getGame() {
		return this.game;
	}
	
	/**
	 * gibt den aktuellen AlphaBetaSearcher zurueck.
	 * @return moveSearcher
	 */
	public AlphaBetaSearch getSearcher() {
		return moveSearcher;
	}
	
	/**
	 * wird aufgerufen, wenn von der GUI "uci" gesendet wird. 
	 * gibt den Namen, Author und uciok auf der Standardausgabe aus
	 */
	public void uci() {
		System.out.println("id name chessbot\n");
		System.out.println("id author grpKretschmer\n");
		System.out.println("uciok\n");
	}
	
	//======================================
	//= isready
	//======================================
	
	//wird aufgerufen, wenn von der GUI "isready" gesendet wird.
	public void isready() {
		System.out.println("readyok\n");
	}
	
	//======================================
	//= newucigame
	//======================================
	
	/*
	 * wird aufgerufen, wenn von der GUI "ucinewgame" gesendet wird.
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
	
	/**
	 * AlphaBetaSearch sucht bis zur mitgegebenen Tiefe.
	 * @param depth
	 */
	public void search(int depth) {
		moveSearcher.resetLimitMoveList();
		moveSearcher.setMaxSearchDepth(depth);
		moveSearcher.run();
	}
	
	/**
	 * sucht bis zur mitgegebenen Tiefe.
	 * sucht nur die mitgegebenen Zuege durch.
	 * @param moves
	 * @param depth
	 */
	public void searchmoves(List<Move> moves, int  depth) {
		moveSearcher.setMaxSearchDepth(depth);
		moveSearcher.setLimitedMoveList(moves);
		moveSearcher.run();
	}
	
	//========================================
	//= stop
	//========================================
	
	/**
	 * stoppt die Suche.
	 */
	public void stop() {
		moveSearcher.stop();
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
=======
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
		moveSearcher.start();
		uci = new UCI(this);
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
	//= newucigame
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
>>>>>>> 071513c10ff634bb62c7f09d93703170917c5dac
