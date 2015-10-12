package de.htwsaar.chessbot;

import de.htwsaar.chessbot.core.Game;
import de.htwsaar.chessbot.config.Config;
import de.htwsaar.chessbot.search.eval.EvaluationFunction;
import de.htwsaar.chessbot.search.eval.Evaluator;
import de.htwsaar.chessbot.uci.UCI;
import de.htwsaar.chessbot.uci.UCISender;
import de.htwsaar.chessbot.uci.Logger;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.search.MoveSearcher;
import de.htwsaar.chessbot.search.NegaMaxSearcher;
import de.htwsaar.chessbot.search.PrincipalVariationSearcher;
import de.htwsaar.chessbot.search.SearchConfiguration;
import de.htwsaar.chessbot.search.SearchWorker;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */
public class Engine {

    private static final EvaluationFunction DEFAULT_EVALUATOR =
            new Evaluator();
    private static final String OPT_SEARCH = "SearchAlgorithm";
    private static final String OPT_SEARCH_VAL_PVS = "PrincipalVariation";
    private static final String OPT_SEARCH_VAL_NEGAMAX = "NegaMax";
    private static final String[] OPT_SEARCH_VALUES = new String[] {
        OPT_SEARCH_VAL_NEGAMAX, OPT_SEARCH_VAL_PVS
    };
    
    static {
        Config.getInstance().addComboOption(
            OPT_SEARCH, OPT_SEARCH_VAL_PVS, Arrays.asList(OPT_SEARCH_VALUES)
        );
    }
    
    private Game mGame;
    private SearchWorker mSearchThread;
    private final UCI mUCI;

    private MoveSearcher createSearcher(EvaluationFunction eval) {
        String searchType = Config.getInstance().getOption(OPT_SEARCH).getValue().toString().trim();
        switch (searchType) {
            case OPT_SEARCH_VAL_NEGAMAX:
                return new NegaMaxSearcher(eval);
            case OPT_SEARCH_VAL_PVS:
                return new PrincipalVariationSearcher(eval);
            default:
                throw new IllegalStateException();
        }
    }
    
    public Engine() {
        //Initialize Engine
        

        //Initialize Game
        mGame = new Game();

        //Initialize Config
        Config.getInstance();

        //Initialize UCISender
//        UCISender.getInstance().sendDebug("Initialized UCISender");

        //Initialize move searcher
        mSearchThread = new SearchWorker(
            createSearcher(DEFAULT_EVALUATOR)
        );

        //Initialize UCI-Protocoll
        mUCI = new UCI(this);
        mUCI.initialize();
    }
    
    public void start() {
        mSearchThread.start();
        mUCI.start();
    }

    //======================================
    //= uci
    //======================================
    public void uci() {
        UCISender.getInstance().sendToGUI("id name chessbot");
        UCISender.getInstance().sendToGUI("id author grpKretschmer");
        UCISender.getInstance().sendToGUI("uciok");
    }

	//======================================
    //= isready
    //======================================
    public void isready() {
        while (isSearching()) {
            // busy wait
        }
        UCISender.getInstance().sendToGUI("readyok");
    }

    public MoveSearcher getSearcher() {
        return mSearchThread.getSearcher();
    }
    
	//======================================
    //= ucinewgame
    //======================================
	/*
     * erstellt ein neues Spiel.
     */
    public void newGame() {
        stop();
        mGame = new Game();
        mSearchThread.setSearcher( createSearcher(DEFAULT_EVALUATOR) );
    }

    //======================================
    //= position
    //======================================
    /**
     * setzt das Spiel auf die Startstellung
     * fuehrt Zuege aus falls vorhanden.
     */
    public void resetBoard(final List<String> moves) {
        mGame = new Game();
        executeMoves(moves);
//        mSearchThread.getSearcher().setBoard(mGame.getCurrentBoard());
    }
    
    public Game getGame() {
        return mGame;
    }

    /*
     * erzeugt eine Stellung auf Grund des fens
     * fuehrt die uebergegebenen Zuege aus falls vorhanden.
     */
    public void setBoard(final String fen, final List<String> moves) {
        mGame = new Game(fen);
        executeMoves(moves);
//        mSearchThread.getSearcher().setBoard(mGame.getCurrentBoard());
    }
    
    public Board getBoard() {
        return mGame.getCurrentBoard();
    }

    /**
     * fuehrt die uebergebenen Zuege aus.
     *
     * @param moves
     */
    public void executeMoves(final List<String> moves) {
        if (moves == null)
            return;
        for (String moveString : moves) {
            mGame.doMove(moveString);
        }
    }

	//========================================
    //= go
    //========================================
    public void search(final SearchConfiguration config) {
        if (isSearching())
            stop();
//        stop();
//        while (!mSearchThread.isSearcherDone()) {
//            // do a busy wait...
//        }
        getSearcher().setGame(mGame);
        getSearcher().getConfiguration().set(config);
        mSearchThread.startSearching();
    }
    
    public void ponderhit() {
        if (mSearchThread.getSearcher().getConfiguration().isPondering()) {
            mSearchThread.getSearcher().getConfiguration().setPonder(false);
            mSearchThread.getSearcher().getConfiguration().prepareForSearch();
        }
    }

	//========================================
    //= stop
    //========================================
    public void stop() {
        mSearchThread.stopSearching();
//        while (isSearching()) {
//            //busy wait
//        }
    }
    
    public boolean isSearching() {
        return mSearchThread.isSearching() || !mSearchThread.isSearcherDone();
    }
    
    public boolean isReady() {
        return !isSearching();
    }

	//========================================
    //= quit
    //========================================
    /**
     * beendet das Programm
     */
    public void quit() {
        mSearchThread.quit();
        Logger.getInstance().close();
        System.exit(0);
    }

    /*
     * main-Methode der Engine.
     */
    public static void main(String[] args) {
        //UCIManager anlegen
        Thread.currentThread().setName("chessbot-main");
        Engine chessbot = new Engine();
        chessbot.start();
    }

}
