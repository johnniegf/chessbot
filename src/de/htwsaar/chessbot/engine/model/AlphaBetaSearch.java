package de.htwsaar.chessbot.engine.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.htwsaar.chessbot.engine.model.GameTree.Node;

/**
 * 
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */

public class AlphaBetaSearch implements Runnable {

	public static void main(String[] args) throws IOException {
		Game game = new Game(
				//"8/2p1pp2/8/4k3/8/1Q6/PPP4P/RN6 b - - 0 1"
				);

		AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(game);
		alphaBetaSearch.setMaxSearchDepth(100000);
		alphaBetaSearch.setTimeLimit(2500);
		new Thread(alphaBetaSearch).start();
	}

	private Game game;
	private GameTree gameTree;
	private final EvaluationFunction evalFunc = new PositionBasedEvaluator();

	private int maxSearchDepth;
	private int maxTime = 0;
	private Collection<Move> limitedMoveList = null;

	private long startTime = 0;
	private long nodesSearched = 0;
	private double nodesPerSecond = 0;
	private volatile boolean exitSearch = false;

	private volatile Move currentBestMove;
	private int currentBestScore = 0;
	private int currentMoveNumber = 0;
	private int currentSearchDepth = 0;

	/**
	 * Erstellt einen BestMove-Sucher der mit AlphaBeta Search arbeitet.
	 * 
	 * @param game  GameState
	 */
	public AlphaBetaSearch(Game game) {
		this.game = game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public void run() {
		this.startTime = System.currentTimeMillis();
		startAlphaBeta();
		sendBestMove();

	}

	public void stop() {
		this.setExitSearch(true);
	}

	/**
	 * Setzt die maximale Suchtiefe für den Baum.
	 * 
	 * @param depth
	 */
	public synchronized void setMaxSearchDepth(int depth) {
		this.maxSearchDepth = depth;
	}

	/**
	 * Setzt der Suche ein Zeitlimit, nach dem automatisch abgebrochen wird.
	 * 
	 * @param millis	Millisekunden bis zum Abbruch
	 */
	public void setTimeLimit(int millis) {
		this.maxTime = millis;
	}

	/**
	 * Gibt dem Algorithmus eine Liste von Zuegen vor, die ausschließlich
	 * untersucht werden soll.
	 * 
	 * @param moveList
	 */
	public void setLimitedMoveList(Collection<Move> moveList) {
		this.limitedMoveList = moveList;
	}
	
	/**
	 * Hebt die Zuglistenbeschraenkung auf.
	 */
	public void resetLimitMoveList() {
		this.limitedMoveList = null;
	}

	/**
	 * Setzt eine Flag, die den Algorithmus dazu bewegt, so bald
	 * wie moeglich abzubrechen.
	 * 
	 * @param exitSearch
	 */
	public synchronized void setExitSearch(boolean exitSearch) {
		this.exitSearch = exitSearch;
	}

	/**
	 * Gibt den zum jetztigen Zeitpunkt besten Zug zurueck, der
	 * ermittelt wurde.
	 * 
	 * @return
	 */
	public synchronized Move getCurrentBestMove() {
		return this.currentBestMove;
	}

	//Setzt den bisher besten Zug
	private synchronized void bestMove(Move move, int score) {
		this.currentBestMove = move;
		this.currentBestScore = score;
	}

	//Sendet Informationen ueber den Zustand der Suche an die GUI
	private void sendInfo(Move currentMove) {
		long timeSpent = System.currentTimeMillis() - this.startTime;
		System.out.println(
				"info currmove " + currentMove +
				" currmovenumber " + this.currentMoveNumber +
				" depth " + this.currentSearchDepth +
				" time " + timeSpent +
				" nps " + (int)this.nodesPerSecond +
				" nodes " +  this.nodesSearched
				//" score cp " + this.currentBestScore
				);
	}

	//Sendet den "besten" Zug nachdem die Suche beendet wurde
	private void sendBestMove() {
		System.out.println("bestmove " + this.getCurrentBestMove().toString());
	}

	//Startet die Suche
	private void startAlphaBeta() {
		this.currentBestMove = null;
		this.exitSearch = false;
		this.gameTree = new GameTree(this.evalFunc, this.game.getCurrentBoard());
		this.nodesSearched = 0;

		for(int i = 1; i <= maxSearchDepth && !this.exitSearch; i++) { 
			this.currentSearchDepth = i;
			this.currentMoveNumber = 0;
			alphaBeta(i, gameTree.getRoot(), true,
					Integer.MIN_VALUE, Integer.MAX_VALUE);

			this.nodesPerSecond = 
					(1000d * this.nodesSearched) / (System.currentTimeMillis() - this.startTime);
		}
	}

	//Rekursion
	private void alphaBeta(final int depth, Node currentNode,
			boolean isMax, int alpha, int beta) {

		//Zeitlimit ueberschritten -> abbrechen
		if(this.maxTime > 0) {
			if(System.currentTimeMillis() - this.startTime >= this.maxTime) {
				this.exitSearch = true;
			}
		}

		//Abbruchbedingung erfuellt -> zurueckgeben
		if(this.exitSearch) {
			return;
		}

		//Zaehler der untersuchten Knoten erhoehen
		this.nodesSearched++;

		//Falls Blatt wird die Stellung bewertet
		if(depth == 0) {
			currentNode.setScore(
					gameTree.getEvaluationFunction().evaluate(currentNode.getBoard()));
		} else {
			//Andernfalls Min-Maxing der Kindknoten
			if(isMax) {
				currentNode.setScore(Integer.MIN_VALUE);
			} else {
				currentNode.setScore(Integer.MAX_VALUE);
			}

			List<Move> moveList;
			//Falls in der Wurzel und beschraenkte MoveList, wird diese genommen statt
			//der Liste aller moeglichen Zuege
			if(currentNode == gameTree.getRoot() && this.limitedMoveList != null) {
				moveList = new ArrayList<Move>();
				moveList.addAll(limitedMoveList);
			} else {
				moveList = (List<Move>) currentNode.getBoard().getMoveList();
			}
			//Kindknoten der Wurzel werden gemischt, damit im Falle der Gleichwertigkeit
			//Nicht immer der erste Zug genommen wird
			if(currentNode == gameTree.getRoot()) {
				Collections.shuffle(moveList);
			}
			//Fuer jeden Zug aus der MoveList den Kindknoten untersuchen
			for(Move move : moveList) {
				//Falls Abbruchbedingung erfuellt ist, wird Schleife verlassen
				if(this.exitSearch) {
					break;
				}
				if(currentNode == gameTree.getRoot()) {
					currentMoveNumber++;
				}
				if(this.currentBestMove == null && currentNode == gameTree.getRoot()) {
					this.currentBestMove = move;
				}

				//Stellung erzeugen und Zobrist-Hash errechnen
				Board board = move.execute(currentNode.getBoard());
				long hash = board.hash();
				Node childNode = null;
				for(Node n : currentNode.getChildren()) {
					if(n.getBoard().equals(board)) {
						childNode = n;
						break;
					}
				}
				if(childNode == null) {
					childNode = new Node(board);
					currentNode.addChild(childNode);
				}
				//Eintrag in der TranspositionTable enthalten -> Bewertung setzen
				//und viiiiel Zeit sparen
				if(TranspositionTable.getInstance().contains(hash, isMax) &&
						TranspositionTable.getInstance().getDepth(hash, isMax) >= depth) {
					childNode.setScore(TranspositionTable.getInstance().getScore(hash, isMax));
				} else {
					//Andernfalls eben bis zur Suchtiefe durchrechnen
					alphaBeta(depth - 1, childNode, !isMax, alpha, beta);
				}

				if(isMax) {
					if(childNode.getScore() > beta) {
						break;
					}
					if(childNode.getScore() > currentNode.getScore()) {
						currentNode.setScore(childNode.getScore());
						if(currentNode == gameTree.getRoot()) {
							bestMove(move, currentNode.getScore());
						}
						if(currentNode.getScore() > alpha) {
							alpha = currentNode.getScore();
						}
					}
				} else {
					if(childNode.getScore() < alpha) {
						break;
					}
					if(childNode.getScore() < currentNode.getScore()) {
						currentNode.setScore(childNode.getScore());
						if(currentNode.getScore() < beta) {
							beta = currentNode.getScore();
						}
					}
				}
				
				//Errechnete Bewertung in der TranspositionTable speichern
				TranspositionTable.getInstance().put(hash, depth, currentNode.getScore(), isMax);

				//Infos fuer die GUI senden
				sendInfo(move);
			}
		}
	}
}