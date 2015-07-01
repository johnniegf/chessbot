package de.htwsaar.chessbot.engine.model;

import java.util.Collection;

/**
* Schachspielvariante.
*
*
*
* @author Johannes Haupt
*/
public interface ChessVariant {

    /**
    * Gib ein neues leeres Schachbrett dieser Variante zurück.
    *
    * @return ein neues leeres Schachbrett
    */
    Board getBoard();

    /**
    * Gib einen Stellungserbauer für diese Variante zurück.
    *
    * @return einen Stellungserbauer
    */
    BoardBuilder getBoardBuilder();
    
    /**
    * Gib die FigurenFabrik dieser Variante zurück.
    *
    * @return eine Figurenfabrik
    */
    Pieces getPieceFactory();

    void checkBoard(final Board board);

    /**
    * Gib die Figurprototypen dieser Variante zurück.
    *
    * @return die Figurenprototypen dieser Variante
    */
    Collection<Piece> getPiecePrototypes();

}
