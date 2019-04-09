package com.tzpt.cloudlibrary.zlibrary.core.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLTree <T extends ZLTree<T>> implements Iterable<T> {
    private int mSize = 1;
    public final T Parent;
    public final int Level;
    private volatile List<T> mSubtrees;

    protected ZLTree() {
        this(null);
    }

    protected ZLTree(T parent) {
        this(parent, -1);
    }

    private ZLTree(T parent, int position) {
        if (position == -1) {
            position = parent == null ? 0 : parent.subtrees().size();
        }
        if (parent != null && (position < 0 || position > parent.subtrees().size())) {
            throw new IndexOutOfBoundsException("`position` value equals " + position + " but must be in range [0; " + parent.subtrees().size() + "]");
        }
        Parent = parent;
        if (parent != null) {
            Level = parent.Level + 1;
            parent.addSubtree((T)this, position);
        } else {
            Level = 0;
        }
    }

    public final int getSize() {
        return mSize;
    }

    public final boolean hasChildren() {
        return mSubtrees != null && !mSubtrees.isEmpty();
    }

    public List<T> subtrees() {
        if (mSubtrees == null) {
            return Collections.emptyList();
        }
        synchronized (mSubtrees) {
            return new ArrayList<T>(mSubtrees);
        }
    }

    public synchronized final T getTreeByParagraphNumber(int index) {
        if (index < 0 || index >= mSize) {
            // TODO: throw an exception?
            return null;
        }
        if (index == 0) {
            return (T)this;
        }
        --index;
        if (mSubtrees != null) {
            synchronized (mSubtrees) {
                for (T subtree : mSubtrees) {
                    if (((ZLTree<?>)subtree).mSize <= index) {
                        index -= ((ZLTree<?>)subtree).mSize;
                    } else {
                        return (T)subtree.getTreeByParagraphNumber(index);
                    }
                }
            }
        }
        throw new RuntimeException("That's impossible!!!");
    }

    synchronized final void addSubtree(T subtree, int position) {
        if (mSubtrees == null) {
            mSubtrees = Collections.synchronizedList(new ArrayList<T>());
        }
        final int subtreeSize = subtree.getSize();
        synchronized (mSubtrees) {
            final int thisSubtreesSize = mSubtrees.size();
            while (position < thisSubtreesSize) {
                subtree = mSubtrees.set(position++, subtree);
            }
            mSubtrees.add(subtree);
            for (ZLTree<?> parent = this; parent != null; parent = parent.Parent) {
                parent.mSize += subtreeSize;
            }
        }
    }

    synchronized public final void moveSubtree(T subtree, int index) {
        if (mSubtrees == null || !mSubtrees.contains(subtree)) {
            return;
        }
        if (index < 0 || index >= mSubtrees.size()) {
            return;
        }
        mSubtrees.remove(subtree);
        mSubtrees.add(index, subtree);
    }

    public void removeSelf() {
        final int subtreeSize = getSize();
        ZLTree<?> parent = Parent;
        if (parent != null) {
            parent.mSubtrees.remove(this);
            for (; parent != null; parent = parent.Parent) {
                parent.mSize -= subtreeSize;
            }
        }
    }

    public final void clear() {
        final int subtreesSize = mSize - 1;
        if (mSubtrees != null) {
            mSubtrees.clear();
        }
        mSize = 1;
        if (subtreesSize > 0) {
            for (ZLTree<?> parent = Parent; parent != null; parent = parent.Parent) {
                parent.mSize -= subtreesSize;
            }
        }
    }

    public final TreeIterator iterator() {
        return new TreeIterator(Integer.MAX_VALUE);
    }

    public final Iterable<T> allSubtrees(final int maxLevel) {
        return new Iterable<T>() {
            public TreeIterator iterator() {
                return new TreeIterator(maxLevel);
            }
        };
    }

    private class TreeIterator implements Iterator<T> {
        private T myCurrentElement = (T)ZLTree.this;
        private final LinkedList<Integer> myIndexStack = new LinkedList<Integer>();
        private final int myMaxLevel;

        TreeIterator(int maxLevel) {
            myMaxLevel = maxLevel;
        }

        public boolean hasNext() {
            return myCurrentElement != null;
        }

        public T next() {
            final T element = myCurrentElement;
            if (element.hasChildren() && element.Level < myMaxLevel) {
                myCurrentElement = (T)((ZLTree<?>)element).mSubtrees.get(0);
                myIndexStack.add(0);
            } else {
                ZLTree<T> parent = element;
                while (!myIndexStack.isEmpty()) {
                    final int index = myIndexStack.removeLast() + 1;
                    parent = parent.Parent;
                    synchronized (parent.mSubtrees) {
                        if (parent.mSubtrees.size() > index) {
                            myCurrentElement = parent.mSubtrees.get(index);
                            myIndexStack.add(index);
                            break;
                        }
                    }
                }
                if (myIndexStack.isEmpty()) {
                    myCurrentElement = null;
                }
            }
            return element;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
