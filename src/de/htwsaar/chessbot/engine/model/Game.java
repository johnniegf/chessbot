package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.util.Exceptions.*;
import java.util.List;
/**
* Schachpartie.
*
*
*
* @author Johannes Haupt
*/
public class Game {
    
    private Clock   mWhiteClock;
    private Clock   mBlackClock;
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
        checkNull(fenInitial, "fenInitial");
        checkNull(moves, "moves");

        mHistory    = new History( 
            fenInitial.equals("startpos") ? Board.B() : Board.B(fenInitial) 
        ); 
        for (String move : moves) {
            doMove(move);
        }

        mWhiteClock = new Clock();
        mBlackClock = new Clock();
    }

    public Board getCurrentBoard() {
        return mHistory.getMostRecent();
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

    public Clock getClock(boolean ofWhitePlayer) {
        return (ofWhitePlayer ? mWhiteClock : mBlackClock);
    }

    public boolean isFinished() {
        return result() != Result.UNFINISHED;
    }

    public boolean isMate() {
        Board current = getCurrentBoard();
        return current.getMoveList().isEmpty() && isKingInCheck(current);
    }

    public boolean isStalemate() {
        Board current = getCurrentBoard();
        return current.getMoveList().isEmpty() && !isKingInCheck(current);
    }

    private boolean isKingInCheck(final Board context) {
        Board b = context.clone();
        b.togglePlayer();
        return b.isValid();
    }

    public boolean isDraw() {
        return getCurrentBoard().getHalfMoves() >= 50;
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

class Clock {
    private long increment;
    private long time;

    public void setTime(long msec) {

    }

    public void setIncrement(long msec) {

    }

    public long getTime() {
        return time;
    }

    public long getIncrement() {
        return increment;
    }
}

class History {

    private List<Move> moveList;
    private Board initialPosition;
    private Board currentPosition;

    public History() {
        this( Board.B() );
    }

    public History(final Board initial) {
        this( initial, null );
    }
    
    public History(final Board initial, final List<Move> moves) {
        checkNull(initial, "initial");
        this.initialPosition = initial;
        this.currentPosition = this.initialPosition;

        this.moveList = new ArrayList<Move>();

        if ( moves != null && !moves.isEmpty() ) {
            for (Move m : moves) {
                this.append(m);
            }
        }
    }

    public boolean append(final Move move) {
        checkNull(move, "move");
        if (move.isPossible(getMostRecent())) {
            Board result = move.execute(getMostRecent());
            setMostRecent(result);
            moveList.add(move);
            return true;

        } else 
            return false;
    }

    private void setMostRecent(final Board current) {
        checkNull(current);
        this.currentPosition = current;
    }

    public Board getMostRecent() {
        return currentPosition;
    }

    public Board get(int moveNumber) {
        checkInBounds(moveNumber, "moveNumber", 0, moveList.size());
        if (moveNumber == moveList.size())
            return getMostRecent();
        else {
            Board b = initialPosition;
            for (int i = 0; i < moveNumber; i++) {
                b = moveList.get(i).execute(b);
            }
            return b;
        }       
    }

}
