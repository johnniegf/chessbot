/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.engine.model.move;

import de.htwsaar.chessbot.engine.model.Board;
import de.htwsaar.chessbot.engine.model.Position;
import de.htwsaar.chessbot.engine.model.piece.Piece;
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
    
    public Board tryExecute(final Board context, final boolean recalc) {
        checkNull(context, "context");
        Piece pc = context.getPieceAt(getStart());
        if (!checkMove(context, pc)) return null;
        Board result = context.clone();
        if ( !doCapture(context.hash(), result, pc) ) return null;
        if ( !movePiece(result, pc) ) return null;
        if ( !togglePlayer(result)  ) return null;
        if ( !disableCastlings(pc, result)) return null;
        if ( !updateLastMove(this, result)) return null;
        if (recalc) 
            result.recalculateAttacks();

        return result;
    }
    
    @Override
    public Board tryExecute(final Board context) {
        return tryExecute(context, true);
    }
    
    
}
