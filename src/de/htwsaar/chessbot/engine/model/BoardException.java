/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.model;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class BoardException extends RuntimeException {

    /**
     * Creates a new instance of <code>BoardException</code> without detail
     * message.
     */
    public BoardException() {
    }

    /**
     * Constructs an instance of <code>BoardException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public BoardException(String msg) {
        super(msg);
    }
}
