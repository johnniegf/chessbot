package de.htwsaar.chessbot.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.io.IOException;

/**
* Simplification for reading various types from an InputStream.
*
* <p>Usage</p>
* <p>
*   <code>Input consoleInput = new Input();</code><br/>
*   <code>//Read a String from System.in</code><br/>
*   <code>String str = input.readLine().toString();</code><br/>
*   <code>input = new Input( new FileInputStream(new File("myFile")) );</code><br/>
*   <code>//Read a double from myFile</code><br/>
*   <code>double d = input.readLine().toDouble();</code><br/>
* </p>
*
* @author Johannes Haupt
*/
public class Input {
    
    private static final InputStream DEFAULT_SOURCE = System.in;

    private BufferedReader reader;

    public Input() {
        this(DEFAULT_SOURCE);
    }

    public Input(final InputStream iStream) {
        setInputSource(iStream);
    }

    /**
    * Reads a line from the input source.
    *
    * @return a result containing the read line or an empty result
    *         if there was an I/O error during the operation.
    */
    public Result readLine() {
        try {
            String line = reader.readLine();
            return new Result(line);
        } catch (IOException ioe) {
            return new Result();
        }
    }

    /**
    * Specifies the InputStream to read from.
    *
    * @param source new input source.
    */
    public void setInputSource(InputStream source) {
        if ( source != null )
            this.reader = new BufferedReader( new InputStreamReader(source) );
    }

    /**
    * Wrapper object, that allows various type casts on read lines.
    */
    public static class Result {
        private final String line;

        /**
        * Create an empty result.
        */
        public Result() {
            this(null);
        }

        /**
        * Create a result containing the specified line.
        *
        * @param line the line to be contained in this Result object.
        *        If the line is <code>null</code>, the result will be empty.
        */
        public Result(String line) {
            this.line = line;
        }

        /**
        * Return true if this result is empty.
        * Note that an empty result does not contain an empty string but rather <code>null</code>
        *
        * @return true if and only if this result is empty.
        */
        public boolean empty() {
            return line == null; 
        }

        /**
        * Returns a char representation of this result (primitive type).
        */
        public char   toChar() {
            if ( empty() || line.isEmpty() )
                throw new ReadInputIsEmpty();
            return line.charAt(0);
        }

        /**
        * Returns the contained line.
        */
        public String toString() {
            return line;
        }

        /**
        * Returns a byte representation of this result (primitive type).
        */
        public byte   toByte() {
            if ( empty() )
                throw new ReadInputIsEmpty();
            try {
                return Byte.parseByte(line);
            } catch (NumberFormatException nfe) {
                throw new CantConvertInputToGivenType(line, "byte", nfe);
            }
        }

        /**
        * Returns a short representation of this result (primitive type).
        */
        public short  toShort() {
            if ( empty() )
                throw new ReadInputIsEmpty();
            try {
                return Short.parseShort(line);
            } catch (NumberFormatException nfe) {
                throw new CantConvertInputToGivenType(line, "short", nfe);
            }
        }

        /**
        * Returns an int representation of this result (primitive type).
        */
        public int    toInt() {
            if ( empty() )
                throw new ReadInputIsEmpty();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException nfe) {
                throw new CantConvertInputToGivenType(line, "int", nfe);
            }
        }
        
        /**
        * Returns a long representation of this result (primitive type).
        */
        public long   toLong() {
            if ( empty() )
                throw new ReadInputIsEmpty();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException nfe) {
                throw new CantConvertInputToGivenType(line, "long", nfe);
            }

        }

        /**
        * Returns a float representation of this result (primitive type).
        */
        public float  toFloat() {
            if ( empty() )
                throw new ReadInputIsEmpty();
            try {
                return Float.parseFloat(line);
            } catch (NumberFormatException nfe) {
                throw new CantConvertInputToGivenType(line, "float", nfe);
            }

        }

        /**
        * Returns a double representation of this result (primitive type).
        */
        public double toDouble() {
            if ( empty() )
                throw new ReadInputIsEmpty();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException nfe) {
                throw new CantConvertInputToGivenType(line, "double", nfe);
            }

        }

    }

}
