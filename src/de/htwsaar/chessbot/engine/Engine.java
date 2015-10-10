package de.htwsaar.chessbot.engine;

import de.htwsaar.chessbot.engine.config.Config;
import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.eval.Evaluator;
import de.htwsaar.chessbot.engine.io.UCI;
import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.io.Logger;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.search.MoveSearcher;
import de.htwsaar.chessbot.engine.search.PrincipalVariationSearcher;
import de.htwsaar.chessbot.engine.search.SearchWorker;

import java.io.IOException;
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
    
    private Game mGame;
    private final SearchWorker mSearchThread;
    private UCI mUCI;

    public Engine() {
        //Initialize Engine
        Thread.currentThread().setName("chessbot-main");

        //Initialize Game
        mGame = new Game();

        //Initialize Config
        Config.getInstance().init();

        //Initialize UCISender
//        UCISender.getInstance().sendDebug("Initialized UCISender");

        //Initialize move searcher
        mSearchThread = new SearchWorker(
            new PrincipalVariationSearcher(DEFAULT_EVALUATOR)
        );

        //Initialize UCI-Protocoll
        mUCI = new UCI(this);
        mUCI.initialize();
    }
    
    public void start() {
        mSearchThread.start();
        try {
            mUCI.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mGame = new Game();
        mSearchThread.getSearcher().setBoard(mGame.getCurrentBoard());
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
        mSearchThread.getSearcher().setBoard(mGame.getCurrentBoard());
        executeMoves(moves);
    }

    /*
     * erzeugt eine Stellung auf Grund des fens
     * fuehrt die uebergegebenen Zuege aus falls vorhanden.
     */
    public void setBoard(final String fen, final List<String> moves) {
        mGame = new Game(fen);
        mSearchThread.getSearcher().setBoard(mGame.getCurrentBoard());
        executeMoves(moves);
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
        for (int i = 0; i < moves.size(); i++) {
            mGame.doMove(moves.get(i).toString());
        }
    }

	//========================================
    //= go
    //========================================
    public void search(int depth) {
        mSearchThread.getSearcher().getConfiguration().setDepthLimit(depth);
        mSearchThread.startSearching();
    }

    public void searchmoves(List<Move> moves, int depth) {
        mSearchThread.getSearcher().getConfiguration().setMoves(moves);
        mSearchThread.startSearching();
    }

	//========================================
    //= stop
    //========================================
    public void stop() {
        mSearchThread.stopSearching();
    }
    
    public boolean isSearching() {
        return false;
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
        Engine chessbot = new Engine();
        chessbot.start();
    }

}
