/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.client.model;

import java.util.Collection;
import java.util.Iterator;

import com.ixora.rms.ResourceId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.repository.DataView;

/**
 * @author Daniel Moraru
 */
final class DataViewModelHelperImpl implements DataViewModelHelper {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataViewModelHelperImpl.class);

    /** Session model */
    private SessionModel model;

    /**
     * Constructor.
     * @param model
     */
    public DataViewModelHelperImpl(SessionModel model) {
        super();
        this.model = model;
    }

    /**
     * @see com.ixora.rms.client.model.DataViewModelHelper#rollbackDataViews(com.ixora.rms.internal.ResourceId)
     */
    public void rollbackDataViews(ResourceId context) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(context, true);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find artefact container for: " + context);
			}
			return;
		}
		Collection dvs = ac.getDataViewInfo();
		if(dvs == null) {
			return;
		}
		DataViewInfo dvi;
		for(Iterator iter = dvs.iterator(); iter.hasNext();) {
            dvi = (DataViewInfo)iter.next();
            model.getQueryHelper().rollbackQuery(context,
                    dvi.getDataView().getQueryDef().getIdentifier());
        }
		// rollback data views
		ac.rollbackAllDataViews();
        // refresh context node
		model.refreshNode(context);
	}

    /**
     * @see com.ixora.rms.client.model.DataViewModelHelper#setDataViewFlag(int, com.ixora.rms.internal.ResourceId, java.lang.String, boolean, boolean)
     */
    public void setDataViewFlag(int flag, ResourceId context, String dv,
            boolean value, boolean commit) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(context, true);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find artefact container for: " + context);
			}
			return;
		}
		DataViewInfo dinfo = ac.getDataViewInfo(dv);
		if(dinfo == null) {
			return;
		}
		// set flag for query
        model.getQueryHelper().setQueryFlag(flag,
                context, dinfo.getDataView().getQueryDef().getIdentifier(), value, commit,
                true); // update state of counters
        // now set flag for data view
		ac.setDataViewFlag(flag, dv, value, commit);
        // refresh context node
		model.refreshNode(context);
    }

    /**
     * @see com.ixora.rms.client.model.DataViewModelHelper#setDataViews(com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.DataView[])
     */
    public void setDataViews(ResourceId id, DataView[] views) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(id, true);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find artefact container for: " + id);
			}
			return;
		}
		ac.setDataViews(views);
		model.refreshNode(id);
    }

    /**
     * @see com.ixora.rms.client.model.DataViewModelHelper#addDataView(com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.DataView)
     */
    public void addDataView(ResourceId id, DataView dv) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(id, true);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find artefact container for: " + id);
			}
			return;
		}
		// check if we already have a data view with the same name,
		// if so remove it's query first...
		DataViewInfo old = ac.getDataViewInfo(dv.getName());
		if(old != null) {
			model.getQueryHelper().removeQuery(id, old.getDataView().getQueryDef().getIdentifier());
		}
		ac.addDataView(dv);
		// register view's query
		model.getQueryHelper().addQuery(id, dv.getQueryDef());
		model.refreshNode(id);
    }

    /**
     * @see com.ixora.rms.client.model.DataViewModelHelper#removeDataView(com.ixora.rms.internal.ResourceId, java.lang.String)
     */
    public void removeDataView(ResourceId id, String dv) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(id, true);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find artefact container for: " + id);
			}
			return;
		}

		DataViewInfo dvi = ac.getDataViewInfo(dv);
		if(dvi != null) {
			ac.removeDataView(dv);
			// remove view's query
			model.getQueryHelper().removeQuery(id, dvi.getDataView().getQueryDef().getIdentifier());
			model.refreshNode(id);
		}
    }

    /**
     * @see com.ixora.rms.client.model.DataViewModelHelper#isDataViewReady(com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.DataView)
     */
    public boolean isDataViewReady(ResourceId context, DataView dv) {
		ArtefactInfoContainer ac = model.getArtefactContainerForResource(context, true);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find artefact container for: " + context);
			}
			return false;
		}
		String qName = dv.getQueryDef().getIdentifier();
		QueryInfo qi = ac.getQueryInfo(qName);
		if(qi == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find query info for name: "
		            + qName + " Context: " + context);
			}
			return false;
		}
		if(!qi.getFlag(QueryInfo.ENABLED)
		        || !qi.isCommitted()) {
        	return false;
		}
        return true;
    }

    /**
     * @see com.ixora.rms.client.model.DataViewModelHelper#rollbackDataView(com.ixora.rms.internal.ResourceId, java.lang.String)
     */
    public void rollbackDataView(ResourceId context, String name) {
		ArtefactInfoContainerImpl ac = model.getArtefactContainerImplForResource(context, true);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find artefact container for: " + context);
			}
			return;
		}
		DataViewInfoImpl dvi = ac.getDataViewInfoImpl(name);
		if(dvi == null) {
			return;
		}
        // rollback query
		model.getQueryHelper().rollbackQuery(context,
                    dvi.getDataView().getQueryDef().getIdentifier());
		// rollback data view
        dvi.rollback();
        // refresh context node
		model.refreshNode(context);
    }

    /**
     * Note: this method uses a non-aggressive search for performance reasons.
     * @see com.ixora.rms.client.model.DataViewModelHelper#recalculateDataViewsStatus(com.ixora.rms.internal.ResourceId)
     */
    public void recalculateDataViewsStatus(ResourceId context) {
        ArtefactInfoContainer ac = model.getArtefactContainerImplForResource(context, false);
		if(ac == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find container for: " + context);
			}
			return;
		}
		Collection cs = ac.getDataViewInfo();
		if(cs == null) {
			return;
		}
		for(Iterator iter = cs.iterator(); iter.hasNext();) {
			DataViewInfoImpl dvinfo = (DataViewInfoImpl)iter.next();
			DataView dv = dvinfo.getDataView();
			QueryInfo qinfo = ac.getQueryInfo(dv.getQueryDef().getIdentifier());
			dvinfo.setFlag(DataViewInfo.ENABLED,
                        qinfo.getFlag(QueryInfo.ENABLED));
            dvinfo.setFlag(DataViewInfo.ACTIVATED,
                    qinfo.getFlag(QueryInfo.ACTIVATED));
            if(qinfo.isCommitted()) {
                dvinfo.commit();
            }
		}
    }
}
