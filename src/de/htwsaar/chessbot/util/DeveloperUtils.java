/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.util;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class DeveloperUtils {
    
    private DeveloperUtils() {
        
    }
    
    private static final Output stdout = new PrintStreamOutput(System.out);
    private static final Output stderr = new PrintStreamOutput(System.err);
    
    public static boolean DEBUG_ENABLED = true;
    
    public static void MESSAGE(final Object message) {
        if (DEBUG_ENABLED)
            stdout.printf(MESSAGE_TEMPLATE, MSG, message);
        else
            stdout.println(message);
    }
    
    public static void WARN(final Object message) {
        stderr.printf(MESSAGE_TEMPLATE, WRN, message);
    }
    
    public static void DEBUG(final Object message) {
        if (DEBUG_ENABLED)
            stdout.printf(MESSAGE_TEMPLATE, DBG, message);
    }
    
    public static void ERROR(final Object message) {
        stderr.printf(MESSAGE_TEMPLATE, ERR, message);
    }
    
    private static final String MESSAGE_TEMPLATE = "[%s] %s" + Output.NEWLINE;
    private static final String ERR = "ERR";
    private static final String WRN = "WRN";
    private static final String MSG = "MSG";
    private static final String DBG = "DBG";
    
    
            
            
    
}
