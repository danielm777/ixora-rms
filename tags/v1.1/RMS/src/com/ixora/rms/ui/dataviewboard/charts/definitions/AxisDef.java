/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.definitions;

import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLTag;

/**
 * AxisDef
 * Base class for both DomainDef and RangeDef.
 */
public abstract class AxisDef extends XMLTag {
	private static final long serialVersionUID = 8381888624713067L;
	protected XMLAttribute	id = new XMLAttributeString("id", true);

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public AxisDef() {
        super();
    }

    /**
     * Constructs an empty object, ready to be loaded from XML
     * @param mandatory whether this tag is mandatory
     */
    public AxisDef(boolean mandatory) {
        super(mandatory);
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param id resource to be plotted on this axis
     */
    public AxisDef(String id) {
        this.id.setValue(id);
    }

    /**
     * @return id resource to be plotted on this axis
     */
    public String getId() {
        return id.getValue();
    }
}
