package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;

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
public final class Pawn extends Piece {

    public Pawn() {
        super();
    }

    public Pawn(final Position position) {
        super(position);
    }

    public Pawn(final Position position, 
                final boolean isWhite) 
    {
        super(position, isWhite);
    }

    public Pawn(final Position position, 
                final boolean isWhite, 
                final boolean hasMoved) 
    {
        super(position, isWhite, hasMoved);
    }

    public final String getName() {
        return "Bauer";
    }

    public final String toSAN() {
        return "";
    }

    protected final String getFEN() {
        return "P";
    }

    public final boolean attacks(final Position targetPosition,
                                 final Board context)
    {
        return getAttacks(context).contains(targetPosition);
    }

    private Collection<Move> makeMove(boolean conversion, Position targetSquare) {
        Collection<Move> moves = new LinkedList<Move>();
        if (conversion) {
            Piece[] types = new Piece[]{ 
                new Bishop(), new Knight(), new Rook(), new Queen() 
            };
            for (Piece type : types) 
                moves.add(new PawnConversion(this, targetSquare, type));
        } else {
            moves.add(new Move(this, targetSquare));
        }
        return moves;
    }

    public final Collection<Move> getMoveList(final Board context) {
        Collection<Move> moveList = new LinkedList<Move>();
        if (context.isWhiteMoving() != isWhite())
            return moveList;

        boolean isConversion;
        int increment = increment(); 
        Position p = getPosition();
        Position pn = p.transpose(0,increment);
        isConversion = isWhite() && pn.getRow() == context.getHeight()
                    || !isWhite() && pn.getRow() == 1;

        if ( pn.existsOn(context) && context.isFree(pn) ) {
            moveList.addAll(makeMove(isConversion, pn));
            if (!hasMoved()) {
                pn = p.transpose(0, 2*increment);
                if (context.isFree(pn))
                    moveList.add(new DoublePawnMove(this, pn));
            }
        }   

        Collection<Position> attacks = getAttacks(context);
        for (Position att : attacks) {
            if (context.isFree(att)) {
                if (att.equals(context.enPassant()))
                    moveList.add(new EnPassant(this, att));
            } else {
                if (context.pieceAt(att).isWhite() != isWhite())
                    moveList.addAll(makeMove(isConversion, att));
            }
        }

        return moveList;
    }

    public final Collection<Position> getValidTargets(final Board context) {
        Collection<Position> possibleMoves = new ArrayList<Position>(4);
        int increment = increment(); 
        Position p = getPosition();
        Position pn;

        // Zugmöglichkeiten prüfen
        pn = p.transpose(0,increment);
        if ( context.isFree(pn) ) {
            possibleMoves.add(pn);
            if (!hasMoved()) {
                pn = p.transpose(0, 2*increment);
                if (context.isFree(pn))
                    possibleMoves.add(pn);
            }
        }   

        possibleMoves.addAll(getValidHits(context));

        return possibleMoves;
    }

    private Collection<Position> getAttacks(final Board context) {
        Collection<Position> possibleMoves = new ArrayList<Position>(2);
        int increment = increment();
        Position[] attacks = new Position[] { 
            getPosition().transpose(-1, increment),
            getPosition().transpose(1, increment)
        };
        for (Position p : attacks) {
            if (p.existsOn(context)) {
                possibleMoves.add(p);
            }
        }
        return possibleMoves;

    }

    private Collection<Position> getValidHits(final Board context) {
        Collection<Position> canHit = getAttacks(context);
        
        Iterator<Position> it = canHit.iterator();
        while(it.hasNext()) {
            Position p = it.next();
            if (context.isFree(p) && !p.equals(context.enPassant()) || 
                !context.isFree(p) && context.pieceAt(p).isWhite() == isWhite()
            )
            {
                it.remove();
            }
        }

        return canHit;
    }

    private int increment() {
        return (isWhite() ? 1 : -1);
    }

    public int hashCode() {
        return super.hashCode() * (hasMoved() ? 61 : 67);
    }

    public boolean equals(final Object other) {
        return super.equals(other);
    }

    public final Pawn clone() {
        return new Pawn(getPosition().clone(), isWhite(), hasMoved());
    }
}
