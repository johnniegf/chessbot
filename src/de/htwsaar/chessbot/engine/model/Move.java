package de.htwsaar.chessbot.engine.model;

import java.util.*;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Move {

    public static final char FLAG = '0';

    private Position mStart;
    private Position mTarget;

    /**
    * Erzeuge einen nicht initialisierten Zug.
    */
    public Move() {

    }

    /**
    * Erzeuge einen neuen Zug mit übergebenem Anfangs- und
    * Zielfeld.
    *
    * @param startingSquare Anfangsfeld
    * @param targetSquare Zielfeld
    */
    public Move(final Position startingSquare,
                final Position targetSquare)
    {
        setStart(startingSquare);
        setTarget(targetSquare);
    }

    /**
    * Gib das Startfeld des Zugs zurück.
    */
    public Position getStart() {
        return mStart;
    }

    /**
    * Lege das Startfeld des Zugs fest.
    *
    * @param start Position des Startfelds.
    * @throws NullPointerException falls <code>start == null</code>
    * @throws MoveException falls die Position des Startfelds ungültig ist.
    */
    public void setStart(final Position start) {
        if (start == null)
            throw new NullPointerException("start");
        if (!start.isValid())
            throw new MoveException(EXN_INVALID_START);

        mStart = start;
    }

    /**
    * Gib das Zielfeld des Zugs zurück. 
    */
    public Position getTarget() {
        return mTarget;
    }

    /**
    * Lege das Zielfeld des Zugs fest.
    *
    * @param target Position des Zielfelds.
    * @throws NullPointerException falls <code>target == null</code>
    * @throws MoveException falls die Position des Zielfelds ungültig ist.
    */
    public void setTarget(final Position target) {
        if (target == null)
            throw new NullPointerException("target");
        if (!target.isValid())
            throw new MoveException(EXN_INVALID_TARGET);

        mTarget = target;
    }

    /**
    * Gib zurück, ob dieser Zug in der übergebenen Stellung möglich ist.
    */
    public boolean isPossible(final Board context) {
        return tryExecute(context) != null;        
    }

    /**
    * Versuche, diesen Zug in der übergebenen Stellung auszuführen.
    *
    * @param context die Stellung des Bretts
    * @return die Stellung nach dem Zug, falls dieser ausgeführt werden
    *         kann, sonst <code>null</code>
    * @throws NullPointerException falls <code>context == null</code>
    */
    protected Board tryExecute(final Board context) {
        if (context == null)
            throw new NullPointerException("context");
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
    * Führe diesen Zug in der übergebenen Stellung aus.
    *
    * @param context die Stellung des Bretts
    * @return die Stellung nach dem Zug, falls dieser ausgeführt werden
    *         kann.
    * @throws NullPointerException falls <code>context == null</code>
    * @throws MoveException falls der Zug nicht ausgeführt werden kann.
    */
    public Board execute(final Board context) {
        return tryExecute(context);
    }

    /**
    * Kopiere diesen Zug.
    */
    public Move clone() {
        Move copy = new Move();
        if (isInitialized()) {
            copy.setStart(getStart());
            copy.setTarget(getTarget());
        }
        return copy;
    }

    public boolean isInitialized() {
        return getStart() != null
            && getTarget() != null;
    }

    /**
    * Gib die Id dieser Zugart zurück.
    */
    protected char flag() {
        return FLAG; 
    }

    /**
    * Gib die UCI-Notation dieses Zugs zurück.
    */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getStart()).append(getTarget());
        return result.toString();
    }

    /**
    * Prüfe diesen Zug auf Gleichheit mit einem anderen Objekt.
    *
    * @return <code>true</code>, falls die Objekte gleich sind,
    *         sonst <code>false</code>
    */
    public boolean equals(final Object other) {
        if (other == null) return false;
        if (other == this) return true;

        if (other instanceof Move) {
            Move om = (Move) other;
            return getStart().equals(om.getStart())
                && getTarget().equals(om.getTarget())
                && flag() == om.flag();
        } else
            return false;
    }

    private static final String EXN_INVALID_START =
        "Das Startfeld ist keine gültige Position";
    private static final String EXN_INVALID_TARGET =
        "Das Zielfeld ist keine gültige Position";

    public static class Cache {
        private Map<Integer,Move> mCache;
        private Map<Character,Move> mPrototypes;
    
        public Cache(Set<Move> prototypes) {
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
    
        /**
        *
        */
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
    
        private Move create(final char flag, 
                            final Position start, 
                            final Position target) 
        {
            if (!mPrototypes.containsKey(flag))
                throw new IllegalArgumentException();
            
            Move m = mPrototypes.get(flag).clone();
            m.setStart(start);
            m.setTarget(target);
            return m;
        }
    
        private int makeIndex(final char flag, 
                              final Position start, 
                              final Position target) 
        {
            int index = 0;
            index += flag;
            index = index << 11;
            index += start.hashCode();
            index = index << 11;
            index += target.hashCode();
            return index;
        }
    }

}
