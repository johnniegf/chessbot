/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class Clock {
    private long increment;
    private long time;

    public void setTime(long msec) {
    	time = msec;
    }

    public void setIncrement(long msec) {
    	increment = msec;
    }

    public long getTime() {
        return time;
    }

    public long getIncrement() {
        return increment;
    }
}
