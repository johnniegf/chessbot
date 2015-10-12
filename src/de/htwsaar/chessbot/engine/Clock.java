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
    public final long wtime;
    public final long btime;
    public final long winc;
    public final long binc;
    public final int movestogo;

    public Clock() {
        this(0L, 0L, 0L, 0L, 0);
    }
    
    public Clock(long wtime, long btime, long winc, long binc, int movestogo) {
        this.wtime = wtime;
        this.btime = btime;
        this.winc = winc;
        this.binc = binc;
        this.movestogo = movestogo;
    }
}
