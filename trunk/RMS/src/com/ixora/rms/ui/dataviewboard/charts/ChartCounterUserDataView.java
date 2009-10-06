/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.dataengine.definitions.SingleCounterQueryDef;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;

/**
 * This is a data view info implementation used to plot counters.
 * @author Daniel Moraru
 */
public class ChartCounterUserDataView extends ChartDef {
	/**
	 * Default constructor to support XML.
	 */
	public ChartCounterUserDataView() {
		super();
	}
	/**
	 * Constructor.
	 * @param counter
	 * @param desc
	 * @param name
	 * @param style
	 */
    public ChartCounterUserDataView(ResourceId counter, String name, String desc, Style style) {
    	super();
        this.query = new SingleCounterQueryDef(counter, name, style);
		this.name.setValue(this.query.getIdentifier());
		if(Utils.isEmptyString(desc)) {
			this.description.setValue(this.name.getValue());
		} else {
			this.description.setValue(desc);
		}
    }

	/**
	 * @see com.ixora.rms.repository.DataView#getSource()
	 */
	public int getSource() {
		return DATAVIEW_SOURCE_USER;
	}
}
