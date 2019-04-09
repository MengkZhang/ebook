package com.tzpt.cloudlibrary.zbar;

/**
 * Iterator over a SymbolSet.
 *
 * Created by Administrator on 2018/10/16.
 */

public class SymbolIterator implements java.util.Iterator<Symbol> {
    /**
     * Next symbol to be returned by the iterator.
     */
    private Symbol current;

    /**
     * SymbolIterators are only created by internal interface methods.
     */
    SymbolIterator(Symbol first) {
        current = first;
    }

    /**
     * Returns true if the iteration has more elements.
     */
    public boolean hasNext() {
        return (current != null);
    }

    /**
     * Retrieves the next element in the iteration.
     */
    public Symbol next() {
        if (current == null)
            throw (new java.util.NoSuchElementException
                    ("access past end of SymbolIterator"));

        Symbol result = current;
        long sym = current.next();
        if (sym != 0)
            current = new Symbol(sym);
        else
            current = null;
        return (result);
    }

    /**
     * Raises UnsupportedOperationException.
     */
    public void remove() {
        throw (new UnsupportedOperationException
                ("SymbolIterator is immutable"));
    }
}
