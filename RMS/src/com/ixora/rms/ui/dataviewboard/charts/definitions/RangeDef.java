/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.definitions;

/**
 * RangeDef
 * Contains definition for a range axis of a chart renderer (data only,
 * no implementation). Loads and saves contents in XML.
 */
public class RangeDef extends AxisDef {

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public RangeDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param id resource to be plotted on this axis
     */
    public RangeDef(String id) {
        super(id);
    }

    /**
     * @return hardcoded name of this tag
     */
    public String getTagName() {
        return "range";
    }
}
