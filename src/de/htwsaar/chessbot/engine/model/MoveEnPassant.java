package de.htwsaar.chessbot.engine.model;

public class MoveEnPassant extends Move {

	public MoveEnPassant(Piece piece, Position targetPosition) {
		super(piece, targetPosition);
		
		if(!(piece instanceof Pawn)) {
			//TODO: Exception: Figur muss Bauer sein
		}
		int rowDifference = Math.abs(piece.getPosition().getRow() - targetPosition.getRow());
		if(!(
			piece.getPosition().getRow() == 5 && targetPosition.getRow() == 6 && piece.isWhite() || 
			piece.getPosition().getRow() == 4 && targetPosition.getRow() == 3 && !piece.isWhite()) ||
			rowDifference != 1){
			//TODO: Exception: ungültiger en passant move
		}
		
		
	}

	public MoveEnPassant(Board context, Position fromPosition, Position toPosition) {
		super(context, fromPosition, toPosition);
	}

	@Override
	public Board execute(Board onBoard) {
		if ( !isPossible(onBoard) )
            return null;

        Board target = onBoard.clone();
        
        int attackDirection = this.getTarget().getColumn() - 
        		this.getPiece().getPosition().getColumn();
        Piece attackedPiece = onBoard.pieceAt(new Position(
        		this.getPiece().getPosition().getColumn() + attackDirection, 
        		this.getPiece().getPosition().getRow()));
        if(attackedPiece == null) {
        	//TODO: Exception: Figur nicht da, da stimmt was nicht
        }
        
        onBoard.removePiece(attackedPiece);

        target.removePiece(this.getPiece());
        if (!target.isFree(this.getTarget()))
            target.removePiece(target.pieceAt(this.getTarget()));

        Piece movedPiece = this.getPiece().clone();
        movedPiece.setPosition(this.getTarget());
        target.addPiece(movedPiece);
        return target;
	}
	
}
