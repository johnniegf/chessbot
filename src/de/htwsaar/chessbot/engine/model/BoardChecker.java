package de.htwsaar.chessbot.engine.model;

/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public interface BoardChecker {

    void check(final Board board);

    boolean isOk(final Board board);

}
