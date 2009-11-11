/**
 * 28-Feb-2006
 */
package com.ixora.common.collections;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Moraru
 */
public class CircullarLinkedList<T> extends LinkedList<T> {
    private static final long serialVersionUID = 1436552581122234265L;

	private int fSize;

	/**
	 *
	 */
	public CircullarLinkedList(int size) {
		super();
		if(size == 0) {
			throw new IllegalArgumentException("Buffer size must be > 0");
		}
		fSize = size;
	}

	/**
	 * @param c
	 */
	public CircullarLinkedList(Collection<T> c, int size) {
		this(size);
		addAll(c);
	}

	/**
	 * @param index
	 * @param element
	 */
	public void add(int index, T element) {
		if(index >= fSize) {
			throw new IllegalArgumentException("Index: " + index + " Size: " + fSize);
		}
		shiftIfNeeded();
		super.add(index, element);
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean add(T o) {
		shiftIfNeeded();
		return super.add(o);
	}

	/**
	 * @param o
	 * @return T the element removed from the list or null
	 */
	public T addAndReturnRemoved(T o) {
		T ret = shiftIfNeeded();
		super.add(o);
		return ret;
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean addAll(Collection<? extends T> c) {
		if(c == null) {
			throw new IllegalArgumentException("Collection is null");
		}
		if(c.size() == 0) {
			return false;
		}
		for(T t : c) {
			add(t);
		}
		return true;
	}

	/**
	 * @param index
	 * @param c
	 * @return
	 */
	public boolean addAll(int index, Collection<? extends T> c) {
		if(c == null) {
			throw new IllegalArgumentException("Collection is null");
		}
		if(c.size() == 0) {
			return false;
		}
		int i = 0;
		for(T t : c) {
			T shifted = shiftIfNeeded();
			int newIndex = (shifted != null) ? index : index + 1;
			super.add(newIndex, t);
			i++;
		}
		return true;
	}

	/**
	 * @param o
	 */
	public void addFirst(T o) {
		shiftIfNeeded();
		super.addFirst(o);
	}

	/**
	 * @param o
	 */
	public void addLast(T o) {
		shiftIfNeeded();
		super.addLast(o);
	}

	/**
	 * Removes the first element in the list if there is no
	 * free space in the circular buffer.
	 * @return T the element remove or null
	 */
	private T shiftIfNeeded() {
		if(size() == fSize) {
			return removeFirst();
		}
		return null;
	}

	/**
	 * @param newSize
	 */
	public void resize(int newSize) {
		fSize = newSize;
		List<T> clone = new LinkedList<T>(this);
		clear();
		addAll(clone);
	}
}
