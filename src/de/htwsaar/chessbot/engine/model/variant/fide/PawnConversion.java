package de.htwsaar.chessbot.engine.model.variant.fide;

import de.htwsaar.chessbot.engine.model.*;

/**
* Bauernumwandlung.
*
* Erreicht ein weißer Bauer die 8., bzw. ein schzwarzer Bauer die 1. Reihe,
* so wird er in eine Dame, einen Turm, einen Springer oder einen Läufer 
* umgewandelt. Der Bauer wird vom Spielbrett genommen und die Figur an 
* dieselbe Stelle gestellt.
*
* @author Johannes Haupt
*/
public class PawnConversion extends Move {
   
    private Piece conversionTarget;

    public PawnConversion(final Piece pawn,
                          final Position target,
                          final Piece convertTo) 
    {
        super(pawn, target);
        this.conversionTarget = convertTo.clone();
        conversionTarget.setPosition(target);
        conversionTarget.setColor(pawn.isWhite());
        conversionTarget.setHasMoved(true);
    }

    public boolean isPossible(Board context) {
        if ( !super.isPossible(context) && !(getPiece() instanceof Pawn))
            return false;

        if (getPiece().isWhite())
            return getTarget().getRow() == context.getHeight();
        else
            return getTarget().getRow() == 1;
    }

    public Board execute(Board onBoard) {
        Board result = super.execute(onBoard);
        if (result != null) {
            result.removePieceAt(getTarget());
            result.addPiece(conversionTarget);
        }
        return result;
    }

    public String toSAN(Board context) {
        String san = super.toSAN(context);
        san += conversionTarget.toSAN();
        return san;
    }

    public String toUCI() {
        String uci = super.toUCI();
        uci += conversionTarget.toSAN();
        return uci;
    }

}
