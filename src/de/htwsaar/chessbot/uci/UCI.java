package de.htwsaar.chessbot.uci;

import de.htwsaar.chessbot.Engine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Universal Chess Interface liest die Ausgabe der GUI. ueberprueft ob die Zeile
 * ein gueltiges Kommando beinhaltet. gibt das entsprechende Kommando an den
 * Parser weiter.
 * 
* @author Timo Klein
 * @author Dominik Becker
 * 
*
 */
public class UCI {

    private String cmd;
    private BufferedReader engineIn;
    private Engine engine;
    private Parser mParser;
    private boolean mExit = false;



    /**
     * startet die Endlosschleife und kann dauerhaft Kommandos empfangen.
     *
     * @param engine
     */
    public UCI(Engine engine) {
        this.engine = engine;
        mParser = new Parser(engine);
    }

    public void initialize() {
        engineIn = new BufferedReader(new InputStreamReader(System.in));
        mParser.start();
    }

    /**
     * liest die Ausgabe und sendet diese an den Parser weiter, wenn ein
     * gueltiges Kommando dabei war.
     *
     * @throws IOException
     */
    public void start() {
        while (!mExit) {
            try {
                cmd = engineIn.readLine();
                mParser.pushCommand(cmd);
            } catch (IOException ioe) {
                mExit = true;
            }
        }
    }
}
