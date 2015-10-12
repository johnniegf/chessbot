/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.uci.UCISender;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import de.htwsaar.chessbot.util.Output;

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
    
    public synchronized MoveSearcher getSearcher() {
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
        mSearcher.stop();
        mSearching = false;
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
            if (!mSearcher.getConfiguration().isPondering())
                infoBestmove(mSearcher.getBestMove(), mSearcher.getPonderMove());
            else
                infoBestmove(mSearcher.getBestMove(), null);
            mSearcherDone = true;
            infoHash(mSearcher.getHashTable().usage());
            stopSearching();
        }
    }
    
    private static void infoBestmove(Move bestMove, Move ponder) {
        String p = Output.EMPTY_STRING;
        if (ponder != null)
            p = String.format(PONDER, ponder);
        String msg = String.format(BESTMOVE, bestMove, p);
        UCISender.getInstance().sendToGUI(msg, true);
    }
    
    private static void infoHash(int usage) {
        UCISender.getInstance().sendToGUI("info hashfull " + usage);
    }
    
    private static final String BESTMOVE = "bestmove %s %s";
    private static final String PONDER   = "ponder %s";
}
