/**
 * Stellt Bewertungsfunktionen für Stellungen bereit.
 *
 * <p>
 * Eine Bewertungsfunktion kann eine Stellung aufgrund verschiedenster
 * Faktoren einstufen. Einfache Exemplare bewerten z.B. anhand der
 * Materialdifferenz, der Bauernstruktur und/oder der Mobilität.
 * </p>
 *
 * <p>
 * <em>Achtung:</em> Aufgrund der Tatsache, dass die Suchalgorithmen in
 * chessbot auf dem <a href="https://en.wikipedia.org/wiki/Negamax">NegaMax</a>
 * Framework basieren, müssen Bewertungsfunktionen Stellungen aus der Sicht
 * des ziehenden Spielers bewerten.
 * </p>
 *
 * @author Dominik Becker
 * @author Johannes Haupt
 * @author David Holzapfel
 */
package de.htwsaar.chessbot.engine.eval;
