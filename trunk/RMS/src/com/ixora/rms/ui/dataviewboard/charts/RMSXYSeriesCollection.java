/*
 * Created on 24-Aug-2004
 *
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.dataengine.QueryResultData;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.dataengine.external.QuerySeries;


/**
 * RMSXYSeriesCollection
 * @author Daniel Moraru
 */
public class RMSXYSeriesCollection extends XYSeriesCollection
	implements RMSDataset, TableXYDataset {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(RMSXYSeriesCollection.class);
	/** ResourceId to plot on domain axis */
	private String fDomainID;
	/** ResourceIds to plot on range axis */
	private List<String> fRangesIDs;
	/** The max number of items in every data series */
	private int fMaxItemCount;

	/**
	 * @param domainID
	 * @param rangesIDs
	 * @param maxItemCount
	 */
	public RMSXYSeriesCollection(String domainID, List<String> rangesIDs, int maxItemCount) {
		// Remember the name of the domain (x axis) QueryResult,
		// and of the ranges (y axis)
		fDomainID = domainID;
		fRangesIDs = rangesIDs;
		fMaxItemCount = maxItemCount;
	}


	/**
	 * @see com.ixora.rms.ui.dataviewboard.charts.RMSDataset#inspectData(com.ixora.rms.dataengine.external.QueryData)
	 */
	public boolean inspectData(QueryData data) {
	    try {
			boolean addedSeries = false;
			boolean addedValues = false;

			// it is important to preserve the order of the series as defined
			// by the ranges in the data view definition, so group here for this cycle
			// all series by the resource IDs defined in ranges
			Map<String, Map<String, XYSeries>> newData = null;

			// Each series will have a category (X axis) and one or more
			// values on the Y axis
			for(QuerySeries series : data) {
				// Extract the domain value
				QueryResultData qrdTS = series.getData(fDomainID);
				// Extract all other values
				for(QueryResultData qrd : series) {
					String name = qrd.getQueryResult().getStyle().getIName();
					String id = qrd.getQueryResult().getStyle().getID();

					// Search only for required ranges
					if (fRangesIDs.contains(id)) {
						// Is this value already in a series?
						XYSeries xySeries = null;
						for (Iterator its = getSeries().iterator(); its.hasNext();) {
							xySeries = (XYSeries)its.next();
							if (xySeries.getName().equals(name)) {
								break;
							} else {
								xySeries = null;
							}
						}

						// No, create a new series
						if (xySeries == null) {
							xySeries = new XYSeries(name);
							xySeries.setMaximumItemCount(fMaxItemCount);
							// Add the value before adding the series because addSeries
							// triggers a DatasetChanged event, which will need the data we're adding
							xySeries.add(qrdTS.getValue().getDouble(), qrd.getValue().getDouble(), false);
							if(newData == null) {
								newData = new HashMap<String, Map<String, XYSeries>>();
							}
							// get the series for the current id
							Map<String, XYSeries> seriesForId = newData.get(id);
							if(seriesForId == null) {
								seriesForId = new HashMap<String, XYSeries>();
								newData.put(id, seriesForId);
							}
							seriesForId.put(name, xySeries);
							addedSeries = true;
						} else {
							xySeries.add(qrdTS.getValue().getDouble(), qrd.getValue().getDouble(), false);
						}
						addedValues = true;
					}
				}
			}

			// Now add all new series to the chart
			if(newData != null) {
				// add new series in the range order
				for(String range : fRangesIDs) {
					Map<String, XYSeries> seriesForRange = newData.get(range);
					if(seriesForRange != null) {
						for(XYSeries xySeries : seriesForRange.values()) {
							addSeries(xySeries);
						}
					}
				}
			}

			// Repaint chart
			if (addedValues) {
			    this.fireDatasetChanged();
			}

			return addedSeries;
	    } catch (Exception e) {
	        // QueryNoSuchResult should not happen
	        logger.error("Error in inspectData", e);
	        return false;
	    }
	}

	/**
	 * @see org.jfree.data.xy.XYDataset#getYValue(int, int)
	 */
	public double getYValue(int series, int item) {
		// Will always return something, even if a series did not receive
		// its values yet
        double result = 0;
		XYSeries xySeries = getSeries(series);
		if (item < xySeries.getItemCount()) {
	        Number y = xySeries.getY(item);
	        if (y != null) {
	            result = y.doubleValue();
	        }
		}
		return result;
	}

    /**
     * RMSXYSeriesCollection can be used as a TableXYDataset (required for
     * area charts) provided that all series have the same number of items
     * (which is usually the case anyway).
     * @see org.jfree.data.TableXYDataset#getItemCount()
     */
    public int getItemCount() {
        if (this.getSeriesCount() < 1) {
            return 0;
        }

        // Return the item count of the first series
        return this.getItemCount(0);
    }

    /**
     * Sets the maximum item count for all series.
     * @param max
     */
    public void setMaximumItemCount(int max) {
		for(Iterator its = getSeries().iterator(); its.hasNext();) {
			XYSeries xySeries = (XYSeries)its.next();
			xySeries.setMaximumItemCount(max);
		}
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.charts.RMSDataset#reset()
	 */
	public void reset() {
		this.removeAllSeries();
	    this.fireDatasetChanged();
	}
}
