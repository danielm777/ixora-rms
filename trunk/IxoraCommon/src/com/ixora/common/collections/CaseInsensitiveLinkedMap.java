/*
 * Created on 29-Aug-2004
 */
package com.ixora.common.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map accomodating case insensitive string keys.
 * @author Daniel Moraru
 */
public final class CaseInsensitiveLinkedMap<V> implements Map<String, V>, Serializable {
    private static final long serialVersionUID = 1436552581122892189L;

	private LinkedHashMap<String, V> map;

    public CaseInsensitiveLinkedMap() {
        super();
        map = new LinkedHashMap<String, V>();
    }
    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map arg0) {
        Map.Entry e;
        for(Iterator iter = arg0.entrySet().iterator(); iter.hasNext();) {
            e = (Map.Entry)iter.next();
            put(e.getKey().toString().toLowerCase(), (V)e.getValue());
        }
    }
    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key.toString().toLowerCase());
    }
    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object key) {
        return map.get(key.toString().toLowerCase());
    }
    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(String key, V val) {
        return map.put(key.toString().toLowerCase(), val);
    }
    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return map.remove(key.toString().toLowerCase());
    }
    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }
    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }
    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }
    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        map.clear();
    }
    /**
     * @see java.util.Map#keySet()
     */
    public Set<String> keySet() {
        return map.keySet();
    }
    /**
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        return map.values();
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set<Entry<String, V>> entrySet() {
        return map.entrySet();
    }
}