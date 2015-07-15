package de.htwsaar.chessbot.engine.model;

import java.util.ArrayList;

/**
 * Diese Klasse kapselt eine Liste, die den Zugverlauf
 * einer Partie abbildet.
 * 
 * @author David Holzapfel
 *
 */
public class MoveHistory {

	private ArrayList<Move> moveList;
	
	public MoveHistory() {
		this.moveList = new ArrayList<Move>();
	}
	
	/**
	 * Fuegt einen Zug am Ende der Liste ein.
	 * 
	 * @param move Einzufuegender Zug
	 */
	public void addMove(Move move) {
		this.moveList.add(move);
	}
	
	/**
	 * @param index Indexposition des gesuchten Zuges
	 * @return	Zug an der angegebenen Position
	 */
	public Move getMove(int index) {
		if(index >= getSize()) {
			throw new IndexOutOfBoundsException();
		}
		
		return this.moveList.get(index);
	}
	
	/**
	 * @return Den zuletzt ausgefuerten Zug
	 */
	public Move getLatestMove() {
		return this.moveList.get(getSize() - 1);
	}
	
	/**
	 * Ermittelt aus dem zuletzt ausgefuerhten Zug, ob Weiﬂ am
	 * Zug ist.
	 * 
	 * @return Ob Weiﬂ am Zug ist
	 */
	public boolean whiteMoves() {
		if(this.moveList.isEmpty()) {
			return true;
		}
		else {
			return !getLatestMove().getPiece().isWhite();
		}
	}
	
	/**
	 * @return Anzahl der Zuege
	 */
	public int getSize() {
		return this.moveList.size();
	}
	
	/**
	 * Fuehrt alle Zuege auf dem Spielbrett aus.
	 * 
	 * @param onBoard	Das Spielbrett, auf dem die Zuege ausgefuehrt werden
	 * @return	Das endgueltige Spielbrett
	 */
	public Board executeMoves(Board onBoard) {
		if(onBoard == null) {
			throw new IllegalArgumentException();
		}
		
		Board board = onBoard.clone();
		for(int i = 0; i < getSize(); i++) {
			board = this.moveList.get(i).execute(board);
		}
		
		return board;
	}
	
}
