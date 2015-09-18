package de.htwsaar.chessbot.engine.model;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
 
/**
*   Universal Chess Interface
*   liest die Ausgabe der GUI.
*   ueberprueft ob die Zeile ein gueltiges Kommando beinhaltet.
*   gibt das entsprechende Kommando an den Parser weiter.
*
*   @author Timo Klein
*   @author Dominik Becker
*   
**/
 
public class UCI  {
 
    private String cmd;
    private BufferedReader engineIn;
    private Engine engine;
    
    private File logFile;
    private Writer logWriter;
    
    //UCI Kommandos
    private static final String POS = "position";
    private static final String GO = "go";
    private static final String STOP = "stop";
    private static final String UCI = "uci";
    private static final String READY = "isready";
    private static final String NEWGAME = "ucinewgame";
    
    private static final String LOG_PATH = "C:/Users/David/Desktop/chessbot.log";
     
    /**
     * startet die Endlosschleife und kann dauerhaft Kommandos empfangen.
     * @param engine
     */
    public UCI(Engine engine) {
    	this.engine = engine;
    	initLog();
        try{
            engineIn = new BufferedReader(
                            new InputStreamReader(System.in));
            start();
            
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void initLog() {
    	try {
    		this.logFile = new File(LOG_PATH);
			this.logWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(logFile), "utf-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    private void log(String msg) throws IOException {
    	String line = msg + "\n";
    	
    	this.logWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(logFile), "utf-8"));
    	logWriter.write(line);
    	logWriter.close();
    }
    
    /**
     * liest die Ausgabe und sendet diese an den Parser weiter,
     * wenn ein gueltiges Kommando dabei war.
     * @throws IOException
     */
    public void start() throws IOException{
        while(true) {
            cmd = engineIn.readLine();
            
            if (cmd.startsWith("quit"))
                System.exit(0);
            
            log(cmd);
            
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
