package de.htwsaar.chessbot.engine.model;

import static de.htwsaar.chessbot.util.Exceptions.*;
import de.htwsaar.chessbot.util.Tree;
import java.util.List;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class GameTree
     extends Tree<Score> 
{

    private EvaluationFunction mEval;
    
    /**
    * Standardkonstruktor.
    */ 
    public GameTree(final EvaluationFunction evaluationFunction) {
        checkNull(evaluationFunction, "evaluationFunction");

        mEval = evaluationFunction;
    }


    public static final class Node
                      extends Tree.Node<Score>
    {
        public Node() {
            super(new Score());
        }

        public Node(final Score score, final Node parent) {
            super(score, parent);
        }

        public Node(final Score score,
                    final Node parent, 
                    final List<Tree.Node<Score> > children) 
        {
            super(score, parent, children);

        }

        public Board getBoard() {
            return getValue().board;
        }

        public void setBoard(final Board board) {
            checkNull(board, "board");
            getValue().board = board;
        }

        public int getScore() {
            return getValue().score;
        }

        public void setScore(final int score) {
            getValue().score = score;
        }
    }

}

class Score {
    public Board board;

    public int score;
}
