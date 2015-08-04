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
	
    private Collection<Piece> pieces;

    public Board getBoard() {
        return fromFenString(DEFAULT_FEN);
    }

    public Board fromFenString(final String fenString) {
        if (!fenString.matches(REGEX_FEN_STRING))
            throw new FenStringParseException(EXN_BAD_FORMAT + fenString);

        Board    result = ChessVariant.getActive().getBoard();
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
        
        setPlayer(fields[1], result);
        setCastlings(fields[2], result);
        setEnPassant(fields[3], result);
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
                Piece p = ChessVariant.getActive()
                                      .getPieceFactory()
                                      .get(current, currentPosition, false);
                checkForStartingPos(p);
                if (p == null)
                    throw new FenStringParseException(EXN_UNKOWN_PIECE);
                
                context.putPiece(p);
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

    private static boolean setPlayer(final String playerString, final Board context) {
        switch(playerString.trim()) {
            case "w":
                context.setWhiteAtMove(true);
                break;
            case "b":
                context.setWhiteAtMove(false);
                break;
            default:
                throw new FenStringParseException();
        }
        return true;
    }

    private static boolean setCastlings(final String castleString, final Board context) {
        String castlings = castleString.trim();
        if (castlings == "-") {
            for (Piece p : context.getPiecesByType(new King().id())) {
                p.setHasMoved(false);
            }
            return true;
        }
        
        Position kingPos, rookPos;
        for (int i = 0; i < castlings.length(); i++) {
            char c = castlings.charAt(i);
            switch(c) {
                case 'Q':
                    kingPos = P("e1");
                    rookPos = P("a1");
                    break;
                case 'K':
                    kingPos = P("e1");
                    rookPos = P("h1");
                    break;
                case 'q':
                    kingPos = P("e8");
                    rookPos = P("a8");
                    break;
                case 'k':
                    kingPos = P("e8");
                    rookPos = P("h8");
                    break;
                default:
                    rookPos = Position.INVALID;
                    kingPos = Position.INVALID;
            }
            if (rookPos.isValid()) {
                Piece p = context.getPieceAt(rookPos);
                if (p instanceof Rook) {
                    p.setHasMoved(false);
                }
                p = context.getPieceAt(kingPos);
                if (p instanceof King) {
                    p.setHasMoved(false);
                } 
            }

        }
        return true;
    }

    private static void checkForStartingPos(Piece p) {
        if (p instanceof Pawn) {
            if (p.isWhite()) {
                p.setHasMoved( p.getPosition().rank() != 2 );
            } else {
                p.setHasMoved( p.getPosition().rank() != 7 );
            }
        }
        if (p instanceof King) {
            if (p.isWhite()) {
                p.setHasMoved( !p.getPosition().equals(P("e1")) );
            } else {
                p.setHasMoved( !p.getPosition().equals(P("e8")) );
            }
        }
    }

    private static void setEnPassant(final String enPassant, Board context) {
        if (enPassant.trim().equals("-")) {
            context.setEnPassant(Position.INVALID);
        } else {
            context.setEnPassant(P(enPassant.trim()));
        }
    }

    private static final String REGEX_FEN_STRING = 
//        "([A-Za-z1-8])+(/([a-zA-Z1-8])+){7} .*";
        "[BbKkNnPpQqRr1-8]+(/[BbKkNnPpQqRr1-8]+){7} " +
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
    private static final String EXN_TOO_MANY_PIECES =
        "Zu viele Figuren der Art ";
    private static final String EXN_TOO_MANY_CONVERTED_PAWNS =
        "Zu viele umgewandelte Bauern";

}
