package de.htwsaar.chessbot.engine.io;

import de.htwsaar.chessbot.Engine;
import de.htwsaar.chessbot.engine.config.Config;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.search.SearchConfiguration;
import java.util.ArrayList;
import java.util.List;

/**
 * zerlegt Ausgaben und splittet diese zurecht. gibt die UCI-Kommandos das
 * praepariert an die Engine weiter.
 *
 * @author Dominik Becker
 *
 */
public class Parser {

    private static final String ILLEGALCMD
            = "Command is not supported: ";
    private static final String MOVE = "([a-h][1-8]){2}[bnrq]?";
    private static final String OPTION = "setoption name .+ value .+";
    private static final String OPTION_VALUE = "setoption name .+ value (?<value>.+)";
    private static final String OPTION_NAME = "setoption name (?<name>.+) value .+";
    private static final String OPTION_NAME_REPLACEMENT = "${name}";
    private static final String OPTION_VALUE_REPLACEMENT = "${value}";
    private static final String POSITION = "position (?<pos>.+) (moves (?<moves>.+))?";

    public static void uci() {
        setUCIParameter();
        sendCmd("uciok");
    }

    public static void isReady(final Engine engine) {
        if (engine.isReady())
            sendCmd("readyok");
    }

    /**
     * ruft in der Engine newGame() auf um ein neues Spiel zu starten.
     *
     * @param engine
     */
    public static void ucinewgame(Engine engine) {
        engine.newGame();
    }

    /**
     * Position Kommando. zerteilt die Ausgabe und gibt der Engine eine fertige
     * Zugliste und einen fen-String mit.
     *
     * @param line
     * @param engine
     */
    public static void position(String line, Engine engine) {
        String[] result = line.split("position ");
        String[] cmd = result[1].split(" ");
        String fenString;
        List<String> moves = new ArrayList<String>();
        if (cmd[0].equals("fen") && cmd.length > 6) {
            fenString = cmd[1] + " "
                    + cmd[2] + " "
                    + cmd[3] + " "
                    + cmd[4] + " "
                    + cmd[5] + " "
                    + cmd[6];
            cmd[1] = fenString;
            for (int i = 7; i < result.length; i++) {
                moves.add(result[i]);
            }
            engine.setBoard(fenString, moves);
        } else {
            for (int i = 2; i < cmd.length; i++) {
                moves.add(cmd[i]);
            }
            engine.resetBoard(moves);
        }
    }

    /**
     * GO-Kommando. zerteilt die Ausgabe und ueberprueft welche Schalter dem
     * Kommando mitgegeben wurde. gibt der Engine die entsprechenden
     * Modifikationen mit und startet die Suche.
     *
     * @param line
     * @param engine
     */
    public static void go(String line, Engine engine) {
        List<Move> moves = null;
        int depth = 0;
        boolean infinite = false;
        engine.getSearcher().resetConfiguration();
        SearchConfiguration cfg = engine.getSearcher().getConfiguration();
        String[] cmds = line.split(" ");
        long wtime = 0, btime = 0,
             winc = 0, binc = 0;
        int movestogo = 0;
        for (int i = 0; i < cmds.length; i++) {
            switch (cmds[i]) {
                case "searchmoves":
                    moves = getMoves(line, engine.getBoard().getMoveList());
                    cfg.setMoves(moves);
                    break;
                case "wtime":
                    wtime = Long.parseLong(cmds[i + 1]);
                    break;
                case "btime":
                    btime = Long.parseLong(cmds[i + 1]);
                    break;
                case "winc":
                    winc = Long.parseLong(cmds[i + 1]);
                    break;
                case "binc":
                    binc = Long.parseLong(cmds[i + 1]);
                    break; 
                case "movestogo":
                    movestogo = Integer.parseInt(cmds[i + 1]);
                    break;
                case "depth":
                    depth = Integer.parseInt(cmds[i + 1]);
                    cfg.setDepthLimit(depth);
                    break;
                case "movetime":
                    int movetime = Integer.parseInt(cmds[i + 1]);
                    cfg.setTimeLimit(movetime);
                    break;
                case "infinite":
                    cfg.setInfinite(true);
                    break;
                case "ponder":
                    cfg.setPonder(true);
                    break;
            }
        }
        
        engine.getGame().setClock(wtime, btime, winc, binc, movestogo);
        engine.search();

//        if (moves != null && depth != 0) {
//            engine.searchmoves(moves, depth);
//        } else if (moves != null) {
//            engine.searchmoves(moves, 5);
//        } else if (depth != 0) {
//            engine.search(depth);
//        } else if (infinite) {
//            engine.search(Integer.MAX_VALUE);
//        } else {
//            engine.search(6);
//        }
    }
    
    /**
     * filtert die Zuege aus der Ausgabe und gibt diese zurueck.
     *
     * @param line
     * @param moveList
     * @return moves
     */
    private static List<Move> getMoves(String line, Move[] moveList) {
        List<Move> moves = new ArrayList<Move>();
        String[] searchMv = line.split("searchmoves ");
        String[] elements = searchMv[1].split(" ");
        for (int i = 0; i < elements.length; i++) {
            if (!elements[i].matches(MOVE)) {
                break;
            }
            for (Move m : moveList) {
                if (elements[i].equals(m.toString())) {
                    moves.add(m);
                }
            }
        }
        return moves;

    }

    //Stop-Kommando
    public static void stop(Engine engine) {
        engine.stop();
    }

    //Ponderhit-Kommando
    public static void ponderhit(Engine engine) {
        engine.ponderhit();
    }

    /**
     * aendert die Optionen der Engine.
     *
     * @param line
     */
    public static void setoption(String line) {
        if(!line.matches(OPTION)) {
            return;
        }
        String name = line.replaceFirst(OPTION_NAME, OPTION_NAME_REPLACEMENT);
        String val = line.replaceFirst(OPTION_VALUE, OPTION_VALUE_REPLACEMENT);
        if(Config.getInstance().containsKey(name)){
            Config.getInstance().getOption(name).setValue(val);
        }
    }

    /**
     * stellt die Debug-Funktion ein oder aus
     *
     * @param line
     */
    public static void setDebug(String line) {
        String[] result = line.split(" ");
        if (result[1].equals("on")) {
            UCISender.getInstance().setShowDebug(true);
        } else if (result[1].equals("off")) {
            UCISender.getInstance().setShowDebug(false);
        }
    }

    public static void illegalCmd(String line) {
        sendCmd(ILLEGALCMD + line);
    }

    private static void sendCmd(String cmd) {
        UCISender.getInstance().sendToGUI(cmd);
    }

    private static void setUCIParameter() {
        sendCmd("id name Chessbot");
        sendCmd("id author Projektgruppe 2015 Schachengine");
        sendCmd(Config.getInstance().toString());

    }

    public static void test(String cmd, Engine engine) {
        String[] params = cmd.split(" ");
        String msg = "TEST_MSG";
        if (params.length > 4) {
            msg = "";
            for (int i = 4; i < params.length; i++) {
                msg += params[i] + " ";
            }
        }

        switch (params[1].toLowerCase()) {
            case "queuemsg":
                int count = Integer.parseInt(params[3]);
                switch (params[2].toLowerCase()) {
                    case "togui":
                        for (int i = 0; i < count; i++) {
                            UCISender.getInstance().sendToGUI(msg);
                        }
                        UCISender.getInstance().sendDebug("[TEST]Done.");
                        break;
                    case "debug":
                        for (int i = 0; i < count; i++) {
                            UCISender.getInstance().sendDebug(msg);
                        }
                        UCISender.getInstance().sendDebug("[TEST]Done.");
                        break;
                    case "error":
                        for (int i = 0; i < count; i++) {
                            UCISender.getInstance().sendError(msg);
                        }
                        UCISender.getInstance().sendDebug("[TEST]Done.");
                }
                break;
        }
    }

}
