/*
 * Created on 07-Sep-2004
 */
package com.ixora.common.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A list of most recently used items.
 * @author Daniel Moraru
 */
public final class MRUList<T> extends LinkedList<T> {
	private static final long serialVersionUID = -2160820921769921961L;
	/** Maximum size */
    private int maxsize;

    /**
     * Constructor.
     * @param maxsize
     */
    public MRUList(int maxsize) {
        super();
        this.maxsize = maxsize;
    }

    /**
     * Constructor.
     * @param maxsize
     * @param coll
     */
    public MRUList(int maxsize, Collection<T> coll) {
        super();
        this.maxsize = maxsize;
        addAll(coll);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(T obj) {
		if(contains(obj)) {
			remove(obj);
			add(0, obj);
		} else {
			int size = size();
			if(size > maxsize) {
				remove(size - 1);
			}
			add(0, obj);
		}
		return true;
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
	public boolean addAll(Collection<? extends T> c) {
        int i = 0;
        for(Iterator<? extends T> iter = c.iterator();
        		iter.hasNext() && i < maxsize; ++i) {
            super.add(iter.next());
        }
        return true;
    }
}
