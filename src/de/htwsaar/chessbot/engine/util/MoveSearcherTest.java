/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.eval.Evaluator;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.BoardUtils;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.search.NegaMaxSearcher;
import de.htwsaar.chessbot.engine.search.PrincipalVariationSearcher;
import de.htwsaar.chessbot.engine.search.SearchWorker;
import static de.htwsaar.chessbot.util.DeveloperUtils.MESSAGE;
import static de.htwsaar.chessbot.util.DeveloperUtils.PRINT;
import static de.htwsaar.chessbot.util.DeveloperUtils.SEPARATE;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class MoveSearcherTest {
    
    
    public static void main(String[] args) {
        EvaluationFunction eval = new Evaluator();
        SearchWorker searcher = new SearchWorker(new PrincipalVariationSearcher(eval));
//        SearchWorker searcher = new SearchWorker(new NegaMaxSearcher(eval));
        Board initial = Board.B();
        Board current = initial;
        searcher.start();
        while (true) {
            SEPARATE();
            PRINT(current);
            if (BoardUtils.isDraw(current)) {
                MESSAGE("Draw");
                break;
            }
            if (BoardUtils.isMate(current)) {
                MESSAGE("Mate");
                break;
            }
            if (BoardUtils.isStalemate(current)) { 
                MESSAGE("Stalemate");
                break;
            }
            searcher.getSearcher().setBoard(current);
            searcher.getSearcher().resetConfiguration();
            searcher.getSearcher().getConfiguration().setDepthLimit(4);
            long started = System.currentTimeMillis();
            searcher.startSearching();
            do {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ire) {

                }
                if (!searcher.isSearching())
                    break;
            } while (true);
            MESSAGE(System.currentTimeMillis() - started);
            Move bestMove = searcher.getSearcher().getBestMove();
            current = bestMove.execute(current);
            PRINT("");
        }
        try {
            searcher.quit();
            searcher.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MoveSearcherTest.class.getName()).
                log(Level.SEVERE, null, ex);
        }
    }
}
