/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.search;

import de.htwsaar.chessbot.engine.eval.EvaluationFunction;
import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.move.Move;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public interface MoveSearcher {

    /**
     * Obere Schranke aller Bewertungen.
     *
     * @see EvaluationFunction#INFINITE
     */
    int INFINITE = EvaluationFunction.INFINITE;

    /**
     * Gib den Namen dieses Suchers zurück.
     *
     * Der Name eines Suchers sollte sich aus dem Namen des Suchalgorithmus,
     * dem Wort "searcher" und einer eindeutigen Instanznummer zusammensetzen.
     * Einmal vergebene Nummern dürfen auch nach der Deallokation des Objekts
     * nicht neu vergeben werden.
     *
     * @return eindeutiger Name dieses Suchers.
     */
    String name();
    
    HashTable getHashTable();

    /**
     * Leere die Transpositionstabelle des Suchers.
     */
    void clearHashTable();

    /**
     * Gib den berechneten besten Zug für die aktuelle Stellung zurück.
     *
     * @return den besten Zug für die aktuelle Stellung.
     */
    Move getBestMove();

    /**
     * Lege die Ausgangsstellung für die Suche fest.
     *
     * @param board Ausgangsstellung für die nächste Suche.
     */
    void setBoard(final Board board);

    /**
     * Setze die Konfiguration des Suchers zurück.
     */
    void resetConfiguration();

    /**
     * Gib die Konfiguration der aktuellen Suche zurück.
     *
     * @return die Suchkonfiguration.
     */
    SearchConfiguration getConfiguration();

    /**
     * Gib zurück, ob eine Suche läuft.
     *
     * @return true genau dann wenn eine Suche läuft.
     */
    boolean isSearching();

    /**
     * Beende die Suche sobald wie möglich.
     */
    void stop();

    /**
     * Beginne die Suche.
     */
    void go();
}
