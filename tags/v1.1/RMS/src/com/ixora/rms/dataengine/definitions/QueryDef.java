/*
 * Created on 28-Nov-2004
 */
package com.ixora.rms.dataengine.definitions;

import java.util.List;

import org.w3c.dom.Node;

import com.ixora.rms.ResourceId;
import com.ixora.common.xml.XMLMultiTagList;
import com.ixora.common.xml.XMLSameTagList;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.XMLTagFactory;
import com.ixora.common.xml.XMLTagList;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * QueryDef
 * Contains definition for a query (data only, no functionality),
 * which is a list of functions or resources.
 * Loads and saves contents into XML.
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class QueryDef extends XMLTag {
	private static final long serialVersionUID = 2630667521805675404L;
	protected XMLTagList<ResourceDef> resources = new XMLSameTagList<ResourceDef>(ResourceDef.class);
    protected XMLTagList<ReactionDef> reactions = new XMLSameTagList<ReactionDef>(ReactionDef.class);
    @SuppressWarnings("serial")
	protected XMLTagList<FunctionDef> functions = new XMLMultiTagList<FunctionDef>(
            new XMLTagFactory<FunctionDef>() {
                public FunctionDef createFromXML(Node n) {
            		if (n.getNodeName().equals("function")) {
            		    if (XMLUtils.findChild(n, "code") != null)
            		    	return new ScriptFunctionDef();
            		    else if (XMLUtils.findChild(n, "value") != null)
            		    	return new ValueFilterDef();
            		    else
            		    	return new FunctionDef();
            		}
            		// not recognized by us, do default
            		return null;
                }
            });

    /** Query id */
    protected String id;

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public QueryDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param listResources list of resources
     * @param listFunctions list of function definitions
     */
    public QueryDef(List<ResourceDef> listResources, List<FunctionDef> listFunctions) {
    	this(listResources, listFunctions, null);
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param listResources list of resources
     * @param listFunctions list of function definitions
     */
    public QueryDef(List<ResourceDef> listResources, List<FunctionDef> listFunctions, List<ReactionDef> listReactions) {
    	super();
        resources.addAll(listResources);
    	if(listFunctions != null) {
    		functions.addAll(listFunctions);
    	}
    	if(listReactions != null) {
    		reactions.addAll(listReactions);
    	}
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param id unique identifier of this query
     * @param listResources list of resources
     * @param listFunctions list of function definitions
     */
    public QueryDef(String id, List<ResourceDef> listResources, List<FunctionDef> listFunctions) {
    	this(id, listResources, listFunctions, null);
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param id unique identifier of this query
     * @param listResources list of resources
     * @param listFunctions list of function definitions
     * @param listReactions
     */
    public QueryDef(String id,
    			List<ResourceDef> listResources,
    			List<FunctionDef> listFunctions,
    			List<ReactionDef> listReactions) {
    	super();
        this.id = id;
        resources.addAll(listResources);
        if(listFunctions != null) {
        	functions.addAll(listFunctions);
        }
        if(listReactions != null) {
        	reactions.addAll(listReactions);
        }
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param listResources list of resources
     */
    public QueryDef(List<ResourceDef> listResources) {
    	this(listResources, null);
    }

	/**
	 * @param rid
	 * @return true if this query is interested in the given resource
	 */
	public boolean isQueryInterestedInResource(ResourceId context, ResourceId rid) {
		for(Object r : resources) {
			ResourceId nrid = ((ResourceDef)r).getResourceId().complete(context);
			if(nrid.contains(rid)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return unique identifier of this query
	 */
	public String getIdentifier() {
		return id;
	}

	/**
	 * Sets the id for this query.
	 * @param id
	 */
	public void setIdentifier(String id) {
		this.id = id;
	}

    /**
     * @return hardcoded name of this tag
     */
	public String getTagName() {
        return "query";
    }

    /**
     * @return the list of functions defined by this query
     */
    public List<FunctionDef> getFunctions() {
    	return functions;
    }

    /**
     * @return a list of all resources used separately or as input params
     * for all functions of this query.
     */
	public List<ResourceDef> getResources() {
	    return resources;
	}

	/**
	 * @return a list of reactions
	 */
	public List<ReactionDef> getReactions() {
		return reactions;
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
		// assign unique ids to reactions
		int i = 0;
		for(Object r : reactions) {
			ReactionDef reaction = (ReactionDef)r;
			reaction.setIdentifier("r" + i);
			++i;
		}
	}


}
