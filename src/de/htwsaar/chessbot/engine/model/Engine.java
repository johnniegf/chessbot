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
	
	public Engine() {
		//AlphaBeta (GameTree) erzeugen & starten
		this.game = new Game();
		//UCIInterface erzeugen & starten
	}
	
	//======================================
	//= uci
	//======================================
	
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
	}
	
	
	//======================================
	//= position
	//======================================
	/*
	 * setzt das Spiel auf die Startstellung
	 * fuehrt Zuege aus falls vorhanden.
	 */
	public void resetBoard(List<Move> moves) {
		this.game = new Game();
			executeMoves(moves);
	}
	
	/*
	 * erzeugt eine Stellung auf Grund des fens
	 * fuehrt die uebergegebenen Zuege aus falls vorhanden.
	 */
	public void setBoard(String fen, List<Move> moves) {
		this.game= new Game(fen);
		executeMoves(moves);
	}
	
	/**
	 * fuehrt die uebergebenen Zuege aus.
	 * @param moves
	 */
	public void executeMoves(List<Move> moves) {
		for(int i = 0; i < moves.size(); i++) {
			game.doMove(moves.get(i).toString());
		}
	}
	
	//========================================
	//= go
	//========================================
	
	public void searchmoves(List<Move> moves) {
		//TODO ruft AlphaBetaSearch auf
	}
	
	
	//========================================
	//= stop
	//========================================
	
	public void stop() {
		//TODO bestmove variable hinzufügen 
		//TODO suche unterbrechen.
		System.out.println("bestmove: \n");
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
