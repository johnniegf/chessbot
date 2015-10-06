package de.htwsaar.chessbot.engine;

public class BackgroundDeepener extends Thread implements DeepeningInterrupter {

	private static final long SLEEPING_TIME = 100;
	
	
	private AlphaBetaSearch searcher;
	private boolean isActive = false;
	private int maxDepth = 1;
	
	public BackgroundDeepener(AlphaBetaSearch searcher) {
		this.searcher = searcher;
	}
	/*
	private GameTree getTree() {
		return this.searcher.getGameTree();
	}*/
	
	@Override
	public synchronized void run() {
		while(true) {
			while(!isActive) {// || getTree() == null) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//doDeepening();
		}
	}
	
	public void beginDeepening() {
		this.isActive = true;
	}
	
	public void endDeepening() {
		this.isActive = false;
	}
	
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	/*
	private void doDeepening() {
		GameTree tree = getTree();
		
		boolean startMax = tree.getRoot().getBoard().isWhiteAtMove();
		for(int i = 1; i < this.maxDepth && !stopDeepening(); i++) {
			
			boolean max = (i % 2 != 0) ? !startMax : startMax;
			tree.deepen(i, max, this, true);
			tree.reduceLayer(i, 15, max, this);

			if(stopDeepening()) {
				return;
			}
			
			try {
				Thread.sleep(SLEEPING_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/

	@Override
	public boolean stopDeepening() {
		return !isActive;
	}
	
}
