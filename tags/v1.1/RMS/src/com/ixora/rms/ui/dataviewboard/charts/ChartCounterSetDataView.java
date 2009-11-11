/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.dataengine.definitions.MultipleCounterQueryDef;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;
import com.ixora.rms.ui.dataviewboard.charts.definitions.RendererDef;

/**
 * This is a data view info implementation used to plot a set of counters.
 * @author Daniel Moraru
 */
public class ChartCounterSetDataView extends ChartDef {
	private static final long serialVersionUID = 6848551625408739176L;

	/**
	 * Default constructor to support XML.
	 */
	public ChartCounterSetDataView() {
		super();
	}
	/**
	 * Constructor.
	 * @param context
	 * @param counters
	 * @param name
	 * @param fDescription
	 */
    public ChartCounterSetDataView(ResourceId context, Set<ResourceId> counters, String name) {
        super();
        if(Utils.isEmptyCollection(counters)) {
            throw new IllegalArgumentException("The list of counters is empty");
        }
        this.query = new MultipleCounterQueryDef(
        		 context,
                counters.toArray(new ResourceId[counters.size()]), name);
		this.name.setValue(name);
		this.description.setValue(name);
        // specfiy a time series renderer
        List<ResourceId> ranges = new LinkedList<ResourceId>(counters);
        RendererDef renderer = new RendererDef(
                ChartStyle.XY_LINE,
                ((MultipleCounterQueryDef)this.query).getTimestamp(),
                ranges);
        this.renderers.add(renderer);
    }

	/**
	 * @see com.ixora.rms.repository.DataView#getSource()
	 */
	public int getSource() {
		return DATAVIEW_SOURCE_USER;
	}
}
