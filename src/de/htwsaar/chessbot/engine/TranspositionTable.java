package de.htwsaar.chessbot.engine;

import de.htwsaar.chessbot.engine.io.UCISender;
import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
	
	private static TranspositionTable INSTANCE;
	private static final long BITS_PER_ENTRY = 1056;
	
	public static TranspositionTable getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new TranspositionTable();
		}
		return INSTANCE;
	}
	
	private HashMap<Long, Integer> scoreTable, depthTable,
		accessTable;
	private int maxMB = 512;
	
	private TranspositionTable() {
		this.scoreTable = new HashMap<Long, Integer>();
		this.depthTable = new HashMap<Long, Integer>();
		this.accessTable = new HashMap<Long, Integer>();
	}
	
	
	public void put(long hash, int depth, int nodeScore, boolean max) {
		/**
		String s = "hash: %d depth: %d score %d %s";
		System.out.println(String.format(s, hash, depth, nodeScore, max ? "max" : "min"));
		**/
		
		if(scoreTable.containsKey(hash)) {
			if(depthTable.get(hash) < depth) {
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
			
			for(long hash : accessTable.keySet()) {
				if(accessTable.get(hash) < minValue) {
					minValue = accessTable.get(hash);
					minKey = hash;
				}
			}
			
			accessTable.remove(minKey);
			scoreTable.remove(minKey);
			depthTable.remove(minKey);
		}
	}


	public int getScore(long hash, boolean max) {
		int newAccess = accessTable.get(hash) + 1;
		accessTable.remove(hash);
		accessTable.put(hash, newAccess);
		return this.scoreTable.get(hash);
	}
	
	public int getDepth(long hash, boolean max) {
		return this.depthTable.get(hash);
	}
	
	public boolean contains(long hash, boolean max) {
		return this.scoreTable.containsKey(hash);
	}
	public int getTableSize() {
		return this.scoreTable.size();
	}
	
	public long getTableUsedMemoryInKBit() {
		return getTableSize() * (long)(BITS_PER_ENTRY / 1024d);
	}
	
	public int getMemoryFillingRate() {
		double maxInKBit = maxMB * 1024 * 8;
		return (int)(1000d * (getTableUsedMemoryInKBit() / maxInKBit));
	}
	
	public boolean hashFull() {
		return getMemoryFillingRate() >= 1000;
	}
	
}
