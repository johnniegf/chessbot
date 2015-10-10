/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.io.UCISender;
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
    }
    
    public void quit() {
        mExit = true;
    }
    
    @Override
    public void run() {
        while (!mExit) {
            while (!mSearching) {
                try {
                    Thread.sleep(50);
                } catch(InterruptedException ire) {
                    // ignore
                }
            }
            mSearcher.go();
            UCISender.getInstance().sendToGUI("bestmove " + mSearcher.getBestMove());
            UCISender.getInstance().sendToGUI("hashfull " + mSearcher.getHashTable().usage());
            stopSearching();
        }
    }
}
