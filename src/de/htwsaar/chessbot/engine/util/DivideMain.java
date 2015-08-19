package de.htwsaar.chessbot.engine.util;

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
                
            default:
                System.out.println(USAGE_STRING);
                return;
        }
        Result r = Perft.run(RunType.DIVIDE,fen,depth,numWorkers);
        System.out.println(r);
    }

    private static final String USAGE_STRING =
        "Usage: Divide <fen_string> <search_depth> [<num_worker_threads>]";
}
