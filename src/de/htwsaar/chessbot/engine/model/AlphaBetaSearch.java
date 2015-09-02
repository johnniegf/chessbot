package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.engine.model.GameTree.Node;

/**
 * 
 * @author David Holzapfel
 *
 */

public class AlphaBetaSearch implements Runnable {

	private GameTree gameTree;
	private Move currentBestMove;
	
	public AlphaBetaSearch() {
		
	}
	
	@Override
	public void run() {
		
	}
	
	public void startAlphaBeta(Board board, final int searchDepth) {
		this.gameTree = new GameTree(board);
		alphaBeta(searchDepth, gameTree.getRoot(), true,
				Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	private void alphaBeta(final int depth, Node currentNode,
			boolean isMax, int alpha, int beta) {
		
		if(depth == 0) {
			currentNode.setScore(
					gameTree.getEvaluationFunction().evaluate(currentNode.getBoard()));
		} else {
			if(isMax) {
				currentNode.setScore(Integer.MIN_VALUE);
			} else {
				currentNode.setScore(Integer.MAX_VALUE);
			}
			
			for(Move move : currentNode.getBoard().getMoveList()) {
				Board board = move.execute(currentNode.getBoard().clone());
				Node childNode = new Node(board);
				currentNode.addChild(childNode);
				alphaBeta(depth - 1, childNode, !isMax, alpha, beta);
				
				if(isMax) {
					if(childNode.getScore() > beta) {
						break;
					}
					if(childNode.getScore() > currentNode.getScore()) {
						currentNode.setScore(childNode.getScore());
						if(currentNode == gameTree.getRoot()) {
							currentBestMove = move;
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
			}
		}
	}
	
}
