package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* Beschreibung.
*
* @author Dominik Becker
* @author Johannes Haupt
*/
public class GameTree {

	private static final EvaluationFunction DEFAULT_EVALUATOR = new MaterialEvaluator();
	
    private EvaluationFunction mEval;
    private Node mRoot = null;
    /**
    * Standardkonstruktor.
    */ 
    public GameTree() {
    	this(DEFAULT_EVALUATOR);
    }
    
    public GameTree(final EvaluationFunction evaluationFunction) {
    	this(evaluationFunction, null);
    }
    
    public GameTree(final EvaluationFunction evaluator, final Board board) {
    	checkNull(evaluator, "evaluator");
    	
    	mEval = evaluator;
    	setRoot(board, 0);
    }

    public void deepen(final int toDepth) {
        if (toDepth < 1)
            if (mRoot.getScore() == 0) 
                mRoot.setScore( mEval.evaluate(mRoot.getBoard()) );
        else {
            deepen(toDepth - 1);
            Board b;
            for (Node n : getLayer(toDepth-1)) {
    	        for(Move m : n.getBoard().getMoveList()) {
    		        b = m.execute(n.getBoard());
    		        n.addChild(new Node(b));
    	        } 
                Collections.sort( n.getChildren() );
                Collections.reverse( n.getChildren() );
            }
        }
    }

    public List<Node> getLayer(int atDepth) {
        return getLayerP(getRoot(), atDepth);
    }

    private List<Node> getLayerP(Node root, int depth) {
        List<Node> l = new ArrayList<Node>();
        if (depth < 1)
            l.add(root);
        else {
            for (Node n : root.getChildren())
                l.addAll( getLayerP(n, depth-1) );
        }
        return l;
    }
    
    public Node getRoot() {
        return mRoot;
    }
    
    public void setRoot(final Board board, final int score) {
    	if (board == null)
    		mRoot = Node.NULL_NODE; 
    	else {
    		mRoot = new Node(board);
    		mRoot.setScore(score);
    	}
    }
    
    public void setRoot(final Node newRoot) {
    	checkNull(newRoot, "newRoot");
    	
    	mRoot = newRoot;
    }


    private static final class Node
    			   implements Comparable<Node>
    {
    	
        public static final Node NULL_NODE = new Node();

        private Node mParent;
        private List<Node > mChildren;
        private Score mValue;

        private Node() {
            this(new Board());   
        }

        public Node(final Board board) {
            this(board, null);
        }

        public Node(final Board board, final Node parent) {
            this(board, parent, new ArrayList<Node >());      
        }

        public Node(final Board board,
                    final Node parent, 
                    final List<Node > children) 
        {
            checkNull(children, "children");

            mParent   = parent;
            mChildren = new ArrayList<Node>();
            setValue(new Score(board));
            for (Node child : children) {
                addChild(child);
            }
        }

        public boolean isRoot() {
            return getParent() == null;
        }

        public Node getParent() {
            return mParent;
        }
        
        public void setParent(final Node parent) {
        	mParent = parent;
        }

        public int childCount() {
            return mChildren.size();
        }

        public boolean addChildren(final List<Node > children) {
            checkNull(children, "children");
            boolean modified = false;
            for (Node child : children) {
                if ( addChild(child) && !modified ) 
                    modified = true;
            }
            return modified;
        }

        public boolean addChild(final Node child) {
            return addChild(child, childCount());
        }

        public boolean addChild(final Node child, final int atIndex) {
            checkNull(child);

            mChildren.add(atIndex, child);
            child.setParent(this);
            return true;   
        }

        /**
        * Gib den Kindknoten am übergebenen Index zurück.
        *
        * @param atIndex Index des gesuchten Knotens.
        * @return cd
        *
        */
        public Node getChild(final int index) {
            checkInBounds(index, "index", 1, getChildren().size());
            int listIndex = index-1;
            return mChildren.get(listIndex);
        }

        /**
        * Gib die Liste der Kindknoten zurück.
        */
        public List<Node > getChildren() {
            return mChildren;
        }

        /**
        * Entferne den Kindknoten mit der übergebenen Nummer.
        *
        * @param atIndex Index des zu entfernenden Knotens
        * @return den entfernten Knoten.
        */
        public Node removeChild(final int atIndex) {
            Node child = getChild(atIndex);
            mChildren.remove(atIndex);
            child.setParent(null);
            return child;
        }

        public Score getValue() {
            return mValue;
        }

        public void setValue(final Score value) {
            checkNull(value, "value");

            mValue = value;
        }

        public Board getBoard() {
            return getValue().board;
        }

        /**
        * Lege die Stellung dieses Knotens fest.
        *
        * @param board Stellung des Knotens
        */
        public void setBoard(final Board board) {
            checkNull(board, "board");
            getValue().board = board;
        }

        /**
        * Gib die Bewertung der Stellung diesen Knotens zurück.
        *
        * @return Bewertung der enthaltenen Stellung
        */
        public int getScore() {
            return getValue().score;
        }

        /**
        * Lege die Bewertung der Stellung diesen Knotens fest.
        *
        * @param Bewertung der enthaltenen Stellung
        */
        public void setScore(final int score) {
            getValue().score = score;
        }
        
        public int compareTo(final Node other) {
        	if (other == null)
        		return 1;
        	return this.getValue().compareTo(other.getValue());
        	
        }
    }

}

class Score implements Comparable<Score> {
	public Score() {}
	
	public Score(final Board b) {
		checkNull(b, "b");
		this.board = b;
		this.score = 0;
	}
	
	public int compareTo(final Score other) {
		if (other == null)
			return 1;
		return Integer.compare(this.score, other.score);
	}
	
    public Board board;

    public int score;
}
