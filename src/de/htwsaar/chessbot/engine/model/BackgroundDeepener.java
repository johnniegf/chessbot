package de.htwsaar.chessbot.engine.model;

public class BackgroundDeepener extends Thread implements DeepeningInterrupter {

	private AlphaBetaSearch searcher;
	private boolean isActive = false;
	private int maxDepth = 1;
	
	public BackgroundDeepener(AlphaBetaSearch searcher) {
		this.searcher = searcher;
	}
	
	private GameTree getTree() {
		return this.searcher.getGameTree();
	}
	
	@Override
	public synchronized void start() {
		while(true) {
			while(!isActive) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			doDeepening();
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
	
	private void doDeepening() {
		GameTree tree = getTree();
		
		for(int i = 1; i < this.maxDepth; i++) {
			if(!isActive) {
				break;
			}
			
			boolean startMax = tree.getRoot().getBoard().isWhiteAtMove();
			tree.deepen(i, (i % 2 != 0) ? !startMax : startMax, this);
		}
	}

	@Override
	public boolean stopDeepening() {
		return !isActive;
	}
	
}
