package de.htwsaar.chessbot.engine.model;
/**
*Klasse zur Bauernumwandlung. 
*
*@author Dominik Becker
*/
public class MovePromotion extends Move {

	private Piece promoted;

	public MovePromotion(final Pawn pawn, final Position endposition, Piece promoted) {
		if(!(endposition.getRow() == 8 || endposition.getRow() == 1)) {
			//TODO Fehlermeldung: nicht die letzte Reihe!

		}

		if(promoted instanceof King || promoted instanceof Pawn) {
			//TODO Fehlermeldung: keine Umwandlung zum König oder Bauer möglich!
		}

		setPiece(pawn);
		setTarget(endposition);
		this.promoted = promoted;

	}

    @Override
	public Board execute(Board onBoard) {

        Board target = super.execute(onBoard);
        target.removePiece(target.pieceAt(getTarget()));

        Piece movedPiece = this.promoted;
        movedPiece.setPosition(getTarget());
        target.addPiece(movedPiece);
        return target;
    }
    
    public String toSAN() {
    	return super.toSAN()+promoted.toSAN();
    }

}