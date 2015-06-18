package de.htwsaar.chessbot.util;

import java.io.*;

public class ConsoleOutput
  implements Output
{
    private static final String lineSeparator = System.getProperty("line.separator");
    private static final PrintStream OUT = System.out;

    public void print(Object message) {
        OUT.print(message == null ? "<null>" : message.toString());
    }

    public void println() {
        print(lineSeparator);
    }

    public void println(Object message) {
        print(message);
        print(lineSeparator);
    }

    public void printf(String format, Object ... params) {
        print( String.format(format, params) );
    }
}
