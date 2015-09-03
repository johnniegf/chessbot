package de.htwsaar.chessbot.engine.model;
 
import java.io.IOException;
 
import de.htwsaar.chessbot.engine.model.GameTree.Node;
 
/**
 * 
 * @author David Holzapfel
 * @author Dominik Becker
 *
 */
 
public class AlphaBetaSearch implements Runnable {
 
     
    public static void main(String[] args) throws IOException {
        Game game = new Game(
                "8/2p1pp2/8/4k3/8/1Q6/PPP4P/RN6 b - - 0 1"
                );
        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(game);
        alphaBetaSearch.setMaxSearchDepth(10);
        new Thread(alphaBetaSearch).start();
         
        System.in.read();
         
        alphaBetaSearch.stop();
    }
     
     
    private Game game;
    private GameTree gameTree;
    private volatile Move currentBestMove;
    private int maxSearchDepth;
    private int currentMoveNumber = 0;
    private boolean exitSearch = false;
    private long startTime = 0;
     
    /**
     * Erstellt einen BestMove-Sucher der mit AlphaBeta Search arbeitet.
     * 
     * @param game  GameState
     */
    public AlphaBetaSearch(Game game) {
        this.game = game;
    }
     
    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        startAlphaBeta();
        System.out.println("bestmove " + this.getCurrentBestMove().toString());

    }
     
    public void stop() {
        this.setExitSearch(true);
         
        if(this.getCurrentBestMove() != null) {
            System.out.println("bestmove " + this.getCurrentBestMove().toString());
        }
         
        Thread.currentThread().interrupt();
    }
     
    /**
     * Setzt die maximale Suchtiefe für den Baum.
     * 
     * @param depth
     */
    public synchronized void setMaxSearchDepth(int depth) {
        this.maxSearchDepth = depth;
    }
     
    /**
     * Setzt eine Flag, die den Algorithmus dazu bewegt, so bald
     * wie moeglich abzubrechen.
     * 
     * @param exitSearch
     */
    public synchronized void setExitSearch(boolean exitSearch) {
        this.exitSearch = exitSearch;
    }
     
    /**
     * Gibt den zum jetztigen Zeitpunkt besten Zug zurueck, der
     * ermittelt wurde.
     * 
     * @return
     */
    public synchronized Move getCurrentBestMove() {
        return this.currentBestMove;
    }
     
    //Setzt den bisher besten Zug
    private synchronized void bestMove(Move move) {
        this.currentBestMove = move;
    }
     
    private void sendInfo(int currentDepth) {
        String bestMoveString = "none";
        if(this.currentBestMove != null) {
            bestMoveString = this.currentBestMove.toString();
        }
        long timeSpent = System.currentTimeMillis() - this.startTime;
        System.out.println(
            "info currmove " + bestMoveString + 
            " currmovenumber " + this.currentMoveNumber +
            " depth " + currentDepth +
            " time " + timeSpent
            );
    }
     
    //Startet die Suche
    private void startAlphaBeta() {
        this.currentBestMove = null;
        this.exitSearch = false;
        for(int i = 1; i <= maxSearchDepth && !this.exitSearch; i++) {           
            this.gameTree = new GameTree(this.game.getCurrentBoard());
            this.currentMoveNumber = 0;
            alphaBeta(i, gameTree.getRoot(), true,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
            sendInfo(i);
        }
    }
     
    private void alphaBeta(final int depth, Node currentNode,
            boolean isMax, int alpha, int beta) {
         
        if(this.exitSearch) {
            return;
        }
         
        if(depth == 0) {
            currentNode.setScore(
                    gameTree.getEvaluationFunction().evaluate(currentNode.getBoard()));
        } else {
            if(isMax) {
                currentNode.setScore(Integer.MIN_VALUE);
            } else {
                currentNode.setScore(Integer.MAX_VALUE);
            }
             
            for(Move move : currentNode.getBoard().getMoveList()) {
                 
                if(currentNode == gameTree.getRoot()) {
                    currentMoveNumber++;
                }
                 
                if(this.currentBestMove == null && currentNode == gameTree.getRoot()) {
                    this.currentBestMove = move;
                }
                 
                Board board = move.execute(currentNode.getBoard().clone());
                Node childNode = new Node(board);
                currentNode.addChild(childNode);
                 
                alphaBeta(depth - 1, childNode, !isMax, alpha, beta);
                 
                if(isMax) {
                    if(childNode.getScore() > beta) {
                        break;
                    }
                    if(childNode.getScore() > currentNode.getScore()) {
                        currentNode.setScore(childNode.getScore());
                        if(currentNode == gameTree.getRoot()) {
                            bestMove(move);
                        }
                        if(currentNode.getScore() > alpha) {
                            alpha = currentNode.getScore();
                        }
                    }
                } else {
                    if(childNode.getScore() < alpha) {
                        break;
                    }
                    if(childNode.getScore() < currentNode.getScore()) {
                        currentNode.setScore(childNode.getScore());
                        if(currentNode.getScore() < beta) {
                            beta = currentNode.getScore();
                        }
                    }
                }
            }
        }
    }
}