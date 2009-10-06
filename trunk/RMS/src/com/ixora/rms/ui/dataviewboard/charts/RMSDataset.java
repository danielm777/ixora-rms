/*
 * Created on Aug 20, 2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.io.Serializable;

import com.ixora.rms.dataengine.external.QueryData;

/**
 * RMSDataset
 */

public interface RMSDataset extends Serializable {
	/**
	 * @param data
	 * @return true if new data was detected
	 */
	public boolean inspectData(QueryData data);
	/**
	 * Reset data model.
	 */
	public void reset();
}
