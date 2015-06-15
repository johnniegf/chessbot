package src.de.htwsaar.chessbot.engine.model;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;

/**
 * Fabrik zur Bereitstellung von Schachfiguren-Objekten.
 *
 * @author David Holzapfel
 * @author Dominik Becker
 * @version 0.2
 */
public class Pieces {

    //Fehlermeldungen
    private static final String ERR_INVALID_PIECETYPE =
            "Fehler: Ungueltiger Figurentyp: %d";
    private static final String ERR_INVALID_POSITION =
            "Fehler: Position ist 'null'";

    //Konstanten für jeden der 6 Figurentypen
    public static final int PAWN = 0;
    public static final int ROOK = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;

    //Klasseninstanz
    private static Pieces INSTANCE;

    //Array, das einen Prototypen jedes Figurentyps enthält
    private Piece[] prototypes;
    //Liste mit allen bisher erstellten Figurenobjekten
    private ArrayList<Piece> existingPieces;

    //Privater Konstruktor, da die Klasse als Singleton implementiert ist.
    private Pieces() {
        this.prototypes = new Piece[6];
        this.existingPieces = new ArrayList<Piece>();
    }

    /**
     * @return Die Klasseninstanz der Fabrik
     */
    public static Pieces getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Pieces();
        }

        return INSTANCE;
    }

    /**
     * Prüft, ob ein Exemplar einer Schachfigur existiert und erstellt bei
     * Bedarf ein neues Objekt.
     *
     * @param pieceType Figurentyp
     * @param position  Position auf dem Schachbrett
     * @param isWhite   Farbe der Schachfigur
     * @param hasMoved  Ob die Figur bereits bewegt wurde
     * @return  Ein Figurenobjekt mit den angegebenen Parametern
     */
    public Piece getPiece(int pieceType, Position position, boolean isWhite, boolean hasMoved)
        throws InvalidArgumentException {
        if(pieceType < 0 || pieceType > 5) {
            throw new InvalidArgumentException(String.format(ERR_INVALID_PIECETYPE, pieceType));
        }
        if(position == null) {
            throw new InvalidArgumentException(ERR_INVALID_POSITION);
        }

        Piece piece = getExistingPiece(pieceType, position, isWhite, hasMoved);
        if(piece == null) {
            piece = this.getPrototypeCopy(pieceType);
            piece.setPosition(position);
            piece.setIsWhite(isWhite);
            piece.setHasMoved(hasMoved);
            this.existingPieces.add(piece);
        }
        return piece;
    }

    //Prüft, ob ein Figurenobjekt bereits existiert. Ist das der Fall wird es zurückgegeben, wenn nicht wird
    //null zurückgegeben.
    private Piece getExistingPiece(int pieceType, Position position, boolean isWhite, boolean hasMoved)
        throws InvalidArgumentException {
        if(pieceType < 0 || pieceType > 5) {
            throw new InvalidArgumentException(String.format(ERR_INVALID_PIECETYPE, pieceType));
        }
        if(position == null) {
            throw new InvalidArgumentException(ERR_INVALID_POSITION);
        }

        Piece foundPiece = null;
        for(int i = 0; i < existingPieces.size() && foundPiece == null; i++) {
            Piece piece = existingPieces.get(i);
            if(piece.getPosition().equals(position) && piece.isWhite() == isWhite && piece.hasMoved() == hasMoved) {
                foundPiece = piece;
            }
        }

        return foundPiece;
    }

    //Erzeugt eine Kopie des Prototypen von einem bestimmten Figurentyp
    private Piece getPrototypeCopy(int pieceType) throws InvalidArgumentException {
        if(pieceType < 0 || pieceType > 5) {
            throw new InvalidArgumentException(String.format(ERR_INVALID_PIECETYPE, pieceType));
        }

        if(this.prototypes[pieceType] == null) {
            Piece newPrototype;
            switch(pieceType) {
                case PAWN:
                    newPrototype = new Pawn();
                    break;
                case ROOK:
                    newPrototype = new Rook();
                    break;
                case KNIGHT:
                    newPrototype = new Knight();
                    break;
                case BISHOP:
                    newPrototype = new Bishop();
                    break;
                case QUEEN:
                    newPrototype = new Queen();
                    break;
                case KING:
                    newPrototype = new King();
                    break;
            }
            this.prototypes[pieceType] = newPrototype;
        }

        return this.prototypes[pieceType].clone();
    }

}