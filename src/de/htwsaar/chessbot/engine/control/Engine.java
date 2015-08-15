package de.htwsaar.chessbot.engine.control;

import static de.htwsaar.chessbot.util.Exceptions.*;

import de.htwsaar.chessbot.engine.model.Game;

import java.util.Map;
import java.util.HashMap;

/**
* Hauptfassade der Engine.
*
* @author Johannes Haupt
*/
public class Engine {
    
    public static final String NAME = "chessbot";
    public static final String AUTHOR = "Projektgruppe Schachengine 2015";
    
    public static Engine getInstance() {
        if ( !initialized() )
            init();
        return sTheEngine;
    }

    private static boolean initialized() {
        return sInitialized;
    }

    private static void init() {
        sTheEngine = new Engine();
        sInitialized = true;
    }

    private static boolean sInitialized = false;
    private static Engine  sTheEngine   = null;


    private Game mCurrentGame;
    private Map<String,Option<?>> mOptions;

    /**
    * Standardkonstruktor.
    */ 
    private Engine() {
        mOptions = new HashMap<String,Option<?> >();         
    }

    public String name() {
        return NAME;
    }

    public String author() {
        return AUTHOR;
    }

    public Game currentGame() {
        return mCurrentGame;
    }

    public void newGame() {
        mCurrentGame = new Game();       
    }

    public <T> T getOption(final String name) {
        try {
            Option<T> opt = (Option<T>) mOptions.get(name);
            return opt.getValue();
        } catch (ClassCastException cce) {
            return null;
        }
    }

    public <T> void setOption(final String name, final T value) {
        
    }

    public void setBoard(final String fenString) {
        setBoard(fenString, new String[0]);
    }

    public void setBoard(final String fenString, final String[] moves) {
        checkNull(fenString, "fenString");
        checkNull(moves, "moves");

        mCurrentGame = new Game(fenString);
        for (String moveString : moves) {
            if ( !move(moveString) )
                break;
        }
    }

    public boolean move(final String moveString) {
        return mCurrentGame.doMove(moveString);
    }

    public static abstract class Option<T> {
        private final String mName;
        private T mValue;

        public Option(final String name) {
            checkNull(name,"name");
            mName = name;
        }

        public Option(final String name, final T value) {
            checkNull(name,"name");
            mName = name;
            setValue(value);
        }

        public T getValue() {
            return mValue;
        }

        public void setValue(final T value) {
            checkNull(value, "value");
            this.mValue = value;
        }

        public String getName() {
            return mName;
        }

    }

    private static final class Switch extends Option<Boolean> {

        public Switch(final String name) {
            super(name, false);
        }

        public void setValue(final boolean value) {
            super.setValue(value);
        }

    }

}
