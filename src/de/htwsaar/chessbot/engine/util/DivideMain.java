package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.util.Perft.*;
/**
* Perft-Hauptprogramm zum Ausführen in der Java VM.
*
* @author Johannes Haupt
*/
public class DivideMain {

    public static void main(final String[] args) {
        int argc = args.length;
        String fen;
        int depth;
        int numWorkers = DEF_WORKERS;
        switch (argc) {
            case 3:
                numWorkers = Integer.valueOf(args[2]);
            case 2:
                fen = args[0];
                depth = Integer.valueOf(args[1]);
                break;
            case 0:
                fen = "7R/8/8/8/8/8/6k1/4K3 b - - 0 1";
                depth = 5;
                numWorkers = 1;
                break;
                
            default:
                System.out.println(USAGE_STRING);
                return;
        }
        System.out.println(Board.B(fen));
        Result r = Perft.run(RunType.DIVIDE,fen,depth,numWorkers);
        System.out.println(r);
    }

    private static final String USAGE_STRING =
        "Usage: Divide <fen_string> <search_depth> [<num_worker_threads>]";
}
