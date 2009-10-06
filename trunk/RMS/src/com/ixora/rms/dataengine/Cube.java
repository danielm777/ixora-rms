package com.ixora.rms.dataengine;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.dataengine.definitions.FunctionDef;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.dataengine.definitions.ReactionDef;
import com.ixora.rms.dataengine.definitions.ResourceDef;
import com.ixora.rms.dataengine.functions.Function;
import com.ixora.rms.dataengine.reactions.Reaction;
import com.ixora.rms.exception.QueryNoSuchResultException;

/**
 * Cube
 * Implementation for a Query, created based on a QueryDef.
 */
public class Cube implements Serializable {
	private List<Function> fFunctions;
	private List<Resource> fResources;
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
    public Cube(QueryDef queryDef, ResourceId ridContext) {
        this(null, queryDef, ridContext);
    }

    /**
     * @param locator
     * @param queryDef
     * @param ridContext
     */
	public Cube(SessionArtefactInfoLocator locator, QueryDef queryDef, ResourceId ridContext) {
		super();
		fFunctions = new LinkedList<Function>();
		fResources = new LinkedList<Resource>();
		this.fIdentifier = queryDef.getIdentifier();
		this.fInfoLocator = locator;
		for (ResourceDef resourceDef : queryDef.getResources()) {
		    Resource resource = new Resource(resourceDef, ridContext);
            resource.localize(locator);
			fResources.add(resource);
		}
		for (FunctionDef functionDef : queryDef.getFunctions()) {
			Function function = Function.createFunction(functionDef, fResources, ridContext);
			function.localize(locator);
            fFunctions.add(function);
		}
		List<ReactionDef> rs = queryDef.getReactions();
		if(!Utils.isEmptyCollection(rs)) {
			fReactions = new LinkedList<Reaction>();
			for(ReactionDef reactionDef : rs) {
				fReactions.add(new Reaction(reactionDef, fResources, ridContext));
			}
		}
	}

//	/**
//	 * NOTE: fuctions don't implement getDef() properly yet so this
//	 * method MUST not be invoked.
//	 * Builds and returns a definition for the underlying query.
//	 * @return a QueryDef for this cube
//	 */
//	public QueryDef getQueryDef() {
//		// build the definition
//		// build resource defs
//		List<ResourceDef> res = new ArrayList<ResourceDef>(this.resources.size());
//		for(Resource r : this.resources) {
//			res.add(r.getResourceDef());
//		}
//
//		// build function defs
//		List<FunctionDef> fun = new ArrayList<FunctionDef>(this.functions.size());
//		for(Function f : this.functions) {
//			fun.add(f.getFunctionDef());
//		}
//
//		return new QueryDef(getIdentifier(), res, fun);
//	}

	/**
	 * @return the contents of this query as a list of QueryResults
	 */
	public List<QueryResult> getQueryResults() {
		List<QueryResult> qrList = new LinkedList<QueryResult>();
		qrList.addAll(fResources);
		qrList.addAll(fFunctions);
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
		for (Function f : fFunctions) {
			if (f.getStyle().getID().equals(id)) {
				return f;
			}
		}
		for (Resource r : fResources) {
		    if (r.getStyle().getID().equals(id)) {
		        return r;
		    }
		}

		throw new QueryNoSuchResultException(id);
	}

	/**
	 * @return
	 */
	public List<Resource> getQuerySource() {
	    return fResources;
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
			for(Resource resource : this.fResources) {
	            resource.localize(this.fInfoLocator);
			}
			for(Function function : this.fFunctions) {
	            function.localize(this.fInfoLocator);
			}
		}
	}
}
