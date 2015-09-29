package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.engine.model.piece.Pieces;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import static de.htwsaar.chessbot.engine.model.Position.*;
import static de.htwsaar.chessbot.util.Exceptions.*;

import java.util.Collection;
/**
* Stellungserbauer.
*
* Hilfsklasse, die gebraucht wird, um einen FEN-String in ein
* Stellungsobjekt zu übersetzen. Der <code>BoardBuilder</code>
* prüft selbständig, ob der übergebene FEN-String in einem gültigen
* Format vorliegt.
*
* @author Johannes Haupt
*/
public class BoardBuilder {

    public Board getBoard() {
        return fromFenString(DEFAULT_FEN);
    }

    public Board fromFenString(final String fenString) {
        if (!fenString.matches(REGEX_FEN_STRING))
            throw new FenStringParseException(
                msg(EXN_BAD_FORMAT, fenString)
            );

        Board    result = new Board();
        String[] fields = fenString.split(" ");
        String[] rows   = fields[0].split("/");
        
        if (rows.length != 8) {
            throw new FenStringParseException(
                msg(EXN_ILLEGAL_ROW_COUNT, rows.length)
            );
        }
        for (int y = 0; y < 8; y++) {
            int rowNumber = 8 - y;
            int width = processRow(rowNumber, rows[y], result);
            if (width != 8) {
                throw new FenStringParseException(
                    msg(EXN_ILLEGAL_COL_COUNT, width)
                );
            }
        }
        
        setPlayer(fields[1], result);
        setCastlings(fields[2], result);
        setEnPassant(fields[3], result);
        setHalfMoves(fields[4], result);
        setFullMoves(fields[5], result);
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
                Piece p = Pieces.PC(current, currentPosition);
                if (p == null)
                    throw new FenStringParseException(
                        msg(EXN_UNKOWN_PIECE, current)
                    );
                
                context.putPiece(p);
                width += 1;
                currentPosition = currentPosition.transpose(1, 0);

            } else if (Character.isDigit(current)) {
                int empty = Character.getNumericValue(current);
                width += empty;
                currentPosition = currentPosition.transpose(empty, 0);
            } else {
                throw new FenStringParseException(
                    msg(EXN_ILLEGAL_CHARACTER, current)
                );
            }
        }
        return width;
        
    }

    private static boolean setPlayer(final String playerString, final Board context) {
        String colorCode = playerString.trim();
        switch(colorCode) {
            case "w":
                context.setWhiteAtMove(true);
                break;
            case "b":
                context.setWhiteAtMove(false);
                break;
            default:
                throw new FenStringParseException( 
                    msg(EXN_INVALID_COLOR, colorCode) 
                );
        }
        return true;
    }

    private static void setEnPassant(final String enPassant, Board context) {
        if (enPassant.trim().equals("-")) {
            context.setEnPassant(Position.INVALID);
        } else {
            context.setEnPassant(P(enPassant.trim()));
        }
    }

    private static void setHalfMoves(final String halfMoves, Board context) {
        int half = Integer.valueOf(halfMoves);
        context.setHalfMoves(half);

    }

    private static void setFullMoves(final String fullMoves, Board context) {
        int full = Integer.valueOf(fullMoves);
        context.setFullMoves(full);
    }
    
    private static void setCastlings(final String castlings, Board context) {
        int castlingBits = 0;
        for (int i = 0; i < castlings.length(); i++) {
            switch (castlings.charAt(i)) {
                case 'K':
                    castlingBits |= Board.CASTLING_W_KING;
                    break;
                case 'Q':
                    castlingBits |= Board.CASTLING_W_QUEEN;
                    break;
                case 'k':
                    castlingBits |= Board.CASTLING_B_KING;
                    break;
                case 'q':
                    castlingBits |= Board.CASTLING_B_QUEEN;
                    break;
                default:
                    throw new FenStringParseException(EXN_ILLEGAL_CHARACTER);
            }
        }
        
        context.setCastlings((byte) castlingBits);
    }

    private static final String REGEX_FEN_STRING = 
        "[BbKkNnPpQqRr1-8]+(/[BbKkNnPpQqRr1-8]+){7} " +
        "[bw] (-|[KQkq]{1,4}) (-|[a-h][1-8]) \\d{1,2} \\d{1,2}";

    private static final String DEFAULT_FEN = 
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final String EXN_ILLEGAL_COL_COUNT =
        "Ungültige Spaltenanzahl! Gefunden %d, erwarte 8.";
    private static final String EXN_ILLEGAL_ROW_COUNT =
        "Ungültige Zeilenanzahl! Gefunden %d, erwarte 8.";
    private static final String EXN_UNKOWN_PIECE =
        "Unbekanntes Figurenkürzel '%s'";
    private static final String EXN_ILLEGAL_CHARACTER =
        "Unbekanntes Zeichen '%s'";
    private static final String EXN_BAD_FORMAT =
        "Unzuässiges Format für FEN-String.";
    private static final String EXN_TOO_MANY_PIECES =
        "Zu viele Figuren der Art %s";
    private static final String EXN_TOO_MANY_CONVERTED_PAWNS =
        "Zu viele umgewandelte Bauern: %d";
    private static final String EXN_INVALID_COLOR =
        "Unbekannte Spielfarbe '%s'";

}

