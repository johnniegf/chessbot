package de.htwsaar.chessbot.engine.util;

import static de.htwsaar.chessbot.engine.model.Board.*;
import de.htwsaar.chessbot.engine.model.*;
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
        final Board initial = B(fenString);
        return calculateR(initial, depth);
    }

    private long calculateR(final Board board, final int depth) {
        if (board == null) 
            throw new NullPointerException("board");
        long result = 0;
        if ( depth > 0 ) {
            Board b;
            System.out.println(board);
            for ( Move m : board.getMoveList() ) {
                //System.out.println(depth + " " + m.getClass().getName() + "(" + board.getPieceAt(m.getStart()).fenShort() +  m + ")");
                if ( !m.isPossible(board) )
                    throw new MoveException("Zug " + m + " ist nicht möglich in " + board);
                b = m.execute(board);
                result += calculateR(b, depth-1);
            }
        } else return 1;
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
