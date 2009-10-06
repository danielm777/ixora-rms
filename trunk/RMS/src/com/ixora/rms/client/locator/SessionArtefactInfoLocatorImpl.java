/*
 * Created on 26-Feb-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.model.DashboardInfo;
import com.ixora.rms.client.model.DataViewInfo;
import com.ixora.rms.client.model.ResourceInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.DataViewMap;
import com.ixora.rms.services.DataViewRepositoryService;

/**
 * @author Daniel Moraru
 */
public class SessionArtefactInfoLocatorImpl implements
		SessionArtefactInfoLocator {
	private SessionModel fModel;
	private DataViewRepositoryService fViewRepository;

	/**
	 * Constructor.
	 * @param model
	 * @param viewRepository
	 */
	public SessionArtefactInfoLocatorImpl(SessionModel model,
			DataViewRepositoryService viewRepository) {
		super();
		fModel = model;
		fViewRepository = viewRepository;
	}

	/**
	 * @see com.ixora.rms.client.locator.SessionArtefactInfoLocator#getDataViewInfo(com.ixora.rms.repository.DataViewId)
	 */
	public SessionDataViewInfo getDataViewInfo(DataViewId vid) {
		// try session model first
		DataViewInfo vi = fModel.getDataViewInfo(vid, true);
		if(vi != null) {
			return new SessionDataViewInfo(vi);
		} else {
			// load it from repository
			DataView view = null;
			ResourceId context = vid.getContext();
            String suoVersion = fModel.getAgentVersionInContext(context);
			if(context == null) {
                DataViewMap views = fViewRepository.getGlobalDataViews();
                if(views == null) {
                    return null;
                }
				view = views.getForAgentVersion(vid.getName(), suoVersion);
				if(view == null) {
					return null;
				}
				return new SessionDataViewInfo(
						view,
						MessageRepository.get(view.getName()),
						MessageRepository.get(view.getDescription()));
			} else if(context.getRepresentation() == ResourceId.HOST) {
                DataViewMap views = fViewRepository.getHostDataViews(context.getHostId().toString());
                if(views == null) {
                    return null;
                }
				view = views.getForAgentVersion(vid.getName(), suoVersion);
				if(view == null) {
					return null;
				}
				return new SessionDataViewInfo(view,
						MessageRepository.get(view.getName()),
						MessageRepository.get(view.getDescription()));
			} else if(context.getRepresentation() == ResourceId.AGENT) {
				String agentInstallationId = context.getAgentId().getInstallationId();
				DataViewMap views = fViewRepository.getAgentDataViews(agentInstallationId);
                if(views == null) {
                    return null;
                }
                view = views.getForAgentVersion(vid.getName(), suoVersion);
				if(view == null) {
					return null;
				}
				return new SessionDataViewInfo(view,
					MessageRepository.get(agentInstallationId, view.getName()),
					MessageRepository.get(agentInstallationId, view.getDescription()));
			} else if(context.getRepresentation() == ResourceId.ENTITY) {
				String agentInstallationId = context.getAgentId().getInstallationId();
				DataViewMap views = fViewRepository.getEntityDataViews(
                        agentInstallationId, context.getEntityId());
                if(views == null) {
                    return null;
                }
                view = views.getForAgentVersion(vid.getName(), suoVersion);
				if(view == null) {
					return null;
				}
				return new SessionDataViewInfo(view,
					MessageRepository.get(agentInstallationId, view.getName()),
					MessageRepository.get(agentInstallationId, view.getDescription()));
			}
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.client.locator.SessionArtefactInfoLocator#getDashboardInfo(com.ixora.rms.repository.DashboardId)
	 */
	public SessionDashboardInfo getDashboardInfo(DashboardId did) {
		// try session model first
		DashboardInfo di = fModel.getDashboardInfo(did, true);
		if(di != null) {
			return new SessionDashboardInfo(di);
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.client.locator.SessionArtefactInfoLocator#getResourceInfo(com.ixora.rms.ResourceId)
	 */
	public SessionResourceInfo getResourceInfo(ResourceId r) {
		// try session model first
		ResourceInfo[] cis = fModel.getResourceInfo(r, true);
		if(Utils.isEmptyArray(cis)) {
			// TODO must try the record definition cache
			// in the data engine
			return null;
		}
		return new SessionResourceInfo(cis[0]);
	}
}
