package de.htwsaar.chessbot.util;

import static de.htwsaar.chessbot.util.Exceptions.*;

import java.util.List;
import java.util.ArrayList;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public abstract class Tree<VAL> {
    
    private Node<VAL> mRoot = null;

    protected Tree() {

    }

    protected Tree(final Node<VAL> root) {
        mRoot = root;
    }

    public Node<VAL> getRoot() {
        return mRoot;
    }

    public static class Node<T> {

        private static final Node<?> NULL_NODE = null;

        private final Node<T> mParent;
        private List<Node<T> > mChildren;
        private T mValue;

        private Node() {
            this(null);   
        }

        public Node(final T value) {
            this(value, null);
        }

        public Node(final T value, final Node<T> parent) {
            this(value, parent, new ArrayList<Node<T> >());      
        }

        public Node(final T value,
                    final Node<T> parent, 
                    final List<Node<T> > children) 
        {
            checkNull(children, "children");

            mParent   = parent;
            mChildren = (List<Node<T> >) new ArrayList();
            setValue(value);
            for (Node<T> child : children) {
                addChild(child);
            }
        }

        public boolean isRoot() {
            return getParent() == null;
        }

        public Node<T> getParent() {
            return mParent;
        }

        public int childCount() {
            return mChildren.size();
        }

        public boolean addChildren(final List<Node<T> > children) {
            checkNull(children, "children");
            boolean modified = false;
            for (Node<T> child : children) {
                if ( addChild(child) && !modified ) 
                    modified = true;
            }
            return modified;
        }

        public boolean addChild(final Node<T> child) {
            return addChild(child, childCount());
        }

        public boolean addChild(final Node<T> child, final int atIndex) {
            checkNull(child);

            mChildren.add(atIndex, child);
            return true;   
        }

        /**
        * Gib den Kindknoten am übergebenen Index zurück.
        *
        * @param atIndex Index des gesuchten Knotens.
        * @return cd
        *
        */
        public Node<T> getChild(final int index) {
            checkInBounds(index, "index", 1, getChildren().size());
            int listIndex = index-1;
            return mChildren.get(listIndex);
        }

        /**
        * Gib die Liste der Kindknoten zurück.
        */
        public List<Node<T> > getChildren() {
            return mChildren;
        }

        /**
        * Entferne den Kindknoten mit der übergebenen Nummer.
        *
        * @param atIndex Index des zu entfernenden Knotens
        * @return den entfernten Knoten.
        */
        public Node<T> removeChild(final int atIndex) {
            Node<T> child = getChild(atIndex);
            mChildren.remove(atIndex);
            return child;
        }

        public T getValue() {
            return mValue;
        }

        public void setValue(final T value) {
            checkNull(value, "value");

            mValue = value;
        }
    }
}
