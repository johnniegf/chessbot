package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.util.Exceptions.checkInBounds;
import static de.htwsaar.chessbot.util.Exceptions.checkNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.htwsaar.chessbot.engine.model.GameTree.Node;

/**
 * Beschreibung.
 *
 * @author Dominik Becker
 * @author Johannes Haupt
 * @author David Holzapfel
 */
public class GameTree {

	private static final EvaluationFunction DEFAULT_EVALUATOR = new MaterialEvaluator();

	private EvaluationFunction mEval;
	private Node mRoot = null;
	private ArrayList<ArrayList<Node>> layers;

	/**
	 * Standardkonstruktor.
	 */ 
	public GameTree() {
		this(DEFAULT_EVALUATOR);
	}

	public GameTree(final EvaluationFunction evaluationFunction) {
		this(evaluationFunction, null);
	}

	public GameTree(final Board b){
		this(DEFAULT_EVALUATOR, b);
	}
	public GameTree(final EvaluationFunction evaluator, final Board board) {
		checkNull(evaluator, "evaluator");
		this.layers = new ArrayList<ArrayList<Node>>();

		mEval = evaluator;
		setRoot(board, 0);

		this.layers.add(new ArrayList<Node>());
		this.layers.get(0).add(getRoot());
	}

	public EvaluationFunction getEvaluationFunction() {
		return  this.mEval;
	}
	
	public void printTreeSize() {
		for(List<Node> layer : layers) {
			System.out.print(layer.size() + "-");
		}
		System.out.println();
	}

	public void deepen(final int toDepth, boolean max) {
		int nodes = 0;
		long time = System.currentTimeMillis();
		if (toDepth >= this.layers.size()) {
			deepen(toDepth - 1, !max);
			System.out.println("info string Start deepening to layer " + toDepth + "...");
			
			Board b;
			ArrayList<Node> layer = new ArrayList<Node>();
			for (Node n : getLayer(toDepth-1)) {
				for(Move m : n.getBoard().getMoveList()) {
					b = m.execute(n.getBoard());
					Node appendNode = new Node(b);
					appendNode.setLeadsTo(m);
					n.addChild(appendNode);
					layer.add(appendNode);
					nodes++;
				}
				Collections.sort(n.getChildren());
				if(max) {
					Collections.reverse(n.getChildren());
				}
			}
			this.layers.add(layer);
		}
		
		time = System.currentTimeMillis() - time;
		System.out.println("info string Done. (" + nodes + " nodes in " + time + "ms)");
	}

	public List<Node> getLayer(int atDepth) {
		return this.layers.get(atDepth);
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
	
	public void cutoff(Node parent, Node child, int parentDepth) {
		Node current;
		int idx;
		List<Node> layer = getLayer(parentDepth+1);
		int cutNodes = 0;
		int childCount = parent.childCount();
		int layerSize = layer.size();
		for (idx = 0; idx < parent.childCount(); idx++) {
			current = parent.getChild(idx);
			if (child.equals(current)) break;
		}
		for (int i = parent.childCount()-1; i > idx; i--) {
			current = parent.getChild(i);
			parent.removeChild(i);
			layer.remove(current);
			cutNodes++;
		}
		int newChildCount = parent.childCount();
		int newLayerSize = layer.size();
		String debug = "## cut %d of %d (%d remain) : %d -> %d";
		//System.out.println(String.format(debug, cutNodes, childCount, newChildCount, layerSize, newLayerSize));
	}

	public static final class Node
	implements Comparable<Node>
	{

		public static final Node NULL_NODE = new Node();

		private Node mParent;
		private List<Node > mChildren;
		private Score mValue;
		private Move leadsTo = null;

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
		
		public Move getLeadsTo() {
			return this.leadsTo;
		}
		
		public void setLeadsTo(Move move) {
			this.leadsTo = move;
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
			//return addChild(child, childCount());
			checkNull(child);

			mChildren.add(child);
			child.setParent(this);
			return true;
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
			checkInBounds(index, "index", 0, getChildren().size()-1);
			return mChildren.get(index);
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
			if(other == this) {
				return 0;
			}
			if (other == null)
				return 1;
			return this.getValue().compareTo(other.getValue());

		}
		
		@Override
		public boolean equals(Object other) {
			if (other == this) return true;
			if (other == null) return false;
			
			if (other instanceof Node) {
				Node n = (Node) other;
				if (!n.getBoard().equals(getBoard())) return false;
				if (!n.getLeadsTo().equals(getLeadsTo())) return false;
				if (n.getScore() != getScore()) return false;
				return true;
			} else 
				return false;
		}

		public void removeChild(Node c) {
			this.mChildren.remove(c);
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
