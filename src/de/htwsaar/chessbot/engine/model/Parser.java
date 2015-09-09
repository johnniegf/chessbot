package de.htwsaar.chessbot.engine.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Parser {
	private static final String ILLEGALCMD = 
			"Command is not supported: ";
	private static final String MOVE = "([a-h][1-8]){2}";
	//TODO ausdruck aktualisieren(Bauernumwandlung)
	
	public static void uci() {
		setUCIParameter();
		sendCmd("uciok");
	}
	
	public static void isReady() {
		sendCmd("readyok");
	}
	
	public static void ucinewgame(Engine engine) {
		engine.newGame();
	}
	
	
	public static void position(String line, Engine engine) {
		String[] result = line.split("position ");
		String[] cmd = result[1].split(" ");
		String fenString;
		List<String> moves = new ArrayList<String>();
		if(cmd[0].equals("fen") && cmd.length > 6){
			fenString = cmd[2] + " " + 
                    cmd[2] + " " + 
                    cmd[3] + " " + 
                    cmd[4] + " " + 
                    cmd[5] + " " + 
                    cmd[6];
	        cmd[1] = fenString;
	        for (int i = 7; i < result.length; i++) {
	        	moves.add(result[i]);
	        }
	        engine.setBoard(fenString, moves);
		}
		else {
			for(int i = 2; i < cmd.length; i++) {
        		moves.add(cmd[i]);
        	}
        	engine.resetBoard(moves);
		}
	}
	
	public static void go(String line, Engine engine) {
		List<Move> moves = null;
		int depth = 0;
		int time = 0;
		String[] result = line.split(" ");
		for(int j = 0; j < result.length; j++) {
			switch(result[j]){
			case "searchmoves":
				moves = getMoves(line, engine.getGame().getCurrentBoard().getMoveList());
				break;
			case "depth":
				String[] depthArray = line.split("depth ");
	    		String[] depthValue = depthArray[1].split(" ");
	    		String sDepth = depthValue[0];
	    		depth = Integer.parseInt(sDepth);
	    		break;
			case "movetime":
				String[] timeArray = line.split("movetime ");
				String[] timeValue = timeArray[1].split(" ");
				String sTime = timeValue[0];
				time = Integer.parseInt(sTime);
				engine.getSearcher().setTimeLimit(time);
				break;
			}
		}
		if(moves != null && depth != 0){
			engine.searchmoves(moves, depth);
		}
		else if(moves != null){engine.searchmoves(moves, 2000);}
		else if(depth != 0){engine.search(depth);}
		else engine.search(2000);
	}
	
	private static List<Move> getMoves(String line, Collection<Move> moveList) {
		List<Move> moves = new ArrayList<Move>();
		String[] searchMv = line.split("searchmoves ");
		String[] elements = searchMv[1].split(" ");
		for(int i = 0; i < elements.length; i++) {
			if(!elements[i].matches(MOVE)){break;}
			for(Move m : moveList) {
				if(elements[i].equals(m.toString()))
					moves.add(m);
			}
		}
		return moves;
		
	}
	
	public static void stop(Engine engine) {
		engine.stop();
	}
	
	public static void illegalCmd(String line) {
		sendCmd(ILLEGALCMD+line);
	}
	
	private static void sendCmd(String cmd) {
        System.out.println(cmd);
    }
 
    private static void setUCIParameter() {
        sendCmd("id name Chessbot");
        sendCmd("id author Projektgruppe 2015 Schachengine");
        sendCmd("option name Evaluation Table type spin default 8 min 1 max 64");
        sendCmd("option name Pawn Table type spin default 8 min 1 max 64");
        sendCmd("option name Write Debug Log type check default false");
        sendCmd("option name Contempt type spin default 0 min -100 max 100");
        sendCmd("option name Min Split Depth type spin default 0 min 0 max 12");
        sendCmd("option name Threads type spin default 1 min 1 max 128");
        sendCmd("option name Hash type spin default 16 min 1 max 1048576");
        sendCmd("option name Clear Hash type button");
        sendCmd("option name Ponder type check default false");
        sendCmd("option name MultiPV type spin default 1 min 1 max 500");
        sendCmd("option name Skill Level type spin default 20 min 0 max 20");
        sendCmd("option name Move Overhead type spin default 30 min 0 max 5000");
        sendCmd("option name Minimum Thinking Time type spin default 20 min 0 max 5000");
        sendCmd("option name Slow Mover type spin default 80 min 10 max 1000");
        sendCmd("option name UCI_Chess960 type check default false");
 
        sendCmd("setoption name Hash value 32");
 
    }
    

}
