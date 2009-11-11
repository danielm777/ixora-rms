/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.definitions;

/**
 * DomainDef
 * Contains definition for a domain axis of a chart renderer (data only,
 * no implementation). Loads and saves contents in XML.
 */
public class DomainDef extends AxisDef {
	private static final long serialVersionUID = -8481104423128245850L;

	/**
     * Constructs an empty object, ready to be loaded from XML.
     * This tag is always mandatory.
     */
    public DomainDef() {
        super(true);
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param id resource to be plotted on this axis
     */
    public DomainDef(String id) {
        super(id);
    }

    /**
     * @return hardcoded name of this tag
     */
	public String getTagName() {
        return "domain";
    }
}
