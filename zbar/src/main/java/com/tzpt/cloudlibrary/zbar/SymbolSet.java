package com.tzpt.cloudlibrary.zbar;

/**
 * Immutable container for decoded result symbols associated with an image or a composite symbol.
 *
 * Created by Administrator on 2018/10/16.
 */
@SuppressWarnings("JniMissingFunction")
public class SymbolSet extends java.util.AbstractCollection<Symbol> {
    /**
     * C pointer to a zbar_symbol_set_t.
     */
    private long peer;

    static {
        System.loadLibrary("zbar");
        init();
    }

    private static native void init();

    /**
     * SymbolSets are only created by other package methods.
     */
    SymbolSet(long peer) {
        this.peer = peer;
    }

    protected void finalize() {
        destroy();
    }

    /**
     * Clean up native data associated with an instance.
     */
    public synchronized void destroy() {
        if (peer != 0) {
            destroy(peer);
            peer = 0;
        }
    }

    /**
     * Release the associated peer instance.
     */
    private native void destroy(long peer);

    /**
     * Retrieve an iterator over the Symbol elements in this collection.
     */
    public java.util.Iterator<Symbol> iterator() {
        long sym = firstSymbol(peer);
        if (sym == 0)
            return (new SymbolIterator(null));

        return (new SymbolIterator(new Symbol(sym)));
    }

    /**
     * Retrieve the number of elements in the collection.
     */
    public native int size();

    /**
     * Retrieve C pointer to first symbol in the set.
     */
    private native long firstSymbol(long peer);
}
