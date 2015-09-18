package de.htwsaar.chessbot.engine.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import de.htwsaar.chessbot.engine.model.GameTree.Node;

/**
 * 
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */

public class AlphaBetaSearch extends Thread {

	public static void main(String[] args) throws IOException {
		Game game = new Game(
				"8/2p1pp2/8/4k3/8/1Q6/PPP4P/RN5K b - - 0 1"
				);

		AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(game);
		alphaBetaSearch.setMaxSearchDepth(6);
		alphaBetaSearch.start();
		alphaBetaSearch.startSearch();
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
		this.exitSearch = true;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void startSearch() {
		this.exitSearch = false;
	}

	@Override
	public void run() {
		while(true) {
			while(exitSearch) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.startTime = System.currentTimeMillis();
			startAlphaBeta();
		}
	}

	public void stopSearch() {
		this.exitSearch = true;
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
	private void sendInfo(Move currentMove, int currentScore) {
		long timeSpent = System.currentTimeMillis() - this.startTime;
		System.out.println(
				"info currmove " + currentMove +
				" currmovenumber " + this.currentMoveNumber +
				" depth " + this.currentSearchDepth +
				" time " + timeSpent +
				" nps " + (int)this.nodesPerSecond +
				" nodes " +  this.nodesSearched +
				" score cp " + currentScore
				);
	}

	//Sendet den "besten" Zug nachdem die Suche beendet wurde
	public void sendBestMove() {
		System.out.println("bestmove " + this.getCurrentBestMove().toString());
	}

	//Startet die Suche
	private void startAlphaBeta() {
		this.currentBestMove = null;
		this.exitSearch = false;
		this.gameTree = new GameTree(this.evalFunc, this.game.getCurrentBoard());
		this.nodesSearched = 0;
		boolean startMax = this.game.getCurrentBoard().isWhiteAtMove();

		for(int i = 1; i <= maxSearchDepth && !this.exitSearch; i++) { 
			this.currentSearchDepth = i;
			this.currentMoveNumber = 0;
			this.gameTree.deepen(i, (i % 2 != 0) ? !startMax : startMax);
			alphaBeta(gameTree.getRoot(), Integer.MIN_VALUE, Integer.MAX_VALUE, i, startMax);

			this.nodesPerSecond = 
					(1000d * this.nodesSearched) / (System.currentTimeMillis() - this.startTime);
		}

		sendBestMove();
		this.exitSearch = true;
	}


	private void alphaBeta(Node n, int alpha, int beta, int depth, boolean max) {

		if(this.maxTime > 0) {
			if(System.currentTimeMillis() - this.startTime >= this.maxTime) {
				this.exitSearch = true;
			}
		}

		if(this.exitSearch) {
			return;
		}

		this.nodesSearched++;

		Board board = n.getBoard();
		TranspositionTable tTable = TranspositionTable.getInstance();

		if(tTable.contains(board.hash(), max) && tTable.getDepth(board.hash(), max) >= depth) {
			n.setScore(tTable.getScore(board.hash(), max));
			return;
		} else if(n.getChildren().isEmpty()) {
			n.setScore(this.gameTree.getEvaluationFunction().evaluate(board));
			return;
		}

		if(n.isRoot() && n.childCount() == 1) {
			bestMove(((ArrayList<Move>)n.getBoard().getMoveList()).get(0), 0);
			return;
		}

		
		for(Node child : n.getChildren()) {
			
			if(this.exitSearch) {
				break;
			}
			
			Move move = child.getLeadsTo();
			
			if(n.isRoot() && this.limitedMoveList != null) {
				if(!this.limitedMoveList.contains(move)) {
					continue;
				}
			}

			if(n.isRoot()) {
				this.currentMoveNumber++;
			}
			
			alphaBeta(child, alpha, beta, depth - 1, !max);

			sendInfo(move, this.gameTree.getEvaluationFunction().evaluate(move.execute(board)));
			
			if(max) {

				if(child.getScore() > alpha) {
					alpha = child.getScore();
					if(alpha >= beta) {
						n.setScore(alpha);
						tTable.put(n.getBoard().hash(), depth, alpha, max);
						this.gameTree.cutoff(n, child, this.currentSearchDepth - depth);
						break;
					}
					if(n.isRoot()) {
						bestMove(move, alpha);
					}
				}

			} else {

				if(child.getScore() < beta) {
					beta = child.getScore();
					if(beta <= alpha) {
						n.setScore(beta);
						tTable.put(n.getBoard().hash(), depth, beta, max);
						this.gameTree.cutoff(n, child, this.currentSearchDepth - depth);
						break;
					}
					if(n.isRoot()) {
						bestMove(move, beta);
					}
				}

			}

		}
		
	}

}