package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.model.*;
import de.htwsaar.chessbot.engine.model.variant.fide.*;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Perft {

    public static void main(final String[] args) {
        int argc = args.length;
        if (argc != 2)
            return;
        String fen = args[0];
        int depth = Integer.valueOf(args[1]);
        if (ChessVariant.getActive() == null)
            ChessVariant.setActive(new FideChess());
        Perft pf = new Perft();
        long result = 0;
        long time = System.currentTimeMillis();
        result = pf.calculate(fen,depth);
        time -= System.currentTimeMillis();
        System.out.println("Result: " + result + ", time : " + time + "ms");
    }

    /**
    * Standardkonstruktor.
    */ 
    public long calculate(final String fenString, final int depth) {
        if (fenString == null)
            throw new NullPointerException("fenString");
        final Board initial
            = ChessVariant.getActive().getBoardBuilder().fromFenString(fenString);
        return calculateR(initial, depth);
    }

    private long calculateR(final Board board, final int depth) {
        if (board == null) 
            throw new NullPointerException("board");
        long result = 0;
        if ( depth > 0 ) {
            Board b;
            for ( Move m : board.getMoveList() ) {
                System.out.println(m);
                b = m.execute(board);
                result += 1 + calculateR(b, depth-1);
            }
        }
        return result;
    }

    /**
    * Gib den Hashwert dieses Objekts aus.
    *
    * @return Hashwert dieses Objekts.
    */
    public int hashCode() {
        int hash = 0;
        // Berechnungen

        return hash;
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
}
