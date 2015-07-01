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
        cli.run(args);
    }
    
    private static final String quitRegex = 
        "([Qq][Uu][Ii][Tt])|([Ee][Xx][Ii][Tt])";
    private static final String fenRegex =
        "[BbKkNnPpQqRr1-8]+(/[BbKkNnPpQqRr1-8]+){7} " +
        "[bw] (-|[KQkq]{1,4}) (-|[a-h][1-8]) \\d{1,2} \\d{1,2}";
    private static final String moveRegex = 
        "[QNBRK]?[a-h][1-8][X]?[a-h][1-8][QNBR]?";
    private static final String showRegex = 
        "[Ss][Hh][Oo][Ww] [a-h][1-8]";
    private static final String NEWLINE = 
        System.getProperty("line.separator");

    private ChessVariant VARIANT;
    private Game game;
    private boolean quitting;
    private static Input input = Input.getInstance();
    private static Output output = new ConsoleOutput();

    /**
    * Standardkonstruktor.
    */ 
    public BoardCLI() {
        this.VARIANT = new FideChess();
        this.game = new Game(VARIANT);
        this.quitting = false;
    }

    public void run(String[] args) {
        do {
            try {
               mainMenu(); 
            } catch (Exception e) {
                output.println(e);
                e.printStackTrace();
            }
        } while ( !quitting ); 
    }

    private void mainMenu() {
        printOptions();
        String choice = queryCommand("Auswahl");
        executeSub(choice);
    }

    private void printOptions() {
        StringBuilder options = new StringBuilder();
        options.append("game").append(NEWLINE);
        options.append("quit").append(NEWLINE);
        output.println(options);
    }

    private void executeSub(String choice) {
        switch(choice.trim()) {
            case "game":
                gameMenu();
                break;
            case "quit":
                output.println("Beende");
                this.quitting = true;
                break;
            default:
                output.println("Unbekanntes Kommando! " + choice);
        }
    }

    private void gameMenu() {
        String command = null;
        game = new Game(VARIANT);
        do {
            printBoard();
            try {
                command = queryCommand("Zug oder Stellung").trim();
                if (command.matches(fenRegex)) {
                    game = new Game(VARIANT, command);
                } else if (command.matches(moveRegex)) {
                    game.executeMove(command);
                } else if (command.matches(quitRegex)) {
                    output.println("Beende");
                    break;
                } else if (command.matches(showRegex)) {
                    String[] args = command.split(" ");
                    Position pos = new Position(args[1]);
                    Piece p = game.getCurrentBoard().pieceAt(pos);
                    if (p == null)
                        output.println("Das Feld " + pos.toSAN() + " ist nicht besetzt!");
                    else
                        printPiece(p);
                } else {
                    output.println("Unbekanntes Kommando! " + command);
                }
            } catch (Exception e) {
                output.println(e);
                e.printStackTrace();
            }
        } while ( command != null );
         game = null;
    }

    private static void printPiece(Piece p) {
        output.println("Figur: " + p.getName());
        output.println("Farbe: " + (p.isWhite() ? "weiß" : "schwarz"));
        output.println("Position: " + p.getPosition().toSAN());
        output.println("Wurde bewegt? " + (p.hasMoved() ? "ja" : "nein"));
    }

    private void printBoard() {
        Board.Formatter f = new ConsoleFormatter();
        Board b = game.getCurrentBoard();
        output.println();
        output.print( f.format(b) );
        output.println();

    }

    private String queryCommand(final String prompt) {
        output.print(prompt + " $> ");
        return input.readLine().toString();
    }

    private void executeMove(String sanMove) {
        game.executeMove(sanMove);
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
            sb.append(board.isWhiteMoving() ? "Weiß" : "Schwarz")
              .append(" am Zug, en passant : ")
              .append(board.enPassant() == null ? "keine" : board.enPassant().toSAN());
            sb.append(lineSeparator);
            return sb.toString();
        }

    }
}
