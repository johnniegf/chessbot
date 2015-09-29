package de.htwsaar.chessbot.engine.util;

import de.htwsaar.chessbot.engine.model.piece.Pieces;
import de.htwsaar.chessbot.engine.model.*;

public class PiecesTest {
    
    public static void main(String[] args) {
        char[] pcs = new char[] { 
            'P', 'p', 'B', 'b', 'N', 'n',
            'R', 'r', 'Q', 'q', 'K', 'k'
        };
        long mem = Runtime.getRuntime().freeMemory();
        for (char pcType : pcs) {
            for (int x = 1; x <= 8; x++) {
                for (int y = 1; y <= 8; y++) {
                    Pieces.PC(pcType, Position.P(y,x)).hash();
                }
            }
        }
        mem = mem - Runtime.getRuntime().freeMemory();
        double m = (mem / 1024) / 1024.;
        System.out.println((mem/1024/1024d) + "MB / " 
                          + Pieces.getInstance().size() 
                          + " = " 
                          + (mem / 1024d / Pieces.getInstance().size()) 
                          + "KB/Piece" );
    }

}
