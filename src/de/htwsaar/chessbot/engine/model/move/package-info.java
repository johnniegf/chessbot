/**
 * Schachzüge.
 *
 * <p>
 * In chessbot sind Züge in eigene Objekte und Klassen aufgeteilt. Die meisten
 * Züge können durch die Klasse <code>StandardMove</code> beschrieben werden,
 * da die Objekte der Klasse auch das Schlagen von Figuren ermöglichen.
 * Lediglich für die Sonderzüge des Königs (Rochade) und des Bauern existieren
 * separate Klassen.
 * </p>
 *
 * <p>
 * Zugobjekte werden durch das Schachbrett erstellt, indem die Zugmöglichkeiten
 * aller Figuren des ziehenden Spielers durchgegangen werden. Züge können nicht
 * a priori - d.h. ohne sie auf der Ausgangsstellung auszuführen - auf
 * Legitimität geprüft werden. Daher haben Zugobjekte zwei Methoden, mit denen
 * sie ausgeführt werden können: <code>execute(Board)</code> und <code>
 * tryExecute(Board)</code>. Erstere wird bei ungültigen Zügen mit dem Werfen
 * einer Ausnahme abbrechen, wogegen letztere <code>null</code> zurückgibt.
 * Für Anwendungsfälle, in denen Performance weniger wichtig als Korrektheit
 * ist, wird empfohlen, <code>isPossible(Board)</code> zu verwenden.
 * </p>
 */
package de.htwsaar.chessbot.engine.model.move;
