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
            engineIn = new BufferedReader(
                            new InputStreamReader(System.in));
            start();
            
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
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
        } 
    }
}