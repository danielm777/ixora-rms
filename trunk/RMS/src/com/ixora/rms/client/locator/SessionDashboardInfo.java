/*
 * Created on 26-Feb-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.client.model.DashboardInfo;
import com.ixora.rms.repository.Dashboard;

/**
 * @author Daniel Moraru
 */
public class SessionDashboardInfo extends AbstractSessionArtefactInfo {
	private Dashboard fDashboard;

	/**
	 * Constructor.
	 * @param vi
	 */
	public SessionDashboardInfo(DashboardInfo di) {
		super();
		fTranslatedName = di.getTranslatedName();
		fTranslatedDescription =  di.getTranslatedDescription();
		fDashboard = di.getDashboard();
	}

	/**
	 * @return the dashboard definition
	 */
	public Dashboard getDashboard() {
		return fDashboard;
	}
}
