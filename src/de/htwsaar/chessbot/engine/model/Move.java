package de.htwsaar.chessbot.engine.model;

import java.util.*;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Move {

    private Position mStart;
    private Position mTarget;

    public Move(final Position startingSquare,
                final Position targetSquare)
    {
        setStart(startingSquare);
        setTarget(targetSquare);
    
    }

    /**
    *
    */
    public Position getStart() {
        return mStart;
    }

    public void setStart(final Position start) {
        if (start == null)
            throw new NullPointerException("start");

        mStart = start;
    }

    /**
    * 
    */
    public Position getTarget() {
        return mTarget;
    }

    public void setTarget(final Position target) {
        if (target == null)
            throw new NullPointerException("target");

        mTarget = target;
    }

    /**
    *
    */
    public boolean isPossible(final Board context) {
        return tryExecute(context) != null;        
    }

    protected Board tryExecute(final Board context) {
        Board result = context.clone();
        Piece pc = context.getPieceAt(getStart());
        if ( pc.isWhite() != context.isWhiteAtMove()) return null;
        if ( pc == null ) return null;
        if ( !pc.canMoveTo(context, getTarget())) return null;
        if ( !context.isFree(mTarget) )
            context.removePieceAt(getTarget());

        if (!ChessVariant.getActive().isLegal(result))
            return null;
        else
            return result;
    }

    /**
    *
    */
    public Board execute(final Board context) {
        return tryExecute(context);
    }

    public Move clone() {
        return new Move(mStart, mTarget);
    }

    /**
    *
    */
    protected char flag() {
        return '0';    
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getStart()).append(getTarget());
        return result.toString();
    }
}

class MoveCache {
    private Map<Integer,Move> mCache;
    private Map<Character,Move> mPrototypes;

    public MoveCache(Collection<Move> prototypes) {
        if (prototypes == null)
            throw new NullPointerException("prototypes");
        if (prototypes.size() < 1)
            throw new IllegalArgumentException("prototypes empty");
        mCache = new HashMap<Integer,Move>();
        mPrototypes = new HashMap<Character,Move>();
        for (Move m : prototypes) {
            mPrototypes.put(m.flag(), m);
        }
    }

    public Move get(char flag, Position start, Position target) {
        if (start == null)
            throw new NullPointerException("start");
        if (target == null)
            throw new NullPointerException("target");

        int index = makeIndex(flag, start, target);
        Move m = mCache.get(index);
        if (m == null) {
            m = create(flag, start, target);
        }
        return m;
    }

    private Move create(final char flag, final Position start, final Position target) {
        if (!mPrototypes.containsKey(flag))
            throw new IllegalArgumentException();
        
        Move m = mPrototypes.get(flag).clone();
        m.setStart(start);
        m.setTarget(target);
        return m;
    }

    private int makeIndex(char flag, Position start, Position target) {
        int index = 0;
        index += flag;
        index = index << 11;
        index += start.hashCode();
        index = index << 11;
        index += target.hashCode();
        return index;
    }
}
