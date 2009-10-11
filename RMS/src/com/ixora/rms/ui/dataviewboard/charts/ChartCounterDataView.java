/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.definitions.SingleCounterQueryDef;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;

/**
 * This is a data view info implementation used to plot counters.
 * @author Daniel Moraru
 */
public class ChartCounterDataView extends ChartDef {
	private static final long serialVersionUID = 6703190585338783469L;

	/**
	 * Default constructor to support XML.
	 */
	public ChartCounterDataView() {
		super();
	}
	/**
	 * Constructor.
	 * @param counter
	 * @param desc
	 * @param name
	 * @param style
	 */
    public ChartCounterDataView(ResourceId counter) {
    	super();
        this.query = new SingleCounterQueryDef(counter, null, null);
		this.name.setValue(this.query.getIdentifier());
		this.description.setValue(this.name.getValue());
    }

	/**
	 * @see com.ixora.rms.repository.DataView#getSource()
	 */
	public int getSource() {
		return DATAVIEW_SOURCE_COUNTER;
	}
}
