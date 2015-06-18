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
    private static final Pieces FACTORY = VARIANT.getPieceFactory();


    private Collection<Piece> pieces;

    public Board getStartingPosition() {
        return fromFenString(DEFAULT_FEN);
    }

    public Board fromFenString(final String fenString) {
        if (!fenString.matches(REGEX_FEN_STRING))
            throw new FenStringParseException(EXN_BAD_FORMAT + fenString);

        Board    result = VARIANT.getBoard();
        String[] fields = fenString.split(" ");
        String[] rows   = fields[0].split("/");
        
        if (rows.length != 8) {
            throw new FenStringParseException(EXN_ILLEGAL_ROW_COUNT);
        }
        for (int y = 0; y < 8; y++) {
            int rowNumber = 8 - y;
            int width = processRow(rowNumber, rows[y], result);
            if (width != 8) {
                throw new FenStringParseException(EXN_ILLEGAL_COL_COUNT);
            }
        }
        return result;
    }

    private static int processRow(final int rowNumber,
                                  final String row, Board context) 
    {
        Position currentPosition = P(1, rowNumber);
        int width = 0;
        for (int x = 0; x < row.length(); x++) {
            char current = row.charAt(x);
            if (Character.isLetter(current)) {
                Piece p = FACTORY.getPiece(""+current, currentPosition, false);
                if (p == null)
                    throw new FenStringParseException(EXN_UNKOWN_PIECE);
                
                context.addPiece(p);
                width += 1;
                currentPosition = currentPosition.transpose(1, 0);

            } else if (Character.isDigit(current)) {
                int empty = Character.getNumericValue(current);
                width += empty;
                currentPosition = currentPosition.transpose(empty, 0);
            } else {
                throw new FenStringParseException(EXN_ILLEGAL_CHARACTER);
            }
        }
        return width;
        
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
        "([A-Za-z1-8])+(/([a-zA-Z1-8])+){7} .*";
//        "[BbKkNnPpQqRr1-8]+(/[BbKkNnPpQqRr1-8])+ " +
//        "[bw] (-|[KQkq]{1,4}) (-|[a-h][1-8]) \\d{1,2} \\d{1,2}";

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
