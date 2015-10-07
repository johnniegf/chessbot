package de.htwsaar.chessbot.engine;

import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;

public class AlphaBetaThread extends Thread {

	private static int INSTANCE_COUNT = 0;

	private HashTable hashTable;
	private EvaluationFunction evaluator;
	private volatile Move bestMove;
	private volatile int bestScore;
	private boolean isRunning;
	private Board rootBoard = null;
	private int depthLimit = 0;
	private long deadLine = 0;
	private boolean uncheckedResults = true;


	public AlphaBetaThread(HashTable hashTable, EvaluationFunction evaluator) {
		this.hashTable = hashTable;
		this.evaluator = evaluator;
		this.bestMove = null;
		this.bestScore = 0;
		this.isRunning = false;
		this.setDaemon(true);
		this.setName("AlphaBeta-" + INSTANCE_COUNT++);
		this.setPriority(4);
	}

	@Override
	public void run() {
		while(true) {
			while(!isRunning) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			this.bestMove = null;
			this.bestScore = 0;
			boolean startMax = rootBoard.isWhiteAtMove();

			alphaBeta(rootBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, startMax, 0, depthLimit);

			isRunning = false;
		}
	}

	public void startSearch(Board board, int depthLimit, long deadLine) {
		this.rootBoard = board;
		this.depthLimit = depthLimit;
		this.deadLine = deadLine;
		this.isRunning = true;
		this.uncheckedResults = true;
	}

	private int alphaBeta(Board currentBoard, int alpha, int beta, boolean max, int depth, int maxDepth) {

		if(getSearchStopped()) {
			return 0;
		}

		int score = hashTable.get(currentBoard, maxDepth - depth, alpha, beta);
		if(HashTable.isDefined(score)) {
			return score;
		}

		if(depth >= maxDepth) {
			return evaluate(currentBoard);
		}

		Board[] boardList = currentBoard.getResultingPositions();

		if(depth == 0) {
			bestMove(boardList[0], evaluate(boardList[0]));
			if(boardList.length == 1) {
				return 0;
			}
		}


		if(max) {
			int bestResult = Integer.MIN_VALUE;
			for(Board board : boardList) {
				if (!Move.isValidResult(board))
					continue;

				if(getSearchStopped()) {
					break;
				}

				int result = alphaBeta(board, alpha, beta, !max, depth + 1, maxDepth);
				if(result > alpha) {
					alpha = result;
					if(depth == 0) {
						bestMove(board, result);
						bestResult = result;
					}
				}
				if(alpha >= beta) {
					hashTable.put(currentBoard, maxDepth - depth, result, HashTable.FLAG_ALPHA);
					return alpha;
				}

			}
			hashTable.put(currentBoard, maxDepth - depth, bestResult, HashTable.FLAG_PV);
			sendInfo(currentBoard, depth);
			return alpha;
		}
		else {
			int bestResult = Integer.MAX_VALUE;
			for(Board board : boardList) {
				if (!Move.isValidResult(board))
					continue;

				if(getSearchStopped()) {
					break;
				}

				int result = alphaBeta(board, alpha, beta, !max, depth + 1, maxDepth);
				if(result < beta) {
					beta = result;
					if(depth == 0) {
						bestMove(board, result);
						bestResult = result;
					}
				}
				if(beta <= alpha) {
					hashTable.put(currentBoard, maxDepth - depth, result, HashTable.FLAG_BETA);
					return beta;
				}

			}
			hashTable.put(currentBoard, maxDepth - depth, bestResult, HashTable.FLAG_PV);
			sendInfo(currentBoard, depth);
			return beta;
		}

	}


	private boolean getSearchStopped() {
		return !this.isRunning || System.currentTimeMillis() >= this.deadLine;
	}

	private int evaluate(Board board) {
		return this.evaluator.evaluate(board);
	}

	private void bestMove(Board board, int score) {
		this.bestMove = board.getLastMove();
		this.bestScore = score;
		uncheckedResults = true;
	}

	public boolean isIdle() {
		return !this.isRunning;
	}

	public Move getCurrentBestMove() {
		return this.bestMove;
	}

	public int getCurrentBestScore() {
		return this.bestScore;
	}

	public void setDeadLine(long deadLine) {
		this.deadLine = deadLine;
	}

	public boolean uncheckedResults() {
		return this.uncheckedResults;
	}

	public void setResultsChecked() {
		this.uncheckedResults = false;
	}

	//Sendet Informationen ueber den Zustand der Suche an die GUI
	private void sendInfo(Board board, int depth) {
		UCISender.getInstance().sendDebug(getName() + ":");
		UCISender.getInstance().sendToGUI(
				"info currmove " + board.getLastMove() +
				" depth " + depth
				);
	}
	
}
