package de.htwsaar.chessbot.search;

import de.htwsaar.chessbot.core.Game;
import de.htwsaar.chessbot.search.eval.EvaluationFunction;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.moves.Move;

/**
 * Schnittstelle für Suchalgorithmen.
 * @author Johannes Haupt
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
    
    TranspositionHashTable getHashTable();

    /**
     * Leere die Transpositionstabelle des Suchers.
     */
    void clearHashTables();

    /**
     * Gib den berechneten besten Zug für die aktuelle Stellung zurück.
     *
     * @return den besten Zug für die aktuelle Stellung.
     */
    Move getBestMove();
    
    /**
     * Gib den Zug zurück, über den die Engine nachdenken möchte.
     * @return Ponder-Zug.
     */
    Move getPonderMove();
    
    /**
     * Gib die zu untersuchende Stellung zurück.
     * @return die zu untersuchende Stellung.
     */
    Board getBoard();
    
    /**
     * Gib den aktuellen Stand der zu untersuchenden Partie zurück.
     * @return die zu untersuchende Partie.
     */
    Game getGame();
    
    /**
     * Lege den Stand der zu untersuchenden Partie fest.
     * @param game die zu untersuchende Partie.
     */
    void setGame(final Game game);

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
