package de.htwsaar.chessbot.engine.model;

/**
* Ein <code>BoardBuilder</code> erzeugt Stellungen für je eine
* Spielvariante.
*
* @author Johannes Haupt
*/
public interface BoardBuilder {
    
    /**
    * Erzeuge eine neue Ausgangsstellung.
    *
    * @return ein neues <code>Board</code> mit der Ausgangsstellung
    *         für den zugehörigen Regelsatz
    */
    Board getStartingPosition();

    /**
    * Erzeuge eine neue Stellung aus der übergebenen FEN-Notation.
    *
    * @param fenString FEN-Notation der zu erzeugenden Stellung
    * @return ein neues <code>Board</code> mit der in FEN-Notation
    *         übergebenen Stellung
    * @throws FenStringParseException falls die übergebene FEN-Notation
    *           falsch oder die beschriebene Stellung unmöglich ist.
    */
    Board fromFenString(String fenString);
    
}
