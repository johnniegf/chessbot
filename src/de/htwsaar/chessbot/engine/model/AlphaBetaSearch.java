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

public class AlphaBetaSearch extends Thread implements DeepeningInterrupter {

	public static void main(String[] args) throws IOException {
		Game game = new Game(
				//"8/2p1pp2/8/4k3/8/1Q6/PPP4P/RN5K b - - 0 1"
				);

		AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(game);
		alphaBetaSearch.setMaxSearchDepth(10000);
		alphaBetaSearch.setTimeLimit(0);
		alphaBetaSearch.setPondering(true);
		alphaBetaSearch.start();
		alphaBetaSearch.startSearch();
	}

	private Game game;
	private GameTree gameTree;
	private BackgroundDeepener bgDeepener;
	private final EvaluationFunction evalFunc = new Evaluator();

	private int maxSearchDepth;
	private int maxTime = 0;
	private Collection<Move> limitedMoveList = null;
	private boolean isPondering = false;
	private boolean ponderHit = false;

	private long startTime = 0;
	private long nodesSearched = 0;
	private double nodesPerSecond = 0;
	private volatile boolean exitSearch = false;

	private Move currentBestMove;
	private Move currentPonderMove;
	private Node ponderNode;
	private int currentBestScore = 0;
	private int currentPonderScore = 0;
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
		this.bgDeepener = new BackgroundDeepener(this);
		this.bgDeepener.setMaxDepth(15);
		this.bgDeepener.setPriority(Thread.MIN_PRIORITY);
		this.bgDeepener.start();
	}

	public void setGame(Game game) {
		this.game = game;
		this.gameTree = null;
		this.ponderHit = false;
	}

	public void startSearch() {
		this.exitSearch = false;
	}

	@Override
	public void run() {
		while(true) {
			while(exitSearch) {
				if((boolean)Config.getInstance().getOption("Ponder").getValue()) {
					this.bgDeepener.beginDeepening();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.bgDeepener.endDeepening();
			this.startTime = System.currentTimeMillis();
			startAlphaBeta(this.ponderHit);
		}
	}

	public void stopSearch() {
		this.exitSearch = true;
	}
	
	public boolean getSearchStopped() {
		if(this.maxTime > 0 && getPassedTime() >= this.maxTime) {
			this.exitSearch = true;
		}
		
		return this.exitSearch;
	}

	public GameTree getGameTree() {
		return this.gameTree;
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
	
	public int getPassedTime() {
		return (int) (System.currentTimeMillis() - this.startTime);
	}
	
	public void setPondering(boolean isPondering) {
		this.isPondering = isPondering;
	}
	
	public void ponderhit() {
		this.ponderHit = true;
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
	private void bestMove(Move move, int score) {
		this.currentBestMove = move;
		this.currentBestScore = score;
	}
	
	private void ponderMove(Move move, int score, Node ponderNode) {
		this.currentPonderMove = move;
		this.currentPonderScore = score;
		this.ponderNode = ponderNode;
	}

	//Sendet Informationen ueber den Zustand der Suche an die GUI
	private void sendInfo(Move currentMove) {
		UCISender.getInstance().sendToGUI(
				"info currmove " + currentMove +
				" currmovenumber " + this.currentMoveNumber +
				" depth " + this.currentSearchDepth +
				" time " + getPassedTime() +
				" nps " + (int)this.nodesPerSecond +
				" nodes " +  this.nodesSearched
				);
	}

	//Sendet den "besten" Zug nachdem die Suche beendet wurde
	public void sendBestMove() {
		String bestMove = "bestmove " + this.currentBestMove;
		if(this.isPondering) {
			bestMove += " ponder " + this.currentPonderMove;
		}
		UCISender.getInstance().sendToGUI(bestMove);
	}

	//Startet die Suche
	private void startAlphaBeta(boolean ponderHit) {
		this.currentBestMove = null;
		this.exitSearch = false;
		if(!ponderHit || this.gameTree == null) {
			this.gameTree = new GameTree(this.evalFunc, this.game.getCurrentBoard());
		}
		else {
			this.ponderHit = false;
			this.gameTree.replaceRoot(this.ponderNode);
		}
		this.nodesSearched = 0;
		boolean startMax = this.gameTree.getRoot().getBoard().isWhiteAtMove();

		for(int i = 1; i <= maxSearchDepth && !getSearchStopped(); i++) { 
			this.currentSearchDepth = i;
			this.currentMoveNumber = 0;
			boolean max = (i % 2 != 0) ? !startMax : startMax;
			UCISender.getInstance().sendToGUI("info string Deepening to depth " + i + "...");
			this.gameTree.deepen(i, max, this, false);
			UCISender.getInstance().sendToGUI("info string Done. " + this.gameTree.getLastDeepeningStats());
			UCISender.getInstance().sendToGUI("info string Tree: " + this.gameTree.getTreeSize());
			alphaBeta(this.gameTree.getRoot(), Integer.MIN_VALUE, Integer.MAX_VALUE, i, startMax);
			this.gameTree.reduceLayer(i, 15, max, this);

			this.nodesPerSecond = 
					(1000d * this.nodesSearched) / getPassedTime();
		}

		sendBestMove();
		UCISender.getInstance().sendToGUI("info string Search completed in " + getPassedTime() + "ms ("
				+ "to depth " + this.currentSearchDepth + ")");
		this.exitSearch = true;
	}


	private void alphaBeta(Node n, int alpha, int beta, int depth, boolean max) {

		if(getSearchStopped()) {
			return;
		}

		this.nodesSearched++;
		UCISender.getInstance().sendToGUI("info nodes " + this.nodesSearched);

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
			
			if(getSearchStopped()) {
				return;
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

			sendInfo(move);
			
			if(max) {

				if(child.getScore() > alpha) {
					alpha = child.getScore();
					if(alpha >= beta) {
						n.setScore(alpha);
						tTable.put(n.getBoard().hash(), depth, alpha, max);
						
						//this.gameTree.cutoff(n, child, this.currentSearchDepth - depth);
						break;
					}
					if(n.isRoot()) {
						bestMove(move, alpha);
					}
					else if(n.getParent().isRoot()) {
						ponderMove(move, alpha, child);
					}
				}

			} else {

				if(child.getScore() < beta) {
					beta = child.getScore();
					if(beta <= alpha) {
						n.setScore(beta);
						tTable.put(n.getBoard().hash(), depth, beta, max);
						
						//this.gameTree.cutoff(n, child, this.currentSearchDepth - depth);
						break;
					}
					if(n.isRoot()) {
						bestMove(move, beta);
					}
					else if(n.getParent().isRoot()) {
						ponderMove(move, beta, child);
					}
				}

			}

		}
		
	}

	@Override
	public boolean stopDeepening() {
		return this.getSearchStopped();
	}

}