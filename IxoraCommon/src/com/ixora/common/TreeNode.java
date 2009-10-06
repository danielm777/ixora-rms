/*
 * Created on 17-Jan-2005
 */
package com.ixora.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Daniel Moraru
 */
public abstract class TreeNode<K, T extends TreeNode>
	implements Serializable {
	/** Children */
	protected Map<K, T> fChildren;

	/**
	 * Constructor.
	 */
	protected TreeNode() {
		super();
	}

	/**
	 * @param eid
	 * @return
	 */
	public T getChild(K k) {
		if(fChildren == null) {
			return null;
		}
		return fChildren.get(k);
	}

	/**
	 * @return
	 */
	public Iterator<T> children() {
		if(fChildren == null) {
			return Collections.EMPTY_LIST.iterator();
		}
		return fChildren.values().iterator();
	}

	/**
	 * @return
	 */
	public int getChildrenCount() {
		if(fChildren == null) {
			return 0;
		}
		return fChildren.size();
	}

	/**
	 * @param t
	 */
	public void addChild(T n) {
		if(fChildren == null) {
			fChildren = new HashMap<K, T>();
		}
		fChildren.put((K)n.getKey(), n);
	}


	/**
	 * @return
	 */
	protected abstract K getKey();
 }
