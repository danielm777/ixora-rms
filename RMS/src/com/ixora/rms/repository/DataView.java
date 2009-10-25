/*
 * Created on 12-Oct-2004
 */
package com.ixora.rms.repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLText;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.RealizedQuery;
import com.ixora.rms.dataengine.QueryClientAbstract;
import com.ixora.rms.dataengine.definitions.FunctionDef;
import com.ixora.rms.dataengine.definitions.ParamDef;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.dataengine.definitions.ResourceDef;
import com.ixora.rms.dataengine.reactions.Reaction;
import com.ixora.rms.exception.QueryDuplicateIDException;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public abstract class DataView extends QueryClientAbstract implements VersionableAgentArtefact, AuthoredArtefact {
	private static final long serialVersionUID = -250994584462884655L;
	//	 source for the data view
	/** The data view comes from repository */
	public static final int DATAVIEW_SOURCE_REPOSITORY = 0;
	/** The data view was designed by user on the fly */
	public static final int DATAVIEW_SOURCE_USER = 1;
	/** The data view was derived from a counter on the fly */
	public static final int DATAVIEW_SOURCE_COUNTER = 2;

    /** Delegate that implements VersionableAgentArtefact */
    protected VersionableAgentArtefactAbstract versionableArtefact;
    /** Author of the data view */
    protected XMLText author = new XMLText("author", false);

    /**
     * Constructor.
     */
    @SuppressWarnings("serial")
	protected DataView() {
        super();
        versionableArtefact = new VersionableAgentArtefactAbstract() {
            /**
             * @see com.ixora.rms.repository.VersionableAgentArtefact#getArtefactName()
             */
            public String getArtefactName() {
                return name.getValue();
            }
        };
    }

    /**
     * @param viewname views's name
     * @param description views's description
     * @param query views's query definition
     * @param isCustom
     */
    @SuppressWarnings("serial")
	protected DataView(String viewname, String description, QueryDef query, String author) {
        super();
        this.name.setValue(viewname);
        this.description.setValue(description);
        this.author.setValue(author);
        this.query = query;
        this.versionableArtefact = new VersionableAgentArtefactAbstract() {
            /**
             * @see com.ixora.rms.repository.VersionableAgentArtefact#getArtefactName()
             */
            public String getArtefactName() {
                return name.getValue();
            }
        };
    }

	/**
	 * @see com.ixora.rms.client.model.DataViewInfo#getSource()
	 */
	public int getSource() {
		return DATAVIEW_SOURCE_REPOSITORY;
	}

    /**
     * @return the class name of the board to host
     * the view
     */
    public abstract String getBoardClass();

   /**
     * Attempts to instantiate/realize this dataview, and throws
     * the appropriate exceptions if it fails.
     */
	public void testDataView(ResourceId context) throws RMSException {
        // Make sure that the cube can be realized (query definition ok)
	    RealizedQuery realizedCube = new RealizedQuery(getQueryDef(), context);

	    // Make sure all IDs are unique
	    Set<String>	setIDs = new HashSet<String>();
	    for (ResourceDef resource : getQueryDef().getResources()) {
	    	String idRes = resource.getID();
	    	if (setIDs.contains(idRes)) {
	    		throw new QueryDuplicateIDException(idRes);
	    	}
	    	setIDs.add(idRes);
	    }


	    // Make sure parameters for all functions refer to valid IDs
	    for (FunctionDef function : getQueryDef().getFunctions()) {
	        for (ParamDef param : function.getParameters()) {
	            realizedCube.getQueryResult(param.getID());
	        }
	    	String idRes = function.getID();
	    	if (setIDs.contains(idRes)) {
	    		throw new QueryDuplicateIDException(idRes);
	    	}
	    	setIDs.add(idRes);
	    }

	    // Make sure the reactions refer to existing resources
	    List<Reaction> reactions = realizedCube.getReactions();
	    if(!Utils.isEmptyCollection(reactions)) {
	    	for(Reaction reaction : reactions) {
	    		reaction.test(setIDs);
	    	}
	    }
	}

// VersionableAgentArtefact methods
    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#getAgentVersions()
     */
    public Set<String> getAgentVersions() {
        return versionableArtefact.getAgentVersions();
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#addAgentVersions(java.util.Collection)
     */
    public void addAgentVersions(Collection<String> versions) {
        this.versionableArtefact.addAgentVersions(versions);
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#removeAgentVersions(java.util.Collection)
     */
    public void removeAgentVersions(Collection<String> versions) {
        this.versionableArtefact.removeAgentVersions(versions);
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#setAgentVersions(java.util.Collection)
     */
    public void setAgentVersions(Collection<String> versions) {
        this.versionableArtefact.setAgentVersions(versions);
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#appliesToAgentVersion(java.lang.String)
     */
    public boolean appliesToAgentVersion(String agentVersion) {
        return this.versionableArtefact.appliesToAgentVersion(agentVersion);
    }


    /**
	 * @see com.ixora.rms.dataengine.QueryClientAbstract#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
//		if(Utils.isEmptyString(author.getValue())) {
//			author.setValue(CUSTOMER);
//		}
	}

	/**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#getArtefactName()
     */
    public String getArtefactName() {
        return this.versionableArtefact.getArtefactName();
    }

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#getAuthor()
	 */
	public String getAuthor() {
		return author.getValue();
	}

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#setAuthor(java.lang.String)
	 */
	public void setAuthor(String author) {
		this.author.setValue(author);
	}

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#isSystem()
	 */
	public boolean isSystem() {
		return Utils.isEmptyString(author.getValue()) || SYSTEM.equalsIgnoreCase(author.getValue());
	}
}
