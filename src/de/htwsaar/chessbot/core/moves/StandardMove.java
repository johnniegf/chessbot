/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.core.moves;

import de.htwsaar.chessbot.core.Board;
import de.htwsaar.chessbot.core.Position;
import de.htwsaar.chessbot.core.pieces.Piece;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

/**
 *
 * @author Johannes Haupt <johnniegf@fsfe.org>
 */
public class StandardMove extends Move {
    
    public static final byte TYPE = 8;
    
    @Override
    public byte type() {
        return TYPE;
    }
    
    public StandardMove(final Position from, final Position to) {
        super(from,to);
    }
    
    @Override
    public Board tryExecute(final Board context) {
        checkNull(context, "context");
        Piece pc = context.getPieceAt(getStart());
        if (!checkMove(context, pc)) return null;
        Board result = context.clone();
        if ( !doCapture(context.hash(), result, pc) ) return null;
        if ( !movePiece(result, pc) ) return null;
        if ( !togglePlayer(result)  ) return null;
        if ( !disableCastlings(pc, result)) return null;
        if ( !updateLastMove(this, result)) return null;

        return result;
    }
    
    
    
}
