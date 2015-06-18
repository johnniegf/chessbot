package de.htwsaar.chessbot.engine.control;

import de.htwsaar.chessbot.util.*;
import de.htwsaar.chessbot.engine.model.*;
import de.htwsaar.chessbot.engine.model.variant.fide.*;



/**
* Beschreibung.
*
* @author
*/
public class BoardCLI {

    public static void main(String[] args) {
        BoardCLI cli = new BoardCLI();

        String move = null;
        do {
            try {
                cli.printBoard();
                move = cli.queryMove();
                cli.executeMove(move);
            } catch (Exception e) {
                output.println(e);
                e.printStackTrace();
            }
        } while (move != null);
    }

    private ChessVariant variant;
    private Game game;
    private static Input input = Input.getInstance();
    private static Output output = new ConsoleOutput();

    /**
    * Standardkonstruktor.
    */ 
    public BoardCLI() {
        this.variant = new FideChess();
        this.game = new Game(variant);
    }

    private void printBoard() {
        Board.Formatter f = new ConsoleFormatter();
        Board b = game.getCurrentBoard();
        output.println();
        output.print( f.format(b) );
        output.println();

    }

    private String queryMove() {
        output.print("Move: ");
        return input.readLine().toString();
    }

    private void executeMove(String sanMove) {
        game.executeMove(sanMove);
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

    private static final class ConsoleFormatter implements Board.Formatter {
        
        private static final String lineSeparator = System.getProperty("line.separator");

        public String format(final Board board) {
            StringBuilder sb = new StringBuilder();
            int w = board.getWidth();
            int h = board.getHeight();
            
            String rowLine = "  +";
            for (int x = 1; x <= w; x++)
                rowLine += "-+";
            rowLine += lineSeparator;
            
            sb.append(rowLine);
            for (int y = h; y >= 1; y--) {
                sb.append( String.format("%2d|", y) );
                for (int x = 1; x <= w; x++) {
                    Position p = new Position(x,y);
                    Piece pc = board.pieceAt(p);
                    sb.append( (pc == null ? " " : pc.toFEN()) );
                    sb.append("|");
                }
                sb.append(lineSeparator);
                sb.append(rowLine);
            }
            sb.append("   ");
            for (char c = 'a'; c - 'a' < w; c++) {
                sb.append(c+" ");
            }
            sb.append(lineSeparator);
            return sb.toString();
        }

    }
}
