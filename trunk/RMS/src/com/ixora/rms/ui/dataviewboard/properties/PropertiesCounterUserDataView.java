/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.properties;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.dataengine.definitions.SingleCounterQueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.dataviewboard.properties.definitions.PropertiesDef;

/**
 * This is a data view info implementation used to plot a counter.
 * @author Daniel Moraru
 */
public class PropertiesCounterUserDataView extends PropertiesDef {
	/**
	 * Default constructor to support XML.
	 */
	public PropertiesCounterUserDataView() {
		super();
	}
	/**
	 * Constructor.
	 * @param counter
	 * @param name
	 * @param desc
	 * @param style
	 */
    public PropertiesCounterUserDataView(ResourceId counter, String name, String desc, Style style) {
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

	/**
	 * @see com.ixora.rms.repository.DataView#testDataView(com.ixora.rms.ResourceId)
	 */
    public void testDataView(ResourceId context) throws RMSException {
        // nothing to test, this is an existing counter
    }
}
