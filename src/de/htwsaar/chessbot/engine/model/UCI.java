package de.htwsaar.chessbot.engine.model;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.SliderUI;
 
/**
*   Universal Chess Interface   
*
*   @author Timo Klein
*   
**/
 
public class UCI  {
 
    private String cmd;
    private BufferedReader engineIn;
    private Map<String,Object> kdo;
    private Engine engine;
    
    private static final String POS = "position";
    private static final String GO = "go";
    private static final String STOP = "stop";
    private static final String UCI = "uci";
    private static final String READY = "isready";
    private static final String NEWGAME = "ucinewgame";
     
     
    public UCI(Engine engine) {
    	this.engine = engine;
        try{
            initKdoList();
            engineIn = new BufferedReader(
                            new InputStreamReader(System.in));
            start();
             
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
     
    public void initKdoList() {
        kdo = new HashMap<>();
        kdo.put("uci", "uciok");
        kdo.put("isready", "readyok");
        kdo.put("ucinewgame", null);
        kdo.put("position", null);
        kdo.put("go", null);
         
    }
     
    public void start() throws IOException{
        while(true) {
            cmd = engineIn.readLine();
            if (cmd.startsWith("quit"))
                System.exit(0);
            
            String [] result = cmd.split(" ");
            for(int i = 0; i < result.length; i++) {
            	switch(result[i]) {
            	case POS:
            		Parser.position(cmd, this.engine);
            		break;
            	case GO:
            		Parser.go(cmd, this.engine);
            		break;
            	case STOP:
            		Parser.stop(this.engine);
            		break;
            	case UCI:
            		Parser.uci();
            		break;
            	case READY:
            		Parser.isReady();
            		break;
            	case NEWGAME:
            		Parser.ucinewgame(this.engine);
            		break;
            		
            		
            	}
            }
             
           /* if (kdo.containsKey(result[0])) {
                if (cmd.equals("uci")){
                    setUCIParameter();
                    sendCmd((String) kdo.get(cmd)); 
                } else if (cmd.equals("isready")) {
                    sendCmd((String) kdo.get(cmd));
                } else if (cmd.equals("ucinewgame")) {
                    kdo.get(cmd);
                } else if (cmd.startsWith("position")) {
                    String fenString;
                    List<String> moves = new ArrayList<String>();
                    if (result[1].equals("fen") && result.length > 7) {
                        fenString = result[2] + " " + 
                                    result[3] + " " + 
                                    result[4] + " " + 
                                    result[5] + " " + 
                                    result[6] + " " + 
                                    result[7];
                        result[2] = fenString;
                        for (int i = 8; i < result.length; i++) {
                        	moves.add(result[i]);
                        }
                        engine.setBoard(fenString, moves);
                    }
                    else {
                    	for(int i = 3; i < result.length; i++) {
                    		moves.add(result[i]);
                    	}
                    	engine.resetBoard(moves);
                    }
                     
                } else if (cmd.startsWith("go")) {
                	if(cmd.contains("depth")){
                		String[] param = cmd.split("depth ");
                		String[] value = param[1].split(" ");
                		String depth = value[0];
                		engine.search(Integer.parseInt(depth));
                	}
                	else {
                		engine.search(2500);
                	}
                }
            } else
                sendCmd("Command is not supported: " + cmd);    
        }   */
    }
    }
    
         
    /*public void sendCmd(String cmd) {
        System.out.println(cmd);
    }
 
    public void setUCIParameter() {
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
     */
}