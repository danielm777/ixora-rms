/*
 * Created on 30-Jul-2004
 */
package com.ixora.rms.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.ixora.common.collections.CaseInsensitiveLinkedMap;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.utils.Utils;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.repository.Dashboard;
import com.ixora.rms.repository.DataView;

/**
 * ArtefactInfoContainerImpl.
 * @author Daniel Moraru
 */
class ArtefactInfoContainerImpl implements ArtefactInfoContainer {
	/**
	 * Queries map.
	 */
	protected CaseInsensitiveLinkedMap<QueryInfoImpl> queries;
	/**
	 * Dashboards map.
	 */
	protected CaseInsensitiveLinkedMap<DashboardInfoImpl> dashboards;
	/**
	 * Data views map.
	 */
	protected CaseInsensitiveLinkedMap<DataViewInfoImpl> dataViews;
	/** Message repository */
	protected String messageRepository;
	/** Model */
	protected SessionModel model;
	/** True if in log replay model */
	protected boolean logReplayMode;

	/**
	 * Constructor.
	 * @param model
	 */
	protected ArtefactInfoContainerImpl(SessionModel model) {
		super();
		this.model = model;
		this.logReplayMode = model.isLogReplayMode();
	}

	/**
	 * @see com.ixora.rms.client.model.ArtefactInfoContainer#getQueryInfo(java.lang.String)
	 */
	public QueryInfo getQueryInfo(String queryName) {
		if(this.queries == null) {
			return null;
		}
		return queries.get(queryName);
	}

	/**
	 * @see com.ixora.rms.client.model.ArtefactInfoContainer#getQueryInfo()
	 */
	public Collection<QueryInfo> getQueryInfo() {
		if(this.queries == null) {
			return null;
		}
		// TODO generics revisit casting
		Collection<QueryInfo> ret = new ArrayList<QueryInfo>(this.queries.size());
		ret.addAll(queries.values());
		return Collections.unmodifiableCollection(ret);
	}

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDashboardInfo(java.lang.String)
     */
    public DashboardInfo getDashboardInfo(String queryGroupName) {
		if(this.dashboards == null) {
			return null;
		}
		return dashboards.get(queryGroupName);
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDashboardInfo()
     */
    public Collection<DashboardInfo> getDashboardInfo() {
		if(this.dashboards == null) {
			return null;
		}
		// TODO generics revisit casting
		Collection<DashboardInfo> ret = new ArrayList<DashboardInfo>(this.dashboards.size());
		ret.addAll(this.dashboards.values());
		return Collections.unmodifiableCollection(ret);
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#uncommittedVisibleQueries()
     */
    public boolean uncommittedVisibleQueries() {
    	Collection<QueryInfo> c = getQueryInfo();
    	if(c == null) {
    		return false;
    	}
    	for(QueryInfo qi : c) {
    		if(!qi.isCommitted()) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#uncommittedVisibleDashboards()
     */
    public boolean uncommittedVisibleDashboards() {
    	Collection<DashboardInfo> c = getDashboardInfo();
    	if(c == null) {
    		return false;
    	}
    	for(DashboardInfo qi : c) {
    		if(!qi.isCommitted()) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getQueriesToRealize()
     */
    public Collection<QueryInfo> getQueriesToRealize() {
        Collection<QueryInfo> qs = getQueryInfo();
        if(qs == null) {
            return null;
        }
        Collection<QueryInfo> ret = new LinkedList<QueryInfo>();
		for(QueryInfo qi : qs) {
			if(qi.getFlag(ArtefactInfo.ENABLED) && !qi.isCommitted()) {
				ret.add(qi);
			}
		}
		return ret;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getQueriesToUnRealize()
     */
    public Collection<QueryInfo> getQueriesToUnRealize() {
        Collection<QueryInfo> qs = getQueryInfo();
        if(qs == null) {
            return null;
        }
        Collection<QueryInfo> ret = new LinkedList<QueryInfo>();
		for(QueryInfo qi : qs) {
			if(!qi.getFlag(ArtefactInfo.ENABLED) && !qi.isCommitted()) {
				ret.add(qi);
			}
		}
		return ret;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDashboardsToRealize()
     */
    public Collection<DashboardInfo> getDashboardsToRealize() {
        Collection<DashboardInfo> gs = getDashboardInfo();
        if(gs == null) {
            return null;
        }
        Collection<DashboardInfo> ret = new LinkedList<DashboardInfo>();
		for(DashboardInfo gi : gs) {
			if(gi.getFlag(ArtefactInfo.ENABLED) && !gi.isCommitted()) {
				ret.add(gi);
			}
		}
		return ret;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDashboardsToUnRealize()
     */
    public Collection getDashboardsToUnRealize() {
        Collection<DashboardInfo> gs = getDashboardInfo();
        if(gs == null) {
            return null;
        }
        Collection<DashboardInfo> ret = new LinkedList<DashboardInfo>();
		for(DashboardInfo gi : gs) {
			if(!gi.getFlag(ArtefactInfo.ENABLED) && !gi.isCommitted()) {
				ret.add(gi);
			}
		}
		return ret;
    }

//  package
	/**
	 * Sets the queries for this entity.
	 * @param q
	 * @param logReplayMode
	 */
	void setQueries(QueryDef[] q, boolean logReplayMode) {
		this.queries = new CaseInsensitiveLinkedMap<QueryInfoImpl>();
		for(int i = 0; i < q.length; i++) {
			addQuery(q[i]);
		}
	}

	/**
	 * Adds a query.
	 * @param q
	 */
	void addQuery(QueryDef q) {
		// check if already in
		if(this.queries != null
				&& this.queries.get(q.getIdentifier()) != null) {
			return;
		}
	    QueryInfoImpl cinfo = new QueryInfoImpl(messageRepository, q, model);
	    if(logReplayMode) {
	        // query must show up enabled and committed
	        cinfo.setFlag(QueryInfo.ENABLED, true);
	        cinfo.commit();
	    }
	    if(this.queries == null) {
	        this.queries = new CaseInsensitiveLinkedMap<QueryInfoImpl>();
	    }
		this.queries.put(
				q.getIdentifier(), cinfo);
	}

	/**
	 * Removes a query.
	 * @param q
	 */
	void removeQuery(String name) {
		if(this.queries == null) {
			return;
		}
	    this.queries.remove(name);
	}

	/**
	 * Sets the dashboards for this entity.
	 * @param g
	 */
	void setDashboards(Dashboard[] gs) {
		this.dashboards = new CaseInsensitiveLinkedMap<DashboardInfoImpl>();
		DashboardInfoImpl ginfo;
		Dashboard g;
		for(int i = 0; i < gs.length; i++) {
			g = gs[i];
			ginfo = new DashboardInfoImpl(messageRepository, g, model);
			this.dashboards.put(
					g.getName(), ginfo);
			if(logReplayMode) {
		        // dashboard must show up enabled and committed
		        ginfo.setFlag(DashboardInfo.ENABLED, true);
		        ginfo.commit();
			}
		}
	}

	/**
	 * Sets the data views for this entity.
	 * @param g
	 */
	void setDataViews(DataView[] dvs) {
		this.dataViews = new CaseInsensitiveLinkedMap<DataViewInfoImpl>();
		DataViewInfoImpl dvinfo;
		DataView dv;
		for(int i = 0; i < dvs.length; i++) {
			dv = dvs[i];
			dvinfo = new DataViewInfoImpl(messageRepository, dv, model);
			this.dataViews.put(
					dv.getName(), dvinfo);
		}
	}

	/**
	 * Adds a dasboard.
	 * @param q
	 */
	void addDashboard(Dashboard g) {
	    DashboardInfoImpl ginfo = new DashboardInfoImpl(
	            messageRepository, g, model);
	    if(this.dashboards == null) {
	        this.dashboards = new CaseInsensitiveLinkedMap<DashboardInfoImpl>();
	    }
		this.dashboards.put(
				g.getName(), ginfo);
	}

	/**
	 * Adds a data view.
	 * @param d
	 */
	void addDataView(DataView d) {
	    DataViewInfoImpl dvinfo = new DataViewInfoImpl(
	            messageRepository, d, model);
	    if(this.dataViews == null) {
	        this.dataViews = new CaseInsensitiveLinkedMap<DataViewInfoImpl>();
	    }
		this.dataViews.put(
				d.getName(), dvinfo);
	}

	/**
	 * Removes a dashboard.
	 * @param name
	 */
	void removeDashboard(String name) {
		if(this.dashboards == null) {
			return;
		}
	    this.dashboards.remove(name);
	}

	/**
	 * Removes a dashboard.
	 * @param name
	 */
	void removeDataView(String name) {
		if(this.dataViews == null) {
			return;
		}
	    this.dataViews.remove(name);
	}

	/**
	 * @return
	 */
	DashboardInfoImpl getDashboardInfoImpl(String db) {
		if(this.dashboards == null) {
			return null;
		}
		return dashboards.get(db);
	}

    /**
     * @return
     */
    Collection<DashboardInfoImpl> getDashboardInfoImpl() {
		if(this.dashboards == null) {
			return null;
		}
		return Collections.unmodifiableCollection(dashboards.values());
    }

	/**
	 * @return
	 */
	DataViewInfoImpl getDataViewInfoImpl(String db) {
		if(this.dataViews == null) {
			return null;
		}
		return dataViews.get(db);
	}

	/**
	 * @return
	 */
	QueryInfoImpl getQueryInfoImpl(String queryName) {
		if(this.queries == null) {
			return null;
		}
		return queries.get(queryName);
	}

	/**
	 * @return
	 */
	Collection<QueryInfoImpl> getQueryInfoImpl() {
		if(this.queries == null) {
			return null;
		}
		return Collections.unmodifiableCollection(queries.values());
	}

	/**
	 * @param f
	 * @param queryName
	 * @param v
	 */
	void setQueryFlag(int f, String queryName, boolean v, boolean commit) {
		if(this.queries == null) {
			return;
		}
		QueryInfoImpl ci = queries.get(queryName);
		if(ci == null) {
			throw new AppRuntimeException("Query " + queryName + " not found in the model");
		}
		ci.setFlag(f, v);
		if(commit) {
			ci.commit();
		}
	}

	/**
	 * @param f
	 * @param name
	 * @param v
	 * @param commit
	 */
	void setDashboardFlag(int f, String name,
	        boolean v, boolean commit) {
		if(this.dashboards == null) {
			return;
		}
		DashboardInfoImpl di = dashboards.get(name);
		if(di == null) {
			throw new AppRuntimeException("Dashboard " + name + " not found in the model");
		}
		di.setFlag(f, v);
		if(commit) {
			di.commit();
		}
	}

	/**
	 * @param f
	 * @param name
	 * @param v
	 * @param commit
	 */
	void setDataViewFlag(int f, String name,
	        boolean v, boolean commit) {
		if(this.dataViews == null) {
			return;
		}
		DataViewInfoImpl dvi = dataViews.get(name);
		if(dvi == null) {
			throw new AppRuntimeException("Data view " + name + " not found in the model");
		}
		dvi.setFlag(f, v);
		if(commit) {
			dvi.commit();
		}
	}

	/**
	 * Rolls back all queries.
	 */
	void rollbackAllQueries() {
		for(QueryInfoImpl ci : queries.values()) {
			ci.rollback();
		}
	}

	/**
	 * Rolls back all dashboards.
	 */
	void rollbackAllDashboards() {
		for(DashboardInfoImpl gi : dashboards.values()) {
			gi.rollback();
		}
	}

	/**
	 * Rolls back all dashboards.
	 */
	void rollbackAllDataViews() {
		for(DataViewInfoImpl dvi : dataViews.values()) {
			dvi.rollback();
		}
	}

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDataViewsToRealize()
     */
    public Collection<DataViewInfo> getDataViewsToRealize() {
        Collection<DataViewInfo> dvis = getDataViewInfo();
        if(dvis == null) {
            return null;
        }
        Collection<DataViewInfo> ret = new LinkedList<DataViewInfo>();
		for(DataViewInfo dvi : dvis) {
			if(dvi.getFlag(ArtefactInfo.ENABLED) && !dvi.isCommitted()) {
				ret.add(dvi);
			}
		}
		return ret;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDataViewsToUnRealize()
     */
    public Collection<DataViewInfo> getDataViewsToUnRealize() {
        Collection<DataViewInfo> dvis = getDataViewInfo();
        if(dvis == null) {
            return null;
        }
        Collection<DataViewInfo> ret = new LinkedList<DataViewInfo>();
		for(DataViewInfo dvi : dvis) {
			if(!dvi.getFlag(ArtefactInfo.ENABLED) && !dvi.isCommitted()) {
				ret.add(dvi);
			}
		}
		return ret;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDataViewInfo(java.lang.String)
     */
    public DataViewInfo getDataViewInfo(String db) {
		if(this.dataViews == null) {
			return null;
		}
		return dataViews.get(db);
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#getDataViewInfo()
     */
    public Collection<DataViewInfo> getDataViewInfo() {
		if(this.dataViews == null) {
			return null;
		}
		// TODO generics revisit casting
		Collection<DataViewInfo> ret = new ArrayList<DataViewInfo>(dataViews.size());
		ret.addAll(dataViews.values());
		return Collections.unmodifiableCollection(ret);
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#uncommittedVisibleDataViews()
     */
    public boolean uncommittedVisibleDataViews() {
    	Collection<DataViewInfo> dvis = getDataViewInfo();
    	if(dvis == null) {
    		return false;
    	}
    	for(DataViewInfo dvi : dvis) {
    		if(!dvi.isCommitted()) {
    			return true;
    		}
    	}
    	return false;
    }

	/**
	 * @see com.ixora.rms.client.model.ArtefactInfoContainer#hasDataViews()
	 */
	public boolean hasDataViews() {
		return !Utils.isEmptyMap(this.dataViews);
	}

	/**
	 * @see com.ixora.rms.client.model.ArtefactInfoContainer#hasDashboards()
	 */
	public boolean hasDashboards() {
		return !Utils.isEmptyMap(this.dashboards);
	}

	/**
	 * @see com.ixora.rms.client.model.ArtefactInfoContainer#hasQueries()
	 */
	public boolean hasQueries() {
		return !Utils.isEmptyMap(this.queries);
	}

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoContainer#showIdentifiers()
     */
    public boolean showIdentifiers() {
        return model.getShowIdentifiers();
    }

	/**
	 * @see com.ixora.rms.client.model.ArtefactInfoContainer#hasDataViewsWithReactions()
	 */
	public boolean hasDataViewsWithReactions() {
		if(Utils.isEmptyMap(this.dataViews)) {
			return false;
		}
		for(DataViewInfo dvi : dataViews.values()) {
			if(dvi.hasReactions()) {
				return true;
			}
		}
		return false;
	}
}

