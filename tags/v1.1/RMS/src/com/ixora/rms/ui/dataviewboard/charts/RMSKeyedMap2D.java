/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jfree.data.KeyedValues2D;

/**
 * RMSKeyedMap2D
 * Optimized implementation for Values2D and KeyedValues2D, to be
 * used with RMSDefaultCategoryDataset instead of DefaultKeyedValues2D
 */
public class RMSKeyedMap2D implements KeyedValues2D {
    private class MapKey {
        private int	row;
        private int	col;

        public MapKey(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null)
                return false;
            MapKey mk = (MapKey)obj;
            return mk.col == col && mk.row == row;
        }

        public int hashCode() {
            return row * col;
        }
    }

    private HashMap<MapKey, Number>			mapValues = new HashMap<MapKey, Number>();
    private HashMap<Comparable<?>, Integer>	mapRowKeys = new HashMap<Comparable<?>, Integer>();
    private HashMap<Comparable<?>, Integer>	mapColKeys = new HashMap<Comparable<?>, Integer>();

    public void setValue(Number value, Comparable<?> rowKey, Comparable<?> colKey) {
        int nRow = getRowIndex(rowKey);
        if (nRow == -1) {
            nRow = mapRowKeys.size();
            mapRowKeys.put(rowKey, new Integer(nRow));
        }
        int nCol = getColumnIndex(colKey);
        if (nCol == -1) {
            nCol = mapColKeys.size();
            mapColKeys.put(colKey, new Integer(nCol));
        }
        MapKey	mk = new MapKey(nRow, nCol);
        mapValues.put(mk, value);
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getRowKey(int)
     */
    public Comparable<?> getRowKey(int row) {
        for (Iterator<Map.Entry<Comparable<?>, Integer>> it = mapRowKeys.entrySet().iterator(); it.hasNext();) {
        	Map.Entry<Comparable<?>, Integer> me = it.next();
            if (me.getValue().intValue() == row)
                return me.getKey();
        }
        return null;
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getRowIndex(java.lang.Comparable)
     */
    @SuppressWarnings("unchecked")
	public int getRowIndex(Comparable key) {
        Integer intVal = mapRowKeys.get(key);
        if (intVal != null)
            return intVal.intValue();
        return -1;
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getRowKeys()
     */
	public List<Comparable<?>> getRowKeys() {
        return new LinkedList<Comparable<?>>(mapRowKeys.keySet());
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getColumnKey(int)
     */
    public Comparable<?> getColumnKey(int column) {
        for (Iterator<Map.Entry<Comparable<?>, Integer>> it = mapColKeys.entrySet().iterator(); it.hasNext();) {
        	Map.Entry<Comparable<?>, Integer> me = it.next();
            if (me.getValue().intValue() == column)
                return me.getKey();
        }
        return null;
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getColumnIndex(java.lang.Comparable)
     */
    @SuppressWarnings("unchecked")
	public int getColumnIndex(Comparable key) {
        Integer intVal = mapColKeys.get(key);
        if (intVal != null)
            return intVal.intValue();
        return -1;
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getColumnKeys()
     */
    public List<Comparable<?>> getColumnKeys() {
        return new LinkedList<Comparable<?>>(mapColKeys.keySet());
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getValue(java.lang.Comparable, java.lang.Comparable)
     */
    @SuppressWarnings("unchecked")
	public Number getValue(Comparable rowKey, Comparable columnKey) {
        int nRow = getRowIndex(rowKey);
        int nCol = getColumnIndex(columnKey);
        return getValue(nRow, nCol);
    }

    /**
     * @see org.jfree.data.Values2D#getRowCount()
     */
    public int getRowCount() {
        return mapRowKeys.size();
    }

    /**
     * @see org.jfree.data.Values2D#getColumnCount()
     */
    public int getColumnCount() {
        return mapColKeys.size();
    }

    /**
     * @see org.jfree.data.Values2D#getValue(int, int)
     */
    public Number getValue(int row, int column) {
        MapKey	mk = new MapKey(row, column);
        return mapValues.get(mk);
    }

    /**
     * Resets data.
     */
    public void reset() {
    	mapColKeys.clear();
    	mapRowKeys.clear();
    	mapValues.clear();
    }
}
