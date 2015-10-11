package de.htwsaar.chessbot.util;

import static de.htwsaar.chessbot.util.Exceptions.checkNull;
import java.io.*;

public class PrintStreamOutput
  implements Output
{
    private final PrintStream out;

    public PrintStreamOutput(final PrintStream target) {
        checkNull(target);
        this.out = target;
    }

    public void print(Object message) {
        this.out.print(
            message == null
                ? "<null>"
                : message.toString()
        );
    }

    public void println() {
        print(NEWLINE);
    }

    public void println(final Object message) {
        print(message);
        println();
    }

    public void printf(final String format, final Object ... params) {
        print( String.format(format, params) );
    }
}
