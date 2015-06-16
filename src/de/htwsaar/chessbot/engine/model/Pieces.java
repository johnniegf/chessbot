package de.htwsaar.chessbot.engine.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
* Figurfabrik.
*
* Erzeugt neue Figuren durch Klonen konfigurierter Prototypen.
*
* @author Dominik Becker
* @author Johannes Haupt
* @author David Holzapfel
*/
public class Pieces {

    private Map<String, Piece> prototypes;
    private Map<String, Map<Position, List<Piece>>> existingPieces;
    
    /**
    * Erzeuge eine Fabrik mit der übergebenen Prototypkonfiguration.
    *
    * @param prototypes Prototypenkonfiguration für diese Fabrik
    * @return neue Fabrik mit übergebener Konfiguration
    * @throws NullPointerException  falls die Liste der übergebenen 
    *           Prototypen <code>null</code> ist
    * @throws PieceFactoryException falls keine Prototypen angegeben wurden
    */
    public static Pieces getFactoryForPrototypes(final Collection<Piece> prototypes) {
        if (prototypes == null)
            throw new NullPointerException(EXN_PROTOTYPES_NULL);
        if (prototypes.size() < 1)
            throw new PieceFactoryException(EXN_NO_PROTOTYPES_SPECIFIED);

        return new Pieces(prototypes);
    }


    /**
    * Erzeuge eine neue Figurfabrik für die übergebenen Prototypen.
    *
    * @param prototypes Prototypen der Fabrik
    */
    private Pieces(final Collection<Piece> prototypes) {
        this.prototypes = new HashMap<String, Piece>();
        this.existingPieces = new HashMap<String, Map<Position, List<Piece>>>();
        
        for (Piece p : prototypes) {
            this.prototypes.put(p.toFEN(), p.clone());
            p.setColor(!p.isWhite());
            this.prototypes.put(p.toFEN(), p.clone());
        }

        for (String fen : this.prototypes.keySet()) {
            Map<Position, List<Piece>> m = new TreeMap<Position, List<Piece>>();
            
            this.existingPieces.put(fen, m);
        }
    }

    /**
    * Erzeuge eine neue Figur aus den übergebenen Parametern.
    *
    * @param fen      FEN-Kürzel der neuen Figur
    * @param position Feld, das die neue Figur besetzt
    * @param hasMoved wurde die Figur bereits gezogen?
    * @return neue Figur mit der angegebenen Konfiguration, oder 
    *         <code>null</code> falls aus der übergebenen Konfiguration
    *         keine Figur erzeugt werden kann.
    */
    public Piece getPiece(final String fen,
                          final Position position,
                          final boolean hasMoved) 
    {
        Position p = (position == null ? Position.INVALID : position);
        Piece piece = getExistingPiece(fen, p, hasMoved);
        if(piece == null) {
            piece = this.getPrototype(fen);
            if (piece != null) {
                piece.setPosition(position);
                piece.setHasMoved(hasMoved);
                this.addExistingPiece(piece);
            }
        }
        return piece;
    }

    /**
    * Suche nach einer gespeicherten Figur mit der übergebenen
    * Konfiguration.
    *
    */
    private Piece getExistingPiece(final String fen, 
                                   final Position position, 
                                   final boolean hasMoved) 
    {
        List<Piece> pieceList = this.existingPieces.get(fen).get(position);
        if (pieceList == null)
            return null;
        for (Piece p : pieceList) {
            if (p.hasMoved() == hasMoved)
                return p;
        }
        return null;
    }

    /**
    * Füge eine neue Figur in den Zwischenspeicher ein.
    */
    private void addExistingPiece(final Piece piece) {
        String fen = piece.toFEN();
        Position p = piece.getPosition();
        List<Piece> target = this.existingPieces.get(fen).get(p);
        if (target == null) {
            target = new ArrayList<Piece>();
            target.add(piece);
            this.existingPieces.get(fen).put(p, target);
        } else 
            target.add(piece);
    }

    /**
    * Gib den gespeicherten Prototyp für den übergebenen FEN-String zurück.
    *
    * @param fen Figurkürzel in FEN-Notation
    * @param den Prototyp der Figur mit dem übergebenen FEN-Kürzel oder
    *        <code>null</code> wenn zu <code>fen</code> keine Figur
    *        existiert
    */
    private Piece getPrototype(String fen) {
        Piece proto = this.prototypes.get(fen);
        if (proto == null)
            return null;
        else
            return proto.clone();
    }

    private static final String EXN_PROTOTYPES_NULL =
        "Die Liste der Prototypen ist null.";
    private static final String EXN_NO_PROTOTYPES_SPECIFIED =
        "Keine Prototypen angegeben.";

}
