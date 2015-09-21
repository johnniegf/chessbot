package de.htwsaar.chessbot.engine.model;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
	
	private static TranspositionTable INSTANCE;
	private static final int BITS_PER_ENTRY = 
			6 * 64 + //6 longs | 8 byte
			6 * 32 + //6 ints | 4 byte
			12 * 64; //12 Objekte | 8 byte
	
	public static TranspositionTable getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new TranspositionTable();
		}
		return INSTANCE;
	}
	
	private HashMap<Long, Integer> scoreMaxTable, scoreMinTable, depthMaxTable, depthMinTable,
		accessMaxTable, accessMinTable;
	private int maxMB = 256;
	
	private TranspositionTable() {
		this.scoreMaxTable = new HashMap<Long, Integer>();
		this.scoreMinTable = new HashMap<Long, Integer>();
		this.depthMaxTable = new HashMap<Long, Integer>();
		this.depthMinTable = new HashMap<Long, Integer>();
		this.accessMaxTable = new HashMap<Long, Integer>();
		this.accessMinTable = new HashMap<Long, Integer>();
	}
	
	
	public void put(long hash, int depth, int nodeScore, boolean max) {
		HashMap<Long, Integer> scoreTable = max ? this.scoreMaxTable : this.scoreMinTable;
		HashMap<Long, Integer> depthTable = max ? this.depthMaxTable : this.depthMinTable;
		HashMap<Long, Integer> accessTable = max ? this.accessMaxTable : this.accessMinTable;
		if(scoreTable.containsKey(hash)) {
			if(depthTable.get(hash) < depth) {
				checkFreeMemory();
				scoreTable.remove(hash);
				depthTable.remove(hash);
				scoreTable.put(hash, nodeScore);
				depthTable.put(hash, depth);
			} else {
				return;
			}
		} else {
			checkFreeMemory();
			scoreTable.put(hash, nodeScore);
			depthTable.put(hash, depth);
			accessTable.put(hash, 0);
		}
		UCISender.getInstance().sendToGUI("info hashfull " + getMemoryFillingRate());
		UCISender.getInstance().sendDebug("Added hash entry, now " + getTableSize() + " entries total");
	}
	
	private void checkFreeMemory() {
		if(hashFull()) {
			long minKey = 0;
			int minValue = Integer.MAX_VALUE;
			HashMap<Long, Integer> minTable = accessMinTable;
			for(long hash : accessMinTable.keySet()) {
				if(accessMinTable.get(hash) < minValue) {
					minValue = accessMinTable.get(hash);
					minKey = hash;
					minTable = accessMinTable;
				}
			}
			for(long hash : accessMinTable.keySet()) {
				if(accessMaxTable.get(hash) < minValue) {
					minValue = accessMaxTable.get(hash);
					minKey = hash;
					minTable = accessMaxTable;
				}
			}
			
			minTable.remove(minKey);
		}
	}


	public int getScore(long hash, boolean max) {
		HashMap<Long, Integer> accessTable = max ? this.accessMaxTable : this.accessMinTable;
		int newAccess = accessTable.get(hash) + 1;
		accessTable.remove(hash);
		accessTable.put(hash, newAccess);
		return max ? this.scoreMaxTable.get(hash) : this.scoreMinTable.get(hash);
	}
	
	public int getDepth(long hash, boolean max) {
		return max ? this.depthMaxTable.get(hash) : this.depthMinTable.get(hash);
	}
	
	public boolean contains(long hash, boolean max) {
		return max ? this.scoreMaxTable.containsKey(hash) : this.scoreMinTable.containsKey(hash);
	}
	public int getTableSize() {
		return this.scoreMaxTable.size() + this.scoreMinTable.size();
	}
	
	public int getTableUsedMemoryInBit() {
		return getTableSize() * BITS_PER_ENTRY;
	}
	
	public int getMemoryFillingRate() {
		double maxInBit = maxMB * 1024 * 1024 * 8;
		return (int)(1000d * getTableUsedMemoryInBit() / maxInBit);
	}
	
	public boolean hashFull() {
		return getMemoryFillingRate() >= 1000;
	}
	
}
