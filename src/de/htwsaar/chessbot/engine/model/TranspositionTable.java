package de.htwsaar.chessbot.engine.model;

import java.util.HashMap;

public class TranspositionTable {
	
	private static TranspositionTable INSTANCE;
	
	public static TranspositionTable getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new TranspositionTable();
		}
		return INSTANCE;
	}
	
	private HashMap<Long, Integer> scoreMaxTable, scoreMinTable, depthMaxTable, depthMinTable;
	
	private TranspositionTable() {
		this.scoreMaxTable = new HashMap<Long, Integer>();
		this.scoreMinTable = new HashMap<Long, Integer>();
		this.depthMaxTable = new HashMap<Long, Integer>();
		this.depthMinTable = new HashMap<Long, Integer>();
	}
	
	
	public void put(long hash, int depth, int nodeScore, boolean max) {
		HashMap<Long, Integer> scoreTable = max ? this.scoreMaxTable : this.scoreMinTable;
		HashMap<Long, Integer> depthTable = max ? this.depthMaxTable : this.depthMinTable;
		if(scoreTable.containsKey(hash)) {
			if(depthTable.get(hash) < depth) {
				scoreTable.remove(hash);
				depthTable.remove(hash);
			} else {
				return;
			}
		}
		scoreTable.put(hash, nodeScore);
		depthTable.put(hash, depth);
	}
	
	public int getScore(long hash, boolean max) {
		return max ? this.scoreMaxTable.get(hash) : this.scoreMinTable.get(hash);
	}
	
	public int getDepth(long hash, boolean max) {
		return max ? this.depthMaxTable.get(hash) : this.depthMinTable.get(hash);
	}
	
	public boolean contains(long hash, boolean max) {
		return max ? this.scoreMaxTable.containsKey(hash) : this.scoreMinTable.containsKey(hash);
	}
	public int getTableSize() {
		return this.scoreMaxTable.size();
	}
	
}
