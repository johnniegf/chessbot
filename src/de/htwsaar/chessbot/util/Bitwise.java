package de.htwsaar.chessbot.util;

public final class Bitwise {

    public static final long not(final long value) {
        return ~value;
    }

    public static final long and(final long lhs, final long rhs) {
        return lhs & rhs;
    }

    public static final long or(final long lhs, final long rhs) {
        return lhs | rhs;
    }

    public static final long xor(final long lhs, final long rhs) {
        return lhs ^ rhs;
    }

    public static final long nand(final long lhs, final long rhs) {
        return ~ ( lhs & rhs );
    }

    public static final long nor(final long lhs, final long rhs) {
        return ~ ( lhs | rhs );
    }

    public static final long xnor(final long lhs, final long rhs) {
        return ~ ( lhs ^ rhs );
    }

    public static final byte lowestBitIndex(final long value) {
        return (byte) Long.numberOfTrailingZeros(value);
    }
    
    public static final long lowestBit(final long value) {
        return 1 << lowestBitIndex(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static final byte highestBitIndex(final long value) {
        return (byte) (63 - Long.numberOfLeadingZeros(value));
    }
    
    public static final long highestBit(final long value) {
        return 1 << highestBitIndex(value);
    }
    
    public static final byte count(final long value) {
        return (byte) Long.bitCount(value);
    }

    public static long popLowestBit(long value) {
        return xor(value, 1L << Bitwise.lowestBitIndex(value));
    }

}
