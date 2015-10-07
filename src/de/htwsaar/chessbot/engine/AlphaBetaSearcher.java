package de.htwsaar.chessbot.engine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;

public class AlphaBetaSearcher extends Thread {
	
	private static int INSTANCE_COUNT = 0;

	private List<AlphaBetaThread> searchThreads;
	private volatile HashTable hashTable;
	private EvaluationFunction evalFunc;
	private boolean isRunning = false;
	private long deadLine = 0;
	private int depthLimit = 0;
	private long startTime = 0;

	private Board rootBoard = null;
	private LinkedList<Board> boardQueue;

	private Move bestMove = null;
	private int bestScore = 0;

	public AlphaBetaSearcher(EvaluationFunction evalFunc) {
		this.searchThreads = new LinkedList<AlphaBetaThread>();
		this.boardQueue = new LinkedList<Board>();
		this.evalFunc = evalFunc;
		this.hashTable = new HashTable();
		this.setName("AlphaBetaSearcher-" + INSTANCE_COUNT++);
	}

	public void setThreadCount(int threadCount) {
		int diff = searchThreads.size() - threadCount;
		if(diff < 0) {
			diff *= -1;
			for(int i = 0; i < diff; i++) {
				AlphaBetaThread thread = new AlphaBetaThread(hashTable, evalFunc);
				thread.start();
				searchThreads.add(thread);
			}
		}
		else if(diff > 0) {
			for(int i = 0; i < diff; i++) {
				int index = searchThreads.size() - i - 1;
				searchThreads.get(index).interrupt();
				searchThreads.remove(index);
			}
		}
	}

	public void startSearch(Board board, long deadLine, int depthLimit) {
		this.rootBoard = board;
		this.deadLine = deadLine;
		this.depthLimit = depthLimit;
		this.isRunning = true;
	}
	
	public void stopSearch() {
		this.isRunning = false;
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

			this.startTime = System.currentTimeMillis();
			for(int depth = 1; depth <= this.depthLimit && !getSearchStopped(); depth++) {
				this.boardQueue.clear();
				this.boardQueue.addAll(Arrays.asList(rootBoard.getResultingPositions()));
				
				while(!searchFinished() && !getSearchStopped()) {
					
					AlphaBetaThread idleThread = getIdleThread();
					if(idleThread != null) {
						Board board = getTask();
						if(board != null) {
							idleThread.startSearch(board, depth, deadLine);
							searchThreads.remove(idleThread);
							searchThreads.add(idleThread);
						}
					}

					updateResults();
				}
				sendInfo();
			}

			sendBestMove();
			isRunning = false;
		}
	}

	private void updateResults() {
		for(AlphaBetaThread thread : searchThreads) {
			if(thread.uncheckedResults()) {
				Move move = thread.getCurrentBestMove();
				int score = thread.getCurrentBestScore();
				if(max()) {
					if(score > this.bestScore) {
						this.bestMove = move;
						this.bestScore = score;
					}
				}
				else {
					if(score < this.bestScore) {
						this.bestMove = move;
						this.bestScore = score;
					}
				}
				thread.setResultsChecked();
			}
		}
	}

	private Board getTask() {
		return this.boardQueue.pollFirst();
	}

	private AlphaBetaThread getIdleThread() {
		for(AlphaBetaThread thread : searchThreads) {
			if(thread.isIdle()) {
				return thread;
			}
		}

		return null;
	}

	private boolean searchFinished() {
		boolean finished = true;
		for(AlphaBetaThread thread : searchThreads) {
			if(!thread.isIdle()) {
				finished = false;
			}
		}
		return this.boardQueue.isEmpty() && finished;

	}

	private boolean max() {
		return this.rootBoard.isWhiteAtMove();
	}

	private boolean getSearchStopped() {
		return !this.isRunning || System.currentTimeMillis() >= this.deadLine;
	}

	//Sendet den "besten" Zug nachdem die Suche beendet wurde
	public void sendBestMove() {
		String bestMove = "bestmove " + this.bestMove;
		UCISender.getInstance().sendToGUI(bestMove);
	}
	
	public void sendInfo() {
		long time = System.currentTimeMillis() - this.startTime;
		UCISender.getInstance().sendToGUI("info time " + time);
	}
	
}
