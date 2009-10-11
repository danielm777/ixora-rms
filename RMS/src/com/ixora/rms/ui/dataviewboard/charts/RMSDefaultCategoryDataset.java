/*
 * Created on Aug 20, 2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.dataengine.QueryResultData;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.dataengine.external.QuerySeries;

/**
 * RMSDefaultCategoryDataset
 * @author Daniel Moraru
 */
public class RMSDefaultCategoryDataset extends AbstractDataset
	implements RMSDataset, CategoryDataset {
	private static final long serialVersionUID = -2668504022249635905L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(RMSDefaultCategoryDataset.class);
	/** ResourceId to plot on domain axis */
	private String domainID;
	/** ResourceIds to plot on range axis */
	private List<String> rangesIDs;
	private RMSKeyedMap2D map2DValues;

	public RMSDefaultCategoryDataset(String domainID, List<String> rangesIDs) {
		// Remember the name of the domain (x axis) QueryResult,
		// and of the ranges (y axis)
		this.domainID = domainID;
		this.rangesIDs = rangesIDs;
		this.map2DValues = new RMSKeyedMap2D();
	}

	public boolean inspectData(QueryData data) {
	    try {
			@SuppressWarnings("unused")
			boolean addedValues = false;
			boolean addedSeries = false;
			// Each series will have a category (X axis) and one or more
			// values on the Y axis
			for (QuerySeries series : data) {

				// Extract the category value
				String categoryName = "";
				if (domainID != null) {
					QueryResultData qrdCtg = series.getData(domainID);
                    categoryName = qrdCtg.getQueryResult().getStyle().getIName();
                    if (Utils.isEmptyString(categoryName)) {
                        categoryName = qrdCtg.getValue().toString();
                    }
				}

				// Extract all other values in the series
				for (QueryResultData qrd : series) {
					String name = qrd.getQueryResult().getStyle().getName();
					String id = qrd.getQueryResult().getStyle().getID();

					// Search only for required ranges
					if (rangesIDs.contains(id)) {
						if(this.getRowIndex(id) < 0) {
						    addedValues = true;
						}
						// set a flag if this is a new series
						if (getRowIndex(name) == -1) {
							addedSeries = true;
						}

						map2DValues.setValue(
						        new Double(qrd.getValue().getDouble()),
								name, // series name
								categoryName); // category name
					}
				}
			}

			// Repaint chart
		    this.fireDatasetChanged();

			return addedSeries;
	    } catch (Exception e) {
	        // QueryNoSuchResult should not happen
	        logger.error("Exception in inspectData", e);
	        return false;
	    }
	}

    /**
     * @see org.jfree.data.KeyedValues2D#getRowKey(int)
     */
    public Comparable<?> getRowKey(int row) {
        return map2DValues.getRowKey(row);
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getRowIndex(java.lang.Comparable)
     */
    @SuppressWarnings("unchecked")
	public int getRowIndex(Comparable key) {
        return map2DValues.getRowIndex(key);
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getRowKeys()
     */
	public List<Comparable<?>> getRowKeys() {
        return map2DValues.getRowKeys();
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getColumnKey(int)
     */
    public Comparable<?> getColumnKey(int column) {
        return map2DValues.getColumnKey(column);
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getColumnIndex(java.lang.Comparable)
     */
    @SuppressWarnings("unchecked")
	public int getColumnIndex(Comparable key) {
        return map2DValues.getColumnIndex(key);
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getColumnKeys()
     */
    public List<Comparable<?>> getColumnKeys() {
        return map2DValues.getColumnKeys();
    }

    /**
     * @see org.jfree.data.KeyedValues2D#getValue(java.lang.Comparable, java.lang.Comparable)
     */
    @SuppressWarnings("unchecked")
	public Number getValue(Comparable rowKey, Comparable columnKey) {
        return map2DValues.getValue(rowKey, columnKey);
    }

    /**
     * @see org.jfree.data.Values2D#getRowCount()
     */
    public int getRowCount() {
        return map2DValues.getRowCount();
    }

    /**
     * @see org.jfree.data.Values2D#getColumnCount()
     */
    public int getColumnCount() {
        return map2DValues.getColumnCount();
    }

    /**
     * @see org.jfree.data.Values2D#getValue(int, int)
     */
    public Number getValue(int row, int column) {
        return map2DValues.getValue(row, column);
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.charts.RMSDataset#reset()
	 */
	public void reset() {
		map2DValues.reset();
	    this.fireDatasetChanged();
	}
}
