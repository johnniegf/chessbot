package de.htwsaar.chessbot.engine.model;

import java.util.*;

/**
* Beschreibung.
*
* @author
*/
public class History {

    private final Board initialBoard;
    private List<HistEntry> moves;

    /**
    * Standardkonstruktor.
    */ 
    public History(final Board initial) {
        if (initial == null)
            throw new NullPointerException(EXN_INITIAL_POS_NULL);

        this.initialBoard = initial;
        this.moves = new ArrayList<HistEntry>();
    }

    public boolean empty() {
        return moves.isEmpty();
    }

    public boolean addMove(final String sanMove) {
        Board b = getLastBoard();
        Move move = b.getMove(sanMove);
        if (move == null)
            throw new InvalidMoveException(EXN_IMPOSSIBLE_MOVE + sanMove);
        HistEntry e = new HistEntry(move, b);
        return moves.add(e);
    }

    public Move getMove(final int moveNumber) {
        HistEntry e = getEntry(moveNumber);
        return e.move;
    }

    public Move getLastMove() {
        if ( empty() )
            return null;

        HistEntry e = getLastEntry();
        return e.move;
    }

    public Board getLastBoard() {
        if ( empty() )
            return initialBoard;

        HistEntry e = getLastEntry();
        return e.board;
    }

    public Board getInitialBoard() {
        return initialBoard;
    }

    public Board getBoard(int moveNumber) {
        checkMoveNumberInBounds(moveNumber);
        
        HistEntry e = getEntry(moveNumber);
        return e.board;
    }

    private HistEntry getEntry(int moveNumber) {
        checkMoveNumberInBounds(moveNumber);

        return moves.get(moveNumber-1);
    }

    private HistEntry getLastEntry() {
        return getEntry(size());
    }

    public int size() {
        return moves.size();
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

    private void checkMoveNumberInBounds(int moveNumber) {
        if (moveNumber < 1 || moveNumber > size())
            throw new IndexOutOfBoundsException(moveNumber + " < " + size());
    }

    private static final String EXN_INITIAL_POS_NULL =
        "Die Ausgangsstellung darf nicht null sein.";
    private static final String EXN_IMPOSSIBLE_MOVE =
        "Der übergebene Zug ist in dieser Stellung unmöglich.";
}

class HistEntry {

    public final Board board;
    public final Move  move;

    public HistEntry(final Move move, final Board boardBeforeMove) {
        if (boardBeforeMove == null)
            throw new NullPointerException(EXN_BOARD_NULL);
        if (move == null)
            throw new NullPointerException(EXN_MOVE_NULL);

        if (!move.isPossible(boardBeforeMove))
            throw new InvalidMoveException(EXN_IMPOSSIBLE_MOVE);

        this.move  = move;
        this.board = move.execute(boardBeforeMove);
    }

    private static final String EXN_MOVE_NULL =
        "Der Zug ist null.";
    private static final String EXN_BOARD_NULL =
        "Die übergebene Stellung ist null.";
    private static final String EXN_IMPOSSIBLE_MOVE =
        "Der übergebene Zug ist in dieser Stellung unmöglich.";
}
