/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.model;

import de.htwsaar.chessbot.util.Bitwise;
import static de.htwsaar.chessbot.util.Exceptions.checkCondition;
import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.msg;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class BoardUtils {
    
    public static void checkBitBoardPosition(final long bb) {
        if (Bitwise.lowestBitIndex(bb) > 63)
            throw new BoardException(EXN_ILLEGAL_BITBOARD_POS);
                       
    }
    
    public static int toIndex(final long bb) {
        return Bitwise.lowestBitIndex(bb);
    }
    
    public static long toBitBoard(final int index) {
        checkInBounds(index, 0, 63);
        return 1L << index;
    }
    
    private static final String EXN_ILLEGAL_BITBOARD_POS = "Position darf nur ein 1-bit enthalten";
    
    private static final long NOT_H_FILE = 0x7f7f_7f7f_7f7f_7f7fL;
    private static final long NOT_A_FILE = 0xfefe_fefe_fefe_fefeL;
    private static long[] SHIFT_MASKS = new long[8];
    private static final int[] DIRECTION_SHIFTS = new int[] {
        8, 9, 1, -7, -8, -9, -1, 7
    };
    
    public static final int North = 0;
    public static final int NorthEast = 1;
    public static final int East = 2;
    public static final int SouthEast = 3;
    public static final int South = 4;
    public static final int SouthWest = 5;
    public static final int West = 6;
    public static final int NorthWest = 7;
    
    public static boolean isNegativeRay(int direction) {
        checkInBounds(direction, North, NorthWest);
        return DIRECTION_SHIFTS[direction] < 0;
    }
    
    public static boolean isPositiveRay(int direction) {
        return !isNegativeRay(direction);
    }
    
    public static long shift(int direction, long sq, int delta) {
        long result = sq;
        for (int i = 0; i < delta; i++) {
            result = shift(direction,result);
        }
        return result;
    }
    
    public static long shift(int direction, long sq) {
        int shift = DIRECTION_SHIFTS[direction];
        if (shift < 0)
            return (sq >>> (-shift)) & SHIFT_MASKS[direction];
        else
            return (sq << shift) & SHIFT_MASKS[direction];
    }
    
    public static long getFileMask(final int file) {
        return FILE_MASK[file];
    }
    
    public static long getRankMask(final int rank) {
        return RANK_MASK[rank];
    }
    
    private static long[]   FILE_MASK  = new long[8];
    private static long[]   RANK_MASK  = new long[8];

    private static void initMasks() {
        long fileMask = 0x0101_0101_0101_0101L;
        long rankMask = 0x0000_0000_0000_00ffL;
        for (int i = 0; i < 8; i++, 
                               fileMask <<= 1,
                               rankMask <<= 8) 
        {
            FILE_MASK[i] = fileMask;
            RANK_MASK[i] = rankMask;
        }
        
        SHIFT_MASKS[North] = ~0L;
        SHIFT_MASKS[NorthEast] = NOT_A_FILE;
        SHIFT_MASKS[East] = NOT_A_FILE;
        SHIFT_MASKS[SouthEast] = NOT_A_FILE;
        SHIFT_MASKS[South] = ~0L;
        SHIFT_MASKS[NorthWest] = NOT_H_FILE;
        SHIFT_MASKS[West] = NOT_H_FILE;
        SHIFT_MASKS[SouthWest] = NOT_H_FILE;

    }
    
    
    static {
        initMasks();
    }
    
    
    public static final class Color {
        public static final int WHITE = 0;
        public static final int BLACK = 1;
        public static final int BOTH  = 2;
        public static final int[] COLORS = new int[] { WHITE, BLACK };

           
        public static int toColor(boolean isWhite) {
            return (isWhite ? WHITE : BLACK);
        }
    
        public static boolean toBool(final int color) {
            return color == WHITE;
        }

        public static int invert(final int color) {
            return (int) Bitwise.xor(color,1);
        }
        
        private Color() {

        }
    }
    
}
