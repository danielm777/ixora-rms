/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.definitions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLSameTagList;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.XMLTagList;
import com.ixora.rms.ui.dataviewboard.charts.ChartStyle;

/**
 * RendererDef
 * Contains definition for a chart renderer (data only, no functionality).
 * Loads and saves contents into XML.
 */
public class RendererDef extends XMLTag {
    protected XMLAttribute	type = new XMLAttributeString("type", true);
    protected DomainDef 	domain = new DomainDef();
    protected XMLTagList	ranges = new XMLSameTagList(RangeDef.class);

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public RendererDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param type type of this renderer (line, area, box etc)
     * @param domainID resource to be plotted on domain axis
     * @param rangeIDs resources to be plotted on range axis
     */
    public RendererDef(String type, String domainID, List<String> rangeIDs) {
        this.type.setValue(type);
        this.domain = new DomainDef(domainID);
        for (String s : rangeIDs) {
            ranges.add(new RangeDef(s));
        }
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param type type of this renderer (line, area, box etc)
     * @param domainID resource to be plotted on domain axis
     * @param rangeIDs resources to be plotted on range axis
     */
    public RendererDef(ChartStyle type, ResourceId domainID, List<ResourceId> rangeIDs) {
        this.type.setValue(type.getRenderer());
        this.domain = new DomainDef(domainID.toString());
        for (ResourceId rid : rangeIDs) {
            ranges.add(new RangeDef(rid.toString()));
        }
    }

    /**
     * @return the hardcoded name of this tag
     */
    public String getTagName() {
    	return "renderer";
    }

    /**
     * @return type of this renderer as string
     */
    public String getType() {
        return type.getValue();
    }

    /**
     * @return domain definition for this renderer
     */
    public DomainDef getDomain() {
        return domain;
    }

    /**
     * @return list of range definitions for this renderer
     */
    public List<RangeDef> getRanges() {
        return ranges;
    }

    /**
     * @return a list of IDs to be placed on the range axis
     */
    public List<String> getRangesIDList() {
    	List<String> listIDs = new LinkedList<String>();
    	for (Iterator it = ranges.iterator(); it.hasNext();) {
			RangeDef rd = (RangeDef) it.next();
    		listIDs.add(rd.getId());
    	}
    	return listIDs;
    }
}
