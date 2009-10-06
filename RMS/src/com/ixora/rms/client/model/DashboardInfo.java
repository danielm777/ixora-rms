/*
 * Created on 08-Aug-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.repository.Dashboard;

/**
 * @author Daniel Moraru
 */
public interface DashboardInfo extends ArtefactInfo {
    /**
     * @return the dashboard
     */
    Dashboard getDashboard();
}
