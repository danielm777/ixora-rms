/**
 * 21-Nov-2005
 */
package com.ixora.rms.tools;

import java.util.Collection;
import java.util.Map;

import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.RMS;
import com.ixora.rms.RMSComponent;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.Dashboard;
import com.ixora.rms.repository.DashboardMap;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewMap;
import com.ixora.rms.repository.ProviderInstance;
import com.ixora.rms.repository.ProviderInstanceMap;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;

/**
 * Checks the author of all artefacts (data views and dashboard). All of them should be system.
 * It must be run before each release.
 * @author Daniel Moraru
 */
public class CheckArtefactsAuthor {

	/**
	 * 
	 */
	private CheckArtefactsAuthor() {
		super();
	}

	public static void main(String[] args) {
		try {
			MessageRepository.initialize(RMSComponent.NAME);
			RMS.initializeRepositories();
			
			AgentRepositoryService agentRep = RMS.getAgentRepository();
			DataViewRepositoryService dataViewRep = RMS.getDataViewRepository();
			DashboardRepositoryService dashboardRep = RMS.getDashboardRepository();
			ProviderInstanceRepositoryService providerRep = RMS.getProviderInstanceRepository();
			
			Map<String, AgentInstallationData> agents = agentRep.getInstalledAgents();
			if(Utils.isEmptyMap(agents)) {
				System.err.println("No agents found");
				System.exit(1);
			}
			for(AgentInstallationData ad : agents.values()) {
				String aid = ad.getAgentInstallationId();
				// data views
				DataViewMap dataViews = dataViewRep.getAgentDataViews(aid);
				processDataViewMap(aid, dataViews);
				Map<EntityId, DataViewMap> entityDataViews = dataViewRep.getEntityDataViews(aid);				
				if(!Utils.isEmptyMap(entityDataViews)) {
					for(DataViewMap dvm : entityDataViews.values()) {
						processDataViewMap(aid, dvm);
					}
				}
				// dashboards
				DashboardMap dashboards = dashboardRep.getAgentDashboards(aid);
				processDashboardMap(aid, dashboards);
				Map<EntityId, DashboardMap> entityDashboards = dashboardRep.getEntityDashboards(aid);
				if(!Utils.isEmptyMap(entityDashboards)) {
					for(DashboardMap dbm : entityDashboards.values()) {
						processDashboardMap(aid, dbm);
					}
				}
				// provider instances
				ProviderInstanceMap providers = providerRep.getAgentProviderInstances(aid);
				processProviderInstanceMap(aid, providers);
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param aid
	 * @param providers
	 */
	private static void processProviderInstanceMap(String aid, ProviderInstanceMap providers) {
		if(providers == null) {
			return;
		}
		Collection<ProviderInstance> piList = providers.getAll();
		if(!Utils.isEmptyCollection(piList)) {
			for(ProviderInstance pi : piList) {
				if(!ProviderInstance.SYSTEM.equalsIgnoreCase(pi.getAuthor())) {
					System.err.println("Agent " + aid + ". Provider instance " + pi.getInstanceName() + " has author " + pi.getAuthor());
				}
			}		
		}
	}

	private static void processDataViewMap(String aid, DataViewMap dataViews) {
		if(dataViews == null) {
			return;
		}
		Collection<DataView> dataViewsList = dataViews.getAll();
		if(!Utils.isEmptyCollection(dataViewsList)) {
			for(DataView dv : dataViewsList) {
				if(!DataView.SYSTEM.equalsIgnoreCase(dv.getAuthor())) {
					System.err.println("Agent " + aid + ". Data view " + dv.getName() + " has author " + dv.getAuthor());
				}
			}		
		}
	}

	private static void processDashboardMap(String aid, DashboardMap dashboards) {
		if(dashboards == null) {
			return;
		}
		Collection<Dashboard> dashboardList = dashboards.getAll();
		if(!Utils.isEmptyCollection(dashboardList)) {
			for(Dashboard db : dashboardList) {
				if(!Dashboard.SYSTEM.equalsIgnoreCase(db.getAuthor())) {
					System.err.println("Agent " + aid + ". Dashboaord " + db.getName() + " has author " + db.getAuthor());
				}
			}		
		}
	}
}
