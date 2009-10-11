/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.properties;

import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.definitions.MultipleCounterQueryDef;
import com.ixora.rms.ui.dataviewboard.properties.definitions.PropertiesDef;

/**
 * This is a data view info implementation used to plot a set of counters.
 * @author Daniel Moraru
 */
public class PropertiesCounterSetDataView extends PropertiesDef {
	private static final long serialVersionUID = 7606968199399774082L;

	/**
	 * Default constructor to support XML.
	 */
	public PropertiesCounterSetDataView() {
		super();
	}
	/**
	 * Constructor.
	 * @param context
	 * @param counter
	 * @param name
	 * @param fDescription
	 */
    public PropertiesCounterSetDataView(ResourceId context, List<ResourceId> counters, String name) {
        super();
    	this.query = new MultipleCounterQueryDef(context,
                counters.toArray(new ResourceId[counters.size()]), name);
		this.name.setValue(name);
		this.description.setValue(name);
    }

	/**
	 * @see com.ixora.rms.repository.DataView#getSource()
	 */
	public int getSource() {
		return DATAVIEW_SOURCE_USER;
	}
}
