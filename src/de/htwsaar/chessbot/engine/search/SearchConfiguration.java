/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.model.move.Move;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Konfiguration für MoveSearcher.
 *
 * Optionen für die Suche können dem "go"-Kommando im UCI-Protokoll
 * mitgegeben werden
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class SearchConfiguration {

    private int  mMaxDepth;
    private long mMaxNodes;
    private long mMaxTime;
    private long mTimeStarted;
    private boolean mInfinite;
    private boolean mPonder;
    private List<Move> mMoves;
    
    private static final int DEPTH_HARD_LIMIT = 50;
    private static final long TIME_HARD_LIMIT = 30000;

    public SearchConfiguration() {
        reset();
    }

    public final boolean shouldStop(final int depth, final long nodes) {
        if (isInfinite()) return false;
        if (isPondering()) return false;
        if (getNodeLimit() > 0 && nodes > getNodeLimit())
            return true;
        if ((getDepthLimit() > 0 && depth > getDepthLimit()) || depth > DEPTH_HARD_LIMIT)
            return true;
        if ((nodes & TIMEOUT_INTERVAL) == 0L)
            return isTimeOut();
        else
            return false;
    }
    
    private static final long TIMEOUT_INTERVAL = 2047;

    public final void reset() {
        mMaxDepth    = 0;
        mMaxNodes    = 0L;
        mMaxTime     = 0L;
        mTimeStarted = 0L;
        mMoves       = new LinkedList<>();
        mInfinite    = false;
        mPonder      = false;
    }

    public final void prepareForSearch() {
        mTimeStarted = System.currentTimeMillis();
    }

    public final boolean isTimeOut() {
        return !isInfinite()
            && getTimeLimit() > 0
            &&  (getElapsedTime() > getTimeLimit()
              || getElapsedTime() > TIME_HARD_LIMIT);
    }
    
    public long getElapsedTime() {
        return System.currentTimeMillis() - mTimeStarted;
    }

    public int getDepthLimit() {
        return mMaxDepth;
    }

    public long getNodeLimit() {
        return mMaxNodes;
    }

    public long getTimeLimit() {
        return mMaxTime;
    }
    
    public long getTimeStarted() {
        return mTimeStarted;
    }

    public List<Move> getMoves() {
        return mMoves;
    }

    public boolean isInfinite() {
        return mInfinite;
    }

    public boolean isPondering() {
        return mPonder;
    }

    public void setDepthLimit(final int maxDepth) {
        checkInBounds(maxDepth, 0, Integer.MAX_VALUE);
        mMaxDepth = maxDepth;
    }

    public void setNodeLimit(final long maxNodes) {
        checkInBounds(maxNodes, 0L, Long.MAX_VALUE);
        mMaxNodes = maxNodes;
    }

    public void setTimeLimit(final long maxTime) {
        checkInBounds(maxTime, 0L, Long.MAX_VALUE);
        mMaxTime = maxTime;
    }

    public void setMoves(final List<Move> moves) {
        checkNull(moves);
        mMoves = moves;
    }

    public void setInfinite(final boolean isInfinite) {
        mInfinite = isInfinite;
    }

    public void setPonder(final boolean shouldPonder) {
        mPonder = shouldPonder;
    }
    
    public void set(final SearchConfiguration config) {
        setDepthLimit(config.getDepthLimit());
        setTimeLimit(config.getTimeLimit());
        setNodeLimit(config.getNodeLimit());
        setInfinite(config.isInfinite());
        setMoves(config.getMoves());
        setPonder(config.isPondering());
    }

}
