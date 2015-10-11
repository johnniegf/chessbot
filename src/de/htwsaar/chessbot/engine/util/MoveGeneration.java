/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.model.Board;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.COLORS;
import static de.htwsaar.chessbot.engine.model.BitBoardUtils.Color.toBool;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.piece.AbstractPiece;
import de.htwsaar.chessbot.engine.model.piece.Bishop;
import de.htwsaar.chessbot.engine.model.piece.King;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Pieces;
import de.htwsaar.chessbot.engine.model.piece.Queen;
import de.htwsaar.chessbot.engine.model.piece.Rook;
import java.util.Collection;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class MoveGeneration {
    
    private static final String LINE = "--------------------------------------";
    private static final int[] SLIDING_PIECES = new int[] { Bishop.ID, Rook.ID, Queen.ID };

    public static void main(String[] args) {
        
        //Board emptyBoard =  Board.B("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
        Board emptyBoard = Board.B("r3k2r/p6p/8/B7/1pp1p3/3b4/P6P/2KR3R b kq - 0 1");
        Piece p;
        int pieceType = King.ID;
        for (int c : COLORS) {
            for (int posIndex = 0; posIndex < 64; posIndex++) {
                emptyBoard.setWhiteAtMove(toBool(c));
                Position pos = Position.P(posIndex);
                System.out.println("MoveGeneration.main: " + pieceType + "," + pos);
                p = Pieces.PC(pieceType, toBool(c), pos);
                System.out.println(String.format("attackBits = %016x", p.getAttackBits(emptyBoard)));
                System.out.println(String.format("moveBits = %016x", p.getMoveBits(emptyBoard)));
                Collection<Move> mvs = p.getMoves(emptyBoard);
                if (oneOf(pieceType,SLIDING_PIECES))
                for (int i = 0; i < 8; i++) {
                    System.out.println( String.format("rays[%d] = %016x", 
                            i, AbstractPiece.rayAttacks[i][posIndex]) );
                }
                System.out.println("Piece: " + p);
                System.out.println("Moves for " + pos + " = " + mvs.size());
                System.out.println("Cache size = " + Move.CACHE_SIZE());
                System.out.println(LINE);
            }
        }
        System.out.println(Pieces.getInstance().size());
    }
    
    public static boolean oneOf(int v, int[] set) {
        for (int s : set) {
            if (v == s) return true;
        }
        return false;
    }
    
}
