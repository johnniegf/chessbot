package de.htwsaar.chessbot.util;

public interface Output {
    /**
    * Line separator, OS-dependent (e.g.\ "\n" on UNIX)
    */
    String NEWLINE = System.lineSeparator();
    String SPACE   = " ";
    String TAB     = "\t";

    /**
    * Print out the specified message.
    *
    * Specifically prints out what is returned by the message objects' <code>toString()</code> method.
    */
    void print(final Object message);

    /**
    * Print a line separator.
    */
    void println();

    /**
    * Print out the specified message and add a line separator.
    *
    * @see Output.print
    */
    void println(final Object message);

    /**
    * Print a formatted string.
    *
    * @see man printf
    */
    void printf(final String format, final Object ... args);
}
