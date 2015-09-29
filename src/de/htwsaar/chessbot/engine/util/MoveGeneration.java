/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.move.Move;
import de.htwsaar.chessbot.engine.model.piece.Pawn;
import de.htwsaar.chessbot.engine.model.piece.Piece;
import de.htwsaar.chessbot.engine.model.piece.Pieces;
import de.htwsaar.chessbot.engine.model.piece.Rook;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class MoveGeneration {

    public static void main(String[] args) {
        
        Board emptyBoard = new Board();
        Piece p;
        for (int pieceType = Rook.ID; pieceType != Pawn.ID; pieceType++) {
            for (int posIndex = 0; posIndex < 64; posIndex++) {
                System.out.println(posIndex);
                p = Pieces.PC(pieceType, true, Position.P(posIndex));
                p.getMoves(emptyBoard);
            }
        }
        System.out.println(Move.CACHE_SIZE());
    }
    
}
