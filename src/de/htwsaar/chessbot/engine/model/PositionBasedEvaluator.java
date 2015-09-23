package de.htwsaar.chessbot.engine.model;

/**
 * Erweiterung des MaterialEvaluators mit Positionsbewertungen.
 * 
 * @author David Holzapfel
 *
 */
public class PositionBasedEvaluator extends EvaluationFunction {

	private static final int[][] PAWN_VALUES = {
			{0, 0, 0, 0, 0, 0, 0, 0},
			{50, 50, 50, 50, 50, 50, 50, 50},
			{10, 10, 20, 30, 30, 20, 10, 10},
			{5, 5, 10, 25, 25, 10, 5, 5},
			{0, 0, 0, 20, 20, 0, 0, 0},
			{5, -5, -10, 0, 0, -10, -5, 5},
			{5, 10, 10, -20, -20, 10, 10, 5},
			{0, 0, 0, 0, 0, 0, 0, 0}
	};
	
	private static final int[][] KNIGHT_VALUES = {
			{-50, -40, -30, -30, -30, -30, -40, -50},
			{-40, -20, 0, 0, 0, 0, -20, -40},
			{-30, 0, 10, 15, 15, 10, 0, -30},
			{-30, 5, 15, 20, 20, 15, 5, -30},
			{-30, 0, 15, 20, 20, 15, 0, -30},
			{-30, 5, 10, 15, 15, 10, 5, -30},
			{-40, -20, 0, 5, 5, 0, -20, -40},
			{-50, -40, -30, -30, -30, -30, -40, -50}
			
	};
	
	private static final int[][] BISHOP_VALUES = {
			{-20,-10,-10,-10,-10,-10,-10,-20},
			{-10,  0,  0,  0,  0,  0,  0,-10},
			{-10,  0,  5, 10, 10,  5,  0,-10},
			{-10,  5,  5, 10, 10,  5,  5,-10},
			{-10,  0, 10, 10, 10, 10,  0,-10},
			{-10, 10, 10, 10, 10, 10, 10,-10},
			{-10,  5,  0,  0,  0,  0,  5,-10},
			{-20,-10,-10,-10,-10,-10,-10,-20}
	};
	
	private static final int[][] ROOK_VALUES = {
			{0,  0,  0,  0,  0,  0,  0,  0},
			{5, 10, 10, 10, 10, 10, 10,  5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{0,  0,  0,  5,  5,  0,  0,  0}
	};
	
	private static final int[][] QUEEN_VALUES = {
			{-20,-10,-10, -5, -5,-10,-10,-20},
			{-10,  0,  0,  0,  0,  0,  0,-10},
			{-10,  0,  5,  5,  5,  5,  0,-10},
			{-5,  0,  5,  5,  5,  5,  0, -5},
			{0,  0,  5,  5,  5,  5,  0, -5},
			{-10,  5,  5,  5,  5,  5,  0,-10},
			{-10,  0,  5,  0,  0,  0,  0,-10},
			{-20,-10,-10, -5, -5,-10,-10,-20}
	};
	
	private static final int[][] KING_VALUES_MIDGAME = {
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-20,-30,-30,-40,-40,-30,-30,-20},
			{-10,-20,-20,-20,-20,-20,-20,-10},
			{20, 20,  0,  0,  0,  0, 20, 20},
			{20, 30, 10,  0,  0, 10, 30, 20}
	};
	
	private static final int[][] KING_VALUES_ENDGAME = {
			{-50,-40,-30,-20,-20,-30,-40,-50},
			{-30,-20,-10,  0,  0,-10,-20,-30},
			{-30,-10, 20, 30, 30, 20,-10,-30},
			{-30,-10, 30, 40, 40, 30,-10,-30},
			{-30,-10, 30, 40, 40, 30,-10,-30},
			{-30,-10, 20, 30, 30, 20,-10,-30},
			{-30,-30,  0,  0,  0,  0,-30,-30},
			{-50,-30,-30,-30,-30,-30,-30,-50}
	};
	
	private static final double PIECE_VALUE_WEIGHT = 1d;
	private static final double PIECE_POSITION_WEIGHT = 1d;
	
	@Override
	public int evaluate(Board b) {
		double materialCount = 0;
		int sign;
		for (Piece piece : b.getPieces()) {
			sign = piece.isWhite() ? 1 : -1;
			materialCount += sign * 
					(PIECE_VALUE_WEIGHT * sPieceValues.get(piece.getClass()) +
					 PIECE_POSITION_WEIGHT * getPositionValue(b, piece));
		}
		return -(int)materialCount;
	}
	
	private int getPositionValue(Board b, Piece p) {
		int x = getX(p);
		int y = getY(p, p.isWhite());
		
		if(p instanceof Pawn) {
			return PAWN_VALUES[x][y];
		} else if(p instanceof Knight) {
			return KNIGHT_VALUES[x][y];
		} else if(p instanceof Bishop) {
			return BISHOP_VALUES[x][y];
		} else if(p instanceof Rook) {
			return ROOK_VALUES[x][y];
		} else if(p instanceof Queen) {
			return QUEEN_VALUES[x][y];
		} else {
			if(isEndGame(b)) {
				return KING_VALUES_ENDGAME[x][y];
			} else {
				return KING_VALUES_MIDGAME[x][y];
			}
		}
	}
	
	private int getX(Piece p) {
		return p.getPosition().file() - 1;
	}
	
	private int getY(Piece p, boolean whiteMoves) {
		if(whiteMoves) {
			return p.getPosition().rank() - 1;
		} else {
			return 8 - p.getPosition().rank();
		}
	}
	
	private boolean isEndGame(Board b) {
		//EndGame -> 1) Beide Spieler ohne Dame
		//      oder 2) Jeder Spieler mit Dame hoechstens 1 "kleine" Figur (Laeufer/Springer)
		boolean condition1 = false, condition2 = false;
		
		boolean whiteQueen = false, blackQueen = false;
		for(Piece p : b.getPieces()) {
			if(p instanceof Queen && p.isWhite()) {
				whiteQueen = true;
			} else if(p instanceof Queen && !p.isWhite()) {
				blackQueen = true;
			}
		}
		condition1 = !(whiteQueen || blackQueen);
		
		if(whiteQueen || blackQueen) {
			condition2 = true;
		}
		
		if(whiteQueen) {
			boolean foundMinorPiece = false;
			for(Piece p : b.getPieces()) {
				if(p instanceof Rook && p.isWhite()) {
					condition2 = false;
					break;
				} else if((p instanceof Bishop || p instanceof Knight) && p.isWhite()) {
					if(foundMinorPiece) {
						condition2 = false;
						break;
					} else {
						foundMinorPiece = true;
					}
				}
			}
		}
		if(blackQueen && condition2) {
			boolean foundMinorPiece = false;
			for(Piece p : b.getPieces()) {
				if(p instanceof Rook && !p.isWhite()) {
					condition2 = false;
					break;
				} else if((p instanceof Bishop || p instanceof Knight) && !p.isWhite()) {
					if(foundMinorPiece) {
						condition2 = false;
						break;
					} else {
						foundMinorPiece = true;
					}
				}
			}
		}
		
		return condition1 || condition2;
	}

}
