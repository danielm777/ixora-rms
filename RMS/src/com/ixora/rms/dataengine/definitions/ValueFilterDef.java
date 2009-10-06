/*
 * Created on 26-Mar-2005
 */
package com.ixora.rms.dataengine.definitions;

import java.util.List;

import com.ixora.common.xml.XMLSameTagList;
import com.ixora.common.xml.XMLTagList;

/**
 * ValueFilterDef
 * Definition for a function which can filter values returned by a
 * resource of a query.
 * Loads and saves contents into XML.
 */
public class ValueFilterDef extends FunctionDef {
    private XMLTagList	rules = new XMLSameTagList(ValueFilterRuleDef.class);

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public ValueFilterDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param listParamDefs list of input parameters
     * @param listRules list of filtering rules
     */
    public ValueFilterDef(List<ParamDef> listParamDefs,
						  List<ValueFilterRuleDef> listRules) {
    	super("filter", listParamDefs);
    	this.rules.addAll(listRules);
    }

    /**
     * @return the list of rules defined in this filter
     */
    public List<ValueFilterRuleDef> getRules() {
    	return rules;
    }

}
