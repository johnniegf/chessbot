package de.htwsaar.chessbot.core;

import de.htwsaar.chessbot.core.BitBoardUtils;
import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.moves.Move;
import static de.htwsaar.chessbot.util.Exceptions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
* Schachpartie.
*
*
*
* @author Johannes Haupt
*/
public class Game {
    
    private Clock   mClock;
    private History mHistory;

    /**
    * Standardkonstruktor.
    */ 
    public Game() {
        this("startpos");
    }

    public Game(final String fenInitial) {
        this(fenInitial, new String[0]);
    }

    public Game(final String fenInitial, final String[] moves) {
        checkNull(fenInitial);
        checkNull(moves);

        mHistory    = new History( 
            fenInitial.equals("startpos") ? Board.B() : Board.B(fenInitial) 
        ); 
        for (String move : moves) {
            doMove(move);
        }
        mClock = new Clock();
    }

    public Board getCurrentBoard() {
        return mHistory.getMostRecent();
    }
    
    public Board getPosition(int plyIndex) {
        return mHistory.get(plyIndex);
    }
    
    public int plyCount() {
        return mHistory.size();
    }

    public boolean doMove(final String moveString) {
        checkNull(moveString, "moveString");
        Board b = getCurrentBoard();
        for (Move m : b.getMoveList()) {
            if (moveString.equals(m.toString())) {
                mHistory.append(m);            
                return true;
            }
        }
        return false;
    }

    public Clock getClock() {
        return mClock;
    }
    
    public void setClock(long wtime, long btime, long winc, long binc, int movestogo) {
        mClock = new Clock(wtime, btime, winc, binc, movestogo);
    }

    public boolean isFinished() {
        return result() != Result.UNFINISHED;
    }

    public boolean isMate() {
        return BitBoardUtils.isMate(getCurrentBoard());
    }

    public boolean isStalemate() {
        return BitBoardUtils.isStalemate(getCurrentBoard());
    }

    public boolean isDraw() {
        return BitBoardUtils.isDraw(getCurrentBoard());
    }

    public Result result() {
        if ( isMate() )
            return Result.MATE;
        else if ( isStalemate() )
            return Result.STALEMATE;
        else if ( isDraw() )
            return Result.DRAW;
        else
            return Result.UNFINISHED;
    }

    public static enum Result {
        UNFINISHED, // <-- Spiel läuft noch
        MATE,       // <-- Schachmatt
        STALEMATE,  // <-- Patt
        DRAW        // <-- Remis
    }

}



class History {

    private List<Board> positions;

    public History() {
        this( Board.B() );
    }

    public History(final Board initial) {
        this( initial, null );
    }
    
    public History(final Board initial, final List<Move> moves) {
        checkNull(initial, "initial");
        positions = new LinkedList<>();
        positions.add(initial);
        Board current = initial;
        if ( moves != null && !moves.isEmpty() ) {
            for (Move m : moves) {
                current = m.execute(current);
                positions.add(0, current);
            }
        }
    }

    public boolean append(final Move move) {
        checkNull(move, "move");
        Board result = move.tryExecute(getMostRecent());
        if (Move.isValidResult(result)) {
            positions.add(0, result);
            return true;
        } else 
            return false;
    }

    public Board getMostRecent() {
        return positions.get(0);
    }

    public Board get(int moveNumber) {
        checkInBounds(moveNumber, 0, positions.size()-1);
        return positions.get(positions.size()-1-moveNumber);
    }
    
    public int size() {
        return positions.size();
    }

}
