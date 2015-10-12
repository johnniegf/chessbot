package de.htwsaar.chessbot.perft;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.core.Board.*;
import static de.htwsaar.chessbot.util.Exceptions.*;
import de.htwsaar.chessbot.util.Table;

import java.util.*;
/**
* Testwerkzeug für Performance- und Korrektheit des Zuggenerators.
*
* Divide zählt alle Züge, die der Zuggenerator für eine Ausgangstellung und 
* eine Suchtiefe erzeugt. Die Ergebnisse der Generierung lassen sich 
* @author Johannes Haupt
*/
public class DivideWorker extends Perft.Worker {

    private static int ID = 1;

    private int mDepth;
    private Board mInitial;
    private Collection<Move> mMoves;
    private Table<String,Integer,Long> mDivided;
    private boolean done = false;
    private Result mResult;


    public DivideWorker(final Board initial, 
                        final Collection<Move> movesToSearch, 
                        final int depth) 
    {
        super("DivideWorker#" + ID);
        mDepth = depth;
        mMoves = movesToSearch;
        mInitial = initial;
        mDivided = makeTable(movesToSearch, depth);
        ID += 1;
    }

    private Table<String,Integer,Long> makeTable(final Collection<Move> m,
                                                 final int d)
    {
        Collection<String> rows = new ArrayList<String>();
        Collection<Integer> cols = new ArrayList<Integer>();
        for (Move mv : m) {
            rows.add(mv.toString());
        }
        for ( int i = 1; i <= d; i++) {
            cols.add(i);
        }
        return new Table<String,Integer,Long>(0L, rows, cols);
    }

    public boolean isDone() {
        return done;
    }

    public void run() {
        if (!done) {
            Board b;
            for (Move m : mMoves) {
                b = m.execute(mInitial);
                calculate(b, mDepth-1,m.toString());
                mDivided.put(m.toString(),
                             1,
                             1L );
            }
            mResult = new Result(mDivided);
            done = true;
        }
    }

    private long calculate(final Board board, 
                           final int depth, 
                           final String move) 
    {
        if (board == null) 
            throw new NullPointerException("board");
        long result = 0;
        if ( depth > 0 ) {
            Board[] results = board.getResultingPositions();
            int index = mDepth-depth+1;
            mDivided.put(move, 
                         index, 
                         mDivided.get(move,index) + results.length);
            for ( Board b : results ) {
                result += calculate(b, depth-1, move);
            }
        } else {
            return 1;
        }
        return result;
    }

    public Perft.Result result() {
        return mResult;
    }

    public static final class Result implements Perft.Result {
        public int mSeekDepth;
        public long mTime = -1L;
        public Table<String,Integer,Long> mDivide;
        private Map<Integer,Long> sums;


        private Result() {
            this(null);
        }

        public Result(final Table<String,Integer,Long> dividedResults) 
        {
            mSeekDepth = dividedResults.colCount();
            mDivide = dividedResults;
            sums = new HashMap<Integer,Long>();
            for (int i = 1; i <= mSeekDepth; i++) {
                sums.put(i, 0L);
            }
            for (String m : mDivide.getRows()) {
                for (int i = 1; i <= mSeekDepth; i++) {
                    sums.put(i, sums.get(i)+mDivide.get(m,i));
                }
            }
        }

        public Perft.Result join(final Perft.Result other) {
                Result r = (Result) other;
                Table<String,Integer,Long> results = new Table<String,Integer,Long>(0L);
                results.putAll(mDivide);
                results.putAll(r.mDivide);
                return new Result(results);
        }

        public void setTime(final long time) {
            mTime = time;
        }

        public String toString() {
            if (mDivide == null)
                return "faulty";

            StringBuilder sb = new StringBuilder();
            sb.append(line());
            sb.append("| Depth |");
            for (int i = 1; i <= mSeekDepth; i++) {
                sb.append( String.format(" %"+getColWidth(i)+"s |", i) );
            }
            sb.append("\n");
            
            sb.append(line());
            for (String move : mDivide.getRows()) {
                sb.append(compileRow(move));
            }
            sb.append(line());
            sb.append("| Total |");
            for (int i = 1; i <= mSeekDepth; i++) {
                sb.append( String.format(" %"+getColWidth(i)+"s |", sums.get(i)) );
            }
            sb.append("\n");
            sb.append(line());
            sb.append(mTime);
            return sb.toString();
        }   

        private String compileRow(String move) {
            String line = String.format("| %-5s |",move);
            for (int i = 1; i <= mSeekDepth; i++) {
                int colWidth = getColWidth(i);
                line += String.format(" %"+colWidth+"d |", 
                                      mDivide.get(move,i));
            }
            line += String.format("%n");
            return line;
        }

        private String line() {
            String line = "+";
            for (int i = 0; i < getRowWidth() - 2; i++)
                line += "-";
            line += "+\n";
            return line;
        }

        private String rowLine() {
            String row = "+-------+";
            for (int i = 1; i <= mSeekDepth; i++) {
                for (int s = 1; s < getColWidth(i); s++)
                    row += "-";
                row += "+";
            }
            return row;
        }

        private int getRowWidth() {
            int width = 9;
            for (int i = 1; i <= mSeekDepth; i++) {
                width += getColWidth(i) + 3;
            }
            return width;
        }

        private int getColWidth(int colNum) {
            int c = 1, max = 0;
            long orig = sums.get(colNum);
            while (orig > 0) {
                c++;
                orig /= 10;
            }
            return c;
        }
    }

    private static String formatNum(final long number) {
        long n = number;
        List<Long> parts = new ArrayList<Long>();
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            parts.add(n % 1000L);
            n /= 1000L;
        }
        for (int i = parts.size() - 1; i >= 0; i--) {
            n = (long) parts.get(i);
            sb.append(n);
            if (i > 0)
                sb.append(".");
        }
        return sb.toString();
    }

}
