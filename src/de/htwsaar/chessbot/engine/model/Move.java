package de.htwsaar.chessbot.engine.model;

import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
/**
* Schachzug.
*
* Ein Schachzug hat ein Start- und ein Zielfeld. Unterklassen können evtl.
* weitere Eigenschaften definieren. Zugobjekte sind kontextabhängig, d.h. 
* ein- und derselbe Zug kann in verschiedenen Stellungen möglich sein 
* oder nicht.
*
* Züge werden erzeugt und vorgehalten vom <code>Move.Cache</code>. So
* existiert jedes differenzierbare Zugobjekt genau einmal.
*
* @author Johannes Haupt
*/
public class Move {

    private static Move.Cache sCache;

    public static int CACHE_SIZE() {
        return sCache.size();
    }

    private static Move.Cache getCache() {
        if (sCache == null) {
            Collection<Move> proto = Arrays.asList( new Move[] {
                new Move(), 
                new DoublePawnMove(), 
                new Castling(),
                new MoveEnPassant(),
                new MovePromotion(new Queen()),
                new MovePromotion(new Rook()),
                new MovePromotion(new Bishop()),
                new MovePromotion(new Knight())
            });
            sCache = new Move.Cache(proto);
        }
        return sCache;
    }

    public static Move MV(final Position from, 
                          final Position to, 
                          final char flag) 
    {
        return getCache().get(flag, from, to);
    }

    public static Move MV(final Position from, final Position to) {
        return MV(from, to, Move.FLAG);
    }

    public static final char FLAG = '0';

    private Position mStart;
    private Position mTarget;
    private transient String tMoveString;

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
        Board result = tryExecute(context);
        return result != null && result.isValid();
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
        Piece pc = result.getPieceAt(getStart());
        // Existiert die Figur?
        if ( pc == null ) return null;
        // Ist die Farbe überhaupt am Zug?
        if ( pc.isWhite() != result.isWhiteAtMove()) return null;
        // Kann die Figur auf das Zielfeld ziehen?
        if ( !pc.canMoveTo(result, getTarget())) {
            return null;
        }

        // Ist das Zielfeld besetzt...
        if ( !result.isFree(getTarget()) ) {
            // ... und die Figur darauf von der selben Farbe wie die gezogene?
            if ( result.getPieceAt(getTarget()).isWhite() == pc.isWhite() )
                return null;
            // Wenn nein, dann schlage die Figur auf dem Zielfeld
            else {
                result.removePieceAt(getTarget());
                result.setHalfMoves(0);
            }
        } else {
            if (pc.id() != Pawn.ID)
                result.setHalfMoves(result.getHalfMoves()+1);
            else
                result.setHalfMoves(0);
        }
        result.putPiece( pc.move(getTarget()) );
        result.removePieceAt(getStart());
        result.togglePlayer();
        if (result.isWhiteAtMove())
            result.setFullMoves(result.getFullMoves()+1);
        result.setEnPassant(Position.INVALID);
        if (pc.id() == King.ID || pc.id() == Rook.ID)
            result.calculateHash();
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
        Board result = tryExecute(context);
        if (result == null || !result.isValid())
            throw new MoveException("Unmöglicher Zug " + getClass().getSimpleName() + this + " auf " + context);
        return result;
    }

    /**
    * Kopiere diesen Zug.
    */
    public Move clone() {
        Move copy = create();
        if (isInitialized()) {
            copy.setStart(getStart());
            copy.setTarget(getTarget());
        }
        return copy;
    }

    protected Move create() {
        return new Move();
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
        if (tMoveString == null) {
            StringBuilder result = new StringBuilder();
            result.append(getStart()).append(getTarget());
            tMoveString = result.toString();
        }
        return tMoveString;
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
    
        public Cache(Collection<Move> prototypes) {
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

        public int size() {
            return mCache.size();
        }
    
        /**
        * Gib den Zug mit der übergebenen Art, Start- und Zielposition aus.
        */
        public Move get(char flag, Position start, Position target) {
        	//System.out.println("Move.Cache.get("+flag+","+start+","+target+")");
            if (start == null)
                throw new NullPointerException("start");
            if (target == null)
                throw new NullPointerException("target");
    
            int index = makeIndex(flag, start, target);
            Move m;
            synchronized(mCache) {
                m = mCache.get(index);
                if (m == null) {
                    m = create(flag, start, target);
                    mCache.put(index, m);
                }
            }
            return m;
        }
    
        private Move create(final char flag, 
                            final Position start, 
                            final Position target) 
        {
            if (!mPrototypes.containsKey(flag))
                throw new IllegalArgumentException("flag = " + flag);
            
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
