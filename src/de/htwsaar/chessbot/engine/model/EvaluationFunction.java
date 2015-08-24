package de.htwsaar.chessbot.engine.model;

/**
* Bewertungsfunktion für Stellungen.
*
* Eine Bewertungsfunktion kann eine Stellung aufgrund vieler verschiedener
* Kriterien bewerten.
*
* @author Johannes Haupt
*/
public interface EvaluationFunction {

    /**
    * Bewerte die übergebene Stellung.
    *
    * @param board die zu bewertende Stellung
    * @return die Bewertung der übergebenen Stellung.
    * @throws NullPointerException falls <code>board == null</code>
    */
    int evaluate(final Board board);

}
