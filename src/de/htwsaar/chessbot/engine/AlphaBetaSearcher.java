package de.htwsaar.chessbot.engine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.htwsaar.chessbot.engine.config.Config;
import de.htwsaar.chessbot.engine.config.SpinOption;
import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;

/**
 * 
 * @author David Holzapfel
 *
 */
public class AlphaBetaSearcher extends Thread {
	
	private static int INSTANCE_COUNT = 0;

	private ThreadGroup alphaBetaGroup;
	private List<AlphaBetaThread> searchThreads;
	private int activeThreads = 0;
	
	private volatile HashTable hashTable;
	private EvaluationFunction evalFunc;
	private boolean isRunning = false;
	private long deadLine = 0;
	private int depthLimit = 0;
	private long startTime = 0;
	private int currentMoveNumber = 0;
	private Move currentMove = null;

	private Board rootBoard = null;
	private LinkedList<Board> boardQueue;

	private Move bestMove = null;
	private int bestScore = 0;

	public AlphaBetaSearcher(EvaluationFunction evalFunc) {
		int instanceID = INSTANCE_COUNT++;
		this.alphaBetaGroup = new ThreadGroup("AlphaBetaGroup-" + instanceID);
		this.alphaBetaGroup.setMaxPriority(7);
		this.searchThreads = new LinkedList<AlphaBetaThread>();
		
		this.boardQueue = new LinkedList<Board>();
		this.evalFunc = evalFunc;
		this.hashTable = new HashTable();
		this.setupThreads();
		this.setName("AlphaBetaSearcher-" + instanceID);
	}
	
	private void setupThreads() {
		SpinOption option = (SpinOption)(Config.getInstance().getOption(Config.THREAD_COUNT));
		int maxThreads = option.getMax();
		for(int i = 0; i < maxThreads; i++) {
			AlphaBetaThread alphaBetaThread = new AlphaBetaThread(hashTable, evalFunc);
			this.searchThreads.add(alphaBetaThread);
			alphaBetaThread.start();
		}
		this.activeThreads = option.getValue();
		setThreadCount(this.activeThreads);
	}

	public void setThreadCount(int threadCount) {
		int maxThreads = searchThreads.size();
		if(threadCount > this.activeThreads) {
			for(int i = activeThreads - 1; i < threadCount; i++) {
				searchThreads.get(i).unscrapThread();
			}
		}
		else if(threadCount < this.activeThreads) {
			for(int i = threadCount; i < maxThreads; i++) {
				searchThreads.get(i).scrapThread();
			}
		}
		this.activeThreads = threadCount;
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

			int threads = (int) Config.getInstance().getOption(Config.THREAD_COUNT).getValue();
			setThreadCount(threads);
			
			this.startTime = System.currentTimeMillis();
			for(int depth = 1; (depth <= this.depthLimit) && !getSearchStopped(); depth++) {
				this.boardQueue.clear();
				this.boardQueue.addAll(Arrays.asList(rootBoard.getResultingPositions()));
				this.currentMoveNumber = 0;
				this.currentMove = null;
				
				while(!searchFinished() && !getSearchStopped()) {
					
					AlphaBetaThread idleThread = getIdleThread();
					if(idleThread != null) {
						Board board = getTask();
						if(board != null) {
							idleThread.startSearch(board, depth, deadLine);
							searchThreads.remove(idleThread);
							searchThreads.add(idleThread);
							currentMove = board.getLastMove();
							currentMoveNumber++;
						}
					}

					updateResults();
				}
				for(AlphaBetaThread thread : searchThreads) {
					thread.stopSearch();
				}
			}

			sendInfo();
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
			if(thread.isIdle() && !thread.isScrapped()) {
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
		UCISender.getInstance().sendToGUI(
				"info time " + time +
				" currmove " + this.currentMove + 
				" currmovenumber " + this.currentMoveNumber);
	}

	public void printThreadStatus() {
		System.out.println("##Threadstatus##");
		for(AlphaBetaThread thread : searchThreads) {
			System.out.print("  " + thread.getName() + ": ");
			String status = "active";
			if(thread.isScrapped()) {
				status = "halted";
			}
			System.out.println(status);
		}
	}
	
}
