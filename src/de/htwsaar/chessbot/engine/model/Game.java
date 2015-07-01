package de.htwsaar.chessbot.engine.model;

/**
* Stellt eine Schachpartie dar.
*
* @author Johannes Haupt
*/
public class Game {
    
    private final ChessVariant chessVariant;
    private History history;


    /**
    * Standardkonstruktor.
    */ 
    public Game(final ChessVariant chessVariant) {
        this(chessVariant, null);
    }

    public Game(final ChessVariant chessVariant,
                final String fenString) 
    {
        if (chessVariant == null)
            throw new NullPointerException(EXN_NULL_VARIANT);
        this.chessVariant = chessVariant;
        Board initial;
        if (fenString == null) {
            initial = chessVariant.getBoardBuilder().getStartingPosition();
        } else {
            initial = chessVariant.getBoardBuilder().fromFenString(fenString);
        }
        chessVariant.checkBoard(initial);
        this.history = new History(initial);
    }

    public Board getCurrentBoard() {
        return history.getLastBoard();
    }

    public Move getLastMove() {
        return history.getLastMove();
    }

    public void executeMove(String sanMove) {
        history.addMove(sanMove);
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
    
    // ------------------------------------------------------

    private static final String EXN_NULL_VARIANT =
        "Die gewählte Spielvariante ist null.";
}
