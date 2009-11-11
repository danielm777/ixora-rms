/*
 * Created on 28-Jul-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.repository.Dashboard;
import com.ixora.rms.repository.VersionableAgentArtefact;

/**
 * Class that holds model data on a dashboard.
 */
public final class DashboardInfoImpl extends ArtefactInfoImpl
			implements DashboardInfo {
	/** Dashboard */
	private Dashboard dashboard;

	/**
	 * @param messageRepository used to locate the translated
	 * name and description of the query
	 * @param db the dashboard
	 * @param model
	 */
	public DashboardInfoImpl(String messageRepository,
	        Dashboard db, SessionModel model) {
	    super(messageRepository,
	            db.getName(),
	            db.getDescription(), model);
		this.dashboard = db;
	}

    /**
     * @see com.ixora.rms.client.model.DashboardInfo#getDashboard()
     */
    public Dashboard getDashboard() {
        return this.dashboard;
    }

    /**
     * @see com.ixora.rms.client.model.ArtefactInfoImpl#getVersionableAgentArtefact()
     */
    public VersionableAgentArtefact getVersionableAgentArtefact() {
        return this.dashboard;
    }
}