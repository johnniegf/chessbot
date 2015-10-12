/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.io.UCISender;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public final class SearchWorker
           extends Thread
{

    private MoveSearcher mSearcher;
    private boolean mExit = false;
    private boolean mSearching = false;
    private boolean mSearcherDone = true;
    
    public SearchWorker(MoveSearcher searcher) {
        setSearcher(searcher);
    }
    
    public MoveSearcher getSearcher() {
        return mSearcher;
    }
    
    public void setSearcher(final MoveSearcher searcher) {
        checkNull(searcher);
        mSearcher = searcher;
        super.setName(searcher.name());
    }
    
    public boolean isSearching() {
        return mSearching;
    }
    
    public void startSearching() {
        mSearching = true;
    }
    
    public void stopSearching() {
        mSearching = false;
        mSearcher.stop();
    }
    
    public boolean isSearcherDone() {
        return mSearcherDone;
    }
    
    public void quit() {
        stopSearching();
        mExit = true;
    }
    
    @Override
    public void run() {
        while (true) {
            while (!isSearching()) {
                try {
                    Thread.sleep(50);
                } catch(InterruptedException ire) {
                    // ignore
                }
                if (mExit)
                    return;
            }
            mSearcherDone = false;
            mSearcher.go();
            infoHash(mSearcher.getHashTable().usage());
//            if (!mSearcher.getConfiguration().isPondering())
            infoBestmove(mSearcher.getBestMove(), mSearcher.getPonderMove());
            mSearcherDone = true;
            stopSearching();
        }
    }
    
    private static void infoBestmove(Move bestMove, Move ponder) {
        String message = "bestmove " + bestMove;
        if (ponder != null)
            message += " ponder " + ponder;
        UCISender.getInstance().sendToGUI(message);
    }
    
    private static void infoHash(int usage) {
        UCISender.getInstance().sendToGUI("info hashfull " + usage);
    }
}
