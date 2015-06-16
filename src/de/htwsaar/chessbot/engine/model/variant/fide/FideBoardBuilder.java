package de.htwsaar.chessbot.engine.model.variant.fide;

import static de.htwsaar.chessbot.engine.model.Position.*;
import de.htwsaar.chessbot.engine.model.*;

import java.util.*;
import java.util.regex.*;

/**
* Beschreibung.
*
* @author
*/
public class FideBoardBuilder implements BoardBuilder {

    private static final ChessVariant VARIANT = new FideChess();

    private Collection<Piece> pieces;

    public Board getStartingPosition() {
        return fromFenString(DEFAULT_FEN);
    }

    public Board fromFenString(final String fenString) {
        if (!fenString.matches(REGEX_FEN_STRING))
            throw new FenStringParseException(EXN_BAD_FORMAT);

        Board result = VARIANT.getBoard();
        
        int length = fenString.length();
        char current;
        Position currentPosition = P("h1");
        String[] fields = fenString.split(" ");
        String[] rows = fields[0].split("/");
        if (rows.length != 8) {
            throw new FenStringParseException(EXN_ILLEGAL_ROW_COUNT);
        }
        for (int y = 0; y < 8; y++) {
            int width = 0;
            for (int x = 0; x < rows[y].length(); x++) {
                current = rows[y].charAt(x);
                if (Character.isLetter(current)) {
                    Piece p = VARIANT.getPieceFactory().getPiece(""+current, currentPosition, false);
                    if (p == null)
                        throw new FenStringParseException(EXN_UNKOWN_PIECE);
                    result.addPiece(p);
                    width += 1;

                } else if (Character.isDigit(current)) {
                    int empty = Character.getNumericValue(current);
                    width += empty;
                    currentPosition = currentPosition.transpose(empty, 0);
                } else {
                    throw new FenStringParseException(EXN_ILLEGAL_CHARACTER);
                }
            }
            if (width != 8) {
                throw new FenStringParseException(EXN_ILLEGAL_COL_COUNT);
            }
            currentPosition = currentPosition.transpose(0,1).setColumn(0);
        }

        boolean whiteMoves = whoMoves(fields[1]);
        whatCastlings(fields[2], result);
        Position ep = enPassant(fields[3]);
        int halfMoves = getNumber(fields[4]);
        int fullMoves = getNumber(fields[5]);
        return null;
    }

    private static boolean whoMoves(String color) {
        switch(color) {
            case "w": return true;
            case "b": return false;
            default: return false;
        }
    }

    private static void whatCastlings(String castlings, Board board) {
        
    }

    private static Position enPassant(String source) {
        if (source != "-")
            return new Position(source);
        else
            return Position.INVALID;
    }

    private static int getNumber(String source) {
        return Integer.valueOf(source);
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    private static final String REGEX_FEN_STRING = 
        "[BbKkNnPpQqRr1-8]+(/[BbKkNnPpQqRr1-8])+ " +
        "[bw] (-|[KQkq]{1,4}) (-|[a-h][1-8]) \\d{1,2} \\d{1,2}";

    private static final String DEFAULT_FEN = 
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final String EXN_ILLEGAL_COL_COUNT =
        "Ungültige Spaltenanzahl, erwarte 8. ";
    private static final String EXN_ILLEGAL_ROW_COUNT =
        "Ungültige Zeilenanzahl, erwarte 8. ";
    private static final String EXN_UNKOWN_PIECE =
        "Unbekanntes Figurenkürzel ";
    private static final String EXN_ILLEGAL_CHARACTER =
        "Unbekanntes Zeichen ";
    private static final String EXN_BAD_FORMAT =
        "Unzuässiges Format für FEN-String.";

}
