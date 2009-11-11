/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.properties;

import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.definitions.SingleCounterQueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.dataviewboard.properties.definitions.PropertiesDef;

/**
 * This is a data view info implementation used to plot a counter.
 * @author Daniel Moraru
 */
public class PropertiesCounterDataView extends PropertiesDef {
	private static final long serialVersionUID = -5489201560336953239L;

	/**
	 * Default constructor to support XML.
	 */
	public PropertiesCounterDataView() {
		super();
	}
	/**
	 * Constructor.
	 * @param counter
	 */
    public PropertiesCounterDataView(ResourceId counter) {
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

	/**
	 * @see com.ixora.rms.repository.DataView#testDataView(com.ixora.rms.ResourceId)
	 */
    public void testDataView(ResourceId context) throws RMSException {
        // nothing to test, this is an existing counter
    }
}
