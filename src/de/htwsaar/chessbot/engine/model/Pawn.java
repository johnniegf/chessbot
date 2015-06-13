package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Der Bauer.
*
* <ul>
* <li>Der Bauer kann einen Schritt nach vorne ziehen, wenn das Zielfeld 
* leer ist. </li>
*
* <li>Wurde der Bauer noch nicht gezogen und befindet sich somit noch in der 
* Ausgangsstellung, kann er wahlweise auch zwei Schritte vorrücken, 
* sofern das Feld vor ihm und das Zielfeld leer sind.</li>
*
* <li>Der Bauer schlägt vorwärts diagonal ein Feld weit. Ist ein diagonal 
* vor ihm liegendes Feld jedoch leer, kann er nicht darauf ziehen (außer 
* bei einem en-passant-Schlag). Er ist der einzige Spielstein, der in 
* eine andere Richtung schlägt als er zieht.</li>
*
* <li>Der Bauer kann als einziger Spielstein en passant schlagen. Hat ein 
* gegnerischer Bauer im unmittelbar vorausgehenden gegnerischen Halbzug 
* einen Doppelschritt gemacht und steht ein eigener Bauer so, dass er das 
* dabei übersprungene Feld angreift, kann er den gegnerischen Bauern so 
* schlagen, als ob dieser nur ein Feld aus der Ausgangsstellung 
* vorgerückt wäre.</li>
*
* <li>Wenn ein Bauer die gegnerische Grundreihe betritt, so muss er als 
* Bestandteil dieses Zuges in eine Dame, einen Turm, einen Läufer 
* oder einen Springer der eigenen Farbe umgewandelt werden. Der Bauer wird 
* aus dem Spiel genommen, und auf das Feld, auf das der Bauer in diesem Zug 
* gezogen wurde, wird die neue Figur gesetzt. Die Eigenschaften der neuen 
* Figur treten sofort in Kraft, dies kann auch zum unmittelbaren Schachmatt
* führen. Die Umwandlung ist nicht davon abhängig, ob die ausgewählte Figur 
* im Laufe des Spiels geschlagen wurde. Durch Umwandlung kann ein Spieler 
* also mehr Exemplare einer Figurenart bekommen, als in der Grundstellung 
* vorhanden sind.</li>
* </ul>
*
* @author Kevin Alberts
* @author Johannes Haupt
*/
public class Pawn extends Piece {

    public Pawn() {
        super();
    }

    public Pawn(Position position) {
        super(position);
    }

    public Pawn(Position position, boolean isWhite) {
        super(position, isWhite);
    }

    public Pawn(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite, hasMoved);
    }

    public String getName() {
        return "Bauer";
    }

    public String getShortName() {
        return "";
    }

    public Collection<Position> getValidMoves(Board context) {
        List<Position> possibleMoves = new ArrayList<Position>(4);
        int increment = isWhite() ? 1 : -1;
        Position p = getPosition();
        Position pn;
        for (int i = -1; i <= 1; ++i) {
            pn = p.translate(increment,i);
            if (pn.existsOn(context))
                possibleMoves.add(pn);
        }
        if ( !hasMoved() )
            possibleMoves.add( p.translate(2*increment, 0) );

        return possibleMoves;
    }

    public boolean equals(Object other) {
        return super.equals(other);
    }

    public Pawn clone() {
        return new Pawn(getPosition().clone(), isWhite(), hasMoved());
    }
}
