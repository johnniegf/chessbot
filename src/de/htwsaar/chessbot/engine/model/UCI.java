package de.htwsaar.chessbot.engine.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
*   Universal Chess Interface   
*
*   @author Timo Klein
*   
**/

public class UCI {

    private String cmd;
    private BufferedReader engineIn;
    private Map<String,Object> kdo;
    private Engine engine;
    
    
    public UCI() {
        try{
        	initKdoList();
        	engineIn = new BufferedReader(new InputStreamReader(System.in));
        	start();
        	
        }catch(IOException ioe) {
        	ioe.printStackTrace();
        }
    }
    
    public void initKdoList() {
    	kdo = new HashMap<>();
    	kdo.put("uci", "uciok");
    	kdo.put("isready", "readyok");
    	kdo.put("ucinewgame", engine = new Engine());
    	kdo.put("position", null);
    	kdo.put("go", null);
    	
    }
    
    public void start() throws IOException{
    	while(true) {
            cmd = engineIn.readLine();
            if (cmd.startsWith("quit"))
            	System.exit(0);
            
            String [] result = cmd.split(" ");
            
            if (kdo.containsKey(result[0])) {
            	if (cmd.equals("uci")){
            		setUCIParameter();
                	sendCmd((String) kdo.get(cmd));	
            	} else if (cmd.startsWith("isready")) {
            		sendCmd((String) kdo.get(cmd));
            	} else if (cmd.equals("ucinewgame")) {
            		kdo.get(cmd);
            	} else if (cmd.startsWith("position")) {
            		engine.position(result, 0);
            	} else if (cmd.startsWith("go")) {
            		engine.go(result, 0);
            	}
            } else
            	System.out.println("Command is not supported: " + cmd);   	
        }	
    }
    
    public void sendCmd(String cmd) {
    	System.out.println(cmd);
    }

    public void setUCIParameter() {
    	sendCmd("id name Chessbot");
    	sendCmd("id author Projektgruppe 2015 Schachengine");
    	sendCmd("option name Hash type spin default 64 min 8 max 512");
    	sendCmd("option name Evaluation Table type spin default 8 min 1 max 64");
    	sendCmd("option name Pawn Table type spin default 8 min 1 max 64");
    }
    
    public static void main(String [] args) {
    	new UCI();	
    }
}
