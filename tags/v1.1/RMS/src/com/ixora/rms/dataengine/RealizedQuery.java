package com.ixora.rms.dataengine;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.common.utils.Utils;
import com.ixora.rms.ResourceId;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.dataengine.definitions.FunctionDef;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.dataengine.definitions.ReactionDef;
import com.ixora.rms.dataengine.definitions.ResourceDef;
import com.ixora.rms.dataengine.functions.Function;
import com.ixora.rms.dataengine.reactions.Reaction;
import com.ixora.rms.exception.QueryNoSuchResultException;

/**
 * Realization of a Query, created based on a QueryDef.
 */
public class RealizedQuery implements Serializable {
	private static final long serialVersionUID = -213521183634344395L;
	private Map<String, Function> fFunctions;
	private Map<String, Resource> fResources;
	private List<Reaction> fReactions;

	/** This mirrors queryDef's identifier */
	private String fIdentifier;
	/** Artefact info locator */
	private SessionArtefactInfoLocator fInfoLocator;

	/**
	 * Identifier copied from query's definition
	 * @return unique identifier of the query.
	 */
	public String getIdentifier() {
		return fIdentifier;
	}

    /**
     * @param locator
     * @param queryDef
     * @param ridContext
     */
    public RealizedQuery(QueryDef queryDef, ResourceId ridContext) {
        this(null, queryDef, ridContext);
    }

    /**
     * @param locator
     * @param queryDef
     * @param ridContext
     */
	public RealizedQuery(SessionArtefactInfoLocator locator, QueryDef queryDef, ResourceId ridContext) {
		super();
		fFunctions = new LinkedHashMap<String, Function>();
		fResources = new LinkedHashMap<String, Resource>();
		this.fIdentifier = queryDef.getIdentifier();
		this.fInfoLocator = locator;
		for (ResourceDef resourceDef : queryDef.getResources()) {
		    Resource resource = new Resource(resourceDef, ridContext);
            resource.localize(locator);
			fResources.put(resource.getStyle().getID(), resource);
		}
		for (FunctionDef functionDef : queryDef.getFunctions()) {
			Function function = Function.createFunction(functionDef, 
					new LinkedList<Resource>(fResources.values()), ridContext);
			function.localize(locator);
            fFunctions.put(function.getStyle().getID(), function);
		}
		List<ReactionDef> rs = queryDef.getReactions();
		if(!Utils.isEmptyCollection(rs)) {
			fReactions = new LinkedList<Reaction>();
			for(ReactionDef reactionDef : rs) {
				fReactions.add(new Reaction(reactionDef, 
						new LinkedList<Resource>(fResources.values()), ridContext));
			}
		}
	}

	/**
	 * @return the contents of this query as a list of QueryResults
	 */
	public List<QueryResult> getQueryResults() {
		List<QueryResult> qrList = new LinkedList<QueryResult>();
		qrList.addAll(fResources.values());
		qrList.addAll(fFunctions.values());
		return qrList;
	}

	/**
	 * @return
	 */
	public List<Reaction> getReactions() {
		if(this.fReactions == null) {
			return null;
		}
		return Collections.unmodifiableList(this.fReactions);
	}

	/**
	 * @return a QueryResult by ID, or throws exception if not found
	 */
	public QueryResult getQueryResult(String id) throws QueryNoSuchResultException {
		Function f = fFunctions.get(id);
		if(f != null) {
			return f;
		}
		Resource r = fResources.get(id);
		if(r != null) {
			return r;
		}

		throw new QueryNoSuchResultException(id);
	}

	/**
	 * @return
	 */
	public List<Resource> getQuerySource() {
	    return new LinkedList<Resource>(fResources.values());
	}

	/**
	 * @return infoLocator.
	 */
	public SessionArtefactInfoLocator getSessionArtefactInfoLocator() {
		return fInfoLocator;
	}

	/**
	 * Localizes static data in this query's resources.
	 */
	public void localize() {
		if(fInfoLocator != null) {
			for(Resource resource : this.fResources.values()) {
	            resource.localize(this.fInfoLocator);
			}
			for(Function function : this.fFunctions.values()) {
	            function.localize(this.fInfoLocator);
			}
		}
	}
}
