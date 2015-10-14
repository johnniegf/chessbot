package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.core.Game;

/**
 * Strategie für Zeitmanagement bei der Zugberechnung.
 * @author Johannes Haupt
 * @author David Holzapfel
 */
public interface TimeStrategy {
    
    long SECOND = 1000L;
    long MINUTE = 60 * SECOND;
    long HOUR   = 60 * MINUTE;
    
    long getMoveTime(final Game gameState);
    
}
