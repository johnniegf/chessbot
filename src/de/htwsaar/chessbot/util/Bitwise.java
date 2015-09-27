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

    public static final byte lowestBit(final long value) {
        return (byte) Long.numberOfTrailingZeros(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static final byte highestBit(final long value) {
        return (byte) (63 - Long.numberOfLeadingZeros(value));
    }

}
