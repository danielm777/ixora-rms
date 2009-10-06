/*
 * Created on 07-Mar-2005
 */
package com.ixora.rms.ui.dataviewboard.properties.definitions;

import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.ui.dataviewboard.properties.PropertiesBoard;

/**
 * @author Daniel Moraru
 */
public class PropertiesDef extends DataView {

	/**
	 * Constructor.
	 */
	public PropertiesDef() {
		super();
	}

    /**
     * @param name
     * @param description
     * @param query
     */
    public PropertiesDef(String name, String description, QueryDef query, String author) {
        super(name, description, query, author);
    }

    /**
	 * @see com.ixora.rms.repository.DataView#getBoardClass()
	 */
	public String getBoardClass() {
		return PropertiesBoard.class.getName();
	}

    /**
     * Throws the appropriate exception if a problem occurs while trying
     * to initialize a control from the given DataView. Used to test
     * the validity of a custom-defined view.
	 * @see com.ixora.rms.repository.DataView#testDataView(com.ixora.rms.ResourceId)
     */
    public void testDataView(ResourceId context) throws RMSException {
    	super.testDataView(context);
    }
}
