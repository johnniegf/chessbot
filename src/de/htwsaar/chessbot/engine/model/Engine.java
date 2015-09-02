package de.htwsaar.chessbot.engine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Dominik Becker
 */
public class Engine {
	
	private static final String SEARCH = "searchmoves";
	private static final String DEPTH = "depth";
	private static final String MOVE = "([a-h][1-8]){2}";
	
	private Game game;
	
	public Engine() {
		this.game = new Game();
	}
	
	public void ucinewgame() {
		this.game = new Game();
	}
	
	public void go(String[] param, int i) {
		List<String> moves;
		GameTree tree = new GameTree(game.getCurrentBoard());
		for(int j = i; j < param.length; j++) {
			switch(param[j]) {
			case(SEARCH):
				while(param[j].matches(MOVE)) {
					
					j++;
				};
				break;
			case(DEPTH):
				tree.alphaBetaSearch(Integer.parseInt(param[j+1]), tree.getRoot(), true,
						Integer.MIN_VALUE, Integer.MAX_VALUE);
			break;
			}
		}
		System.out.println(
				tree.alphaBetaSearch(3, tree.getRoot(), true, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	public void stop(String[] param, int i) {
		
	}
	
	public void position(String[] split, int i) {
		List<String> moves;
		if(split[i+1].equals("moves")) {
			moves = new ArrayList<String>();
			for(int j = i+2; j < split.length; j++) {
				moves.add(split[j]);
			}
			move(moves);
		}
		else
		if(split[i+1].equals("startpos")) {
			this.game = new Game();
			moves = new ArrayList<String>();
			for(int j = i+3; j < split.length; j++) {
				moves.add(split[j]);
			}
			move(moves);
		} else {
			this.game = new Game(split[i+2]);
			moves = new ArrayList<String>();
			for(int j = i+4; j < split.length; j++) {
				moves.add(split[j]);
			}
			move(moves);
		}
	}
	
	private void move(List<String> moves) {
		for(int i = 0; i < moves.size(); i++) {
			game.doMove(moves.get(i));
		}
		//System.out.println(game.getCurrentBoard()+"\n\n\n");
	}
}
