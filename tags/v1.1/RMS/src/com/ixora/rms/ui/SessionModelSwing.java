/*
 * Created on 24-Jul-2004
 */
package com.ixora.rms.ui;

import java.rmi.RemoteException;

import javax.swing.SwingUtilities;

import com.ixora.rms.HostInformation;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.ServiceState;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;

/**
 * This version of the session model ensures that all the
 * methods that might trigger painting events are executed
 * on the event dispatch thread.
 * @author Daniel Moraru
 */
public final class SessionModelSwing extends SessionModel {
	private static final long serialVersionUID = 8210929782330925936L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(SessionModelSwing.class);

	/**
	 * Constructor used by a live session.
	 * @param mss
	 * @param hms
	 */
	public SessionModelSwing(
	        MonitoringSessionService mss,
			HostMonitorService hms,
			DashboardRepositoryService qgrs,
			DataViewRepositoryService dvrs,
			ProviderInstanceRepositoryService pirs) {
		super(mss, hms, qgrs, dvrs, pirs);
	}
	/**
     * Constructor used by the log replay view.
     * @param rmsAgentRepository
     * @param rmsDashboardRepository
     * @param rmsDataViewRepository
     */
    public SessionModelSwing(
            AgentRepositoryService rmsAgentRepository,
            DashboardRepositoryService rmsDashboardRepository,
            DataViewRepositoryService rmsDataViewRepository) {
        super(rmsAgentRepository,
                rmsDashboardRepository, rmsDataViewRepository);
    }
    /**
	 * @see com.ixora.rms.client.model.SessionModel#addAgent(java.lang.String, com.ixora.rms.control.struct.AgentInstallationInfo, com.ixora.rms.control.struct.AgentDeploymentInfo)
	 */
	public void addAgent(final String host,
			final AgentInstallationData aid,
			final AgentInstanceData ad) {
		if(SwingUtilities.isEventDispatchThread()) {
			super.addAgent(host, aid, ad);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					addAgentSuper(host, aid, ad);
				}
				});
		}
	}

	/**
	 * @see com.ixora.rms.client.model.SessionModel#addEntity(java.lang.String, java.lang.String, com.ixora.rms.EntityDescriptor)
	 */
	public void addEntity(final String host, final AgentId agentId, final EntityDescriptor entity) {
		if(SwingUtilities.isEventDispatchThread()) {
			super.addEntity(host, agentId, entity);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
                        addEntitySuper(host, agentId, entity);
                    } catch(Exception e) {
                        logger.error(e);
                    }
				}
				});
		}
	}

	/**
	 * @see com.ixora.rms.client.model.SessionModel#updateEntities(java.lang.String, java.lang.String, com.ixora.rms.EntityId, com.ixora.rms.EntityDescriptor[])
	 */
	public void updateEntities(final String host, final AgentId agentId,
			final EntityDescriptorTree descs) {
		if(SwingUtilities.isEventDispatchThread()) {
			super.updateEntities(host, agentId, descs);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
                        updateEntitiesSuper(host, agentId, descs);
                    } catch(Exception e) {
                        logger.error(e);
                    }
				}
			});
		}
	}

	/**
	 * @see com.ixora.rms.client.model.SessionModel#updateEntities(java.lang.String, java.lang.String, com.ixora.rms.EntityDescriptorTree)
	 */
	private void updateEntitiesSuper(String host, AgentId agentId,
			EntityDescriptorTree descs) {
		super.updateEntities(host, agentId, descs);
	}

	/**
	 * @throws RemoteException
	 * @throws RMSException
	 * @see com.ixora.rms.client.model.SessionModel#addHost(java.lang.String)
	 */
	public void addHost(final String host) throws RMSException, RemoteException {
		if(SwingUtilities.isEventDispatchThread()) {
			super.addHost(host);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
                        addHostSuper(host);
                    } catch(Exception e) {
                        logger.error(e);
                    }
				}
				});
		}

	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleAgentNonFatalError(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	protected void handleAgentNonFatalError(
			final String host,
			final AgentId agentId,
			final Throwable t) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleAgentNonFatalErrorSuper(host, agentId, t);
			}
			});
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleAgentStateChanged(java.lang.String, AgentId, com.ixora.rms.AgentCategory, java.lang.Throwable)
	 */
	protected void handleAgentStateChanged(final String host,
			final AgentId agentId,
			final AgentState state,
			final Throwable e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleAgentStateChangedSuper(host, agentId, state, e);
			}
		});
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleProviderStateChanged(java.lang.String, com.ixora.rms.internal.agents.AgentId, java.lang.String, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
	 */
	protected void handleProviderStateChanged(final String host, final AgentId agentId,
			final String providerInstanceName, final ProviderState state, final Throwable e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleProviderStateChangedSuper(host, agentId, providerInstanceName,
						state, e);
			}
		});
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleEntitiesChanged(java.lang.String, java.lang.String, com.ixora.rms.EntityId, com.ixora.rms.EntityDescriptor[])
	 */
	protected void handleEntitiesChanged(final String host,
			final AgentId agentId,
			final EntityDescriptorTree entities) {
		if(SwingUtilities.isEventDispatchThread()) {
			// it helps debugging
			handleEntitiesChangedSuper(host, agentId, entities);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					handleEntitiesChangedSuper(host, agentId, entities);
				}
			});
		}
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleHostStateChanged(java.lang.String, int, com.ixora.common.remote.ServiceState)
	 */
	protected void handleHostStateChanged(
			final String host,
			final int serviceID,
			final ServiceState state) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleHostStateChangedSuper(host, serviceID, state);
			}
		});
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleUpdateHostInfo(java.lang.String, com.ixora.rms.struct.HostInformation)
	 */
	protected void handleUpdateHostInfo(final String host,
			final HostInformation info) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleUpdateHostInfoSuper(host, info);
			}
			});
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#addAgent(java.lang.String, com.ixora.rms.control.struct.AgentInstallationInfo, com.ixora.rms.control.struct.AgentDeploymentInfo)
	 */
	private void addAgentSuper(String host, AgentInstallationData aid,
			AgentInstanceData ad) {
		super.addAgent(host, aid, ad);
	}
	/**
	 * @throws RemoteException
	 * @throws RMSException
	 * @see com.ixora.rms.client.model.SessionModel#addHost(java.lang.String)
	 */
	private void addHostSuper(String host) throws RMSException, RemoteException {
		super.addHost(host);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleAgentNonFatalError(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	private void handleAgentNonFatalErrorSuper(String host, AgentId agentId,
			Throwable t) {
		super.handleAgentNonFatalError(host, agentId, t);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleAgentStateChanged(java.lang.String, AgentId, com.ixora.rms.AgentCategory, java.lang.Throwable)
	 */
	private void handleAgentStateChangedSuper(String host, AgentId agentId,
			AgentState state, Throwable e) {
		super.handleAgentStateChanged(host, agentId, state, e);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleAgentStateChanged(java.lang.String, AgentId, com.ixora.rms.AgentCategory, java.lang.Throwable)
	 */
	private void handleProviderStateChangedSuper(String host, AgentId agentId, String providerInstanceName,
			ProviderState state, Throwable e) {
		super.handleProviderStateChanged(host, agentId, providerInstanceName, state, e);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleEntitiesChanged(java.lang.String, java.lang.String, com.ixora.rms.EntityId, com.ixora.rms.EntityDescriptor[])
	 */
	private void handleEntitiesChangedSuper(String host, AgentId agentId, EntityDescriptorTree entities) {
		super.handleEntitiesChanged(host, agentId, entities);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleHostStateChanged(java.lang.String, int, com.ixora.common.remote.ServiceState)
	 */
	private void handleHostStateChangedSuper(String host, int serviceID,
			ServiceState state) {
		super.handleHostStateChanged(host, serviceID, state);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#handleUpdateHostInfo(java.lang.String, com.ixora.rms.struct.HostInformation)
	 */
	private void handleUpdateHostInfoSuper(String host, HostInformation info) {
		super.handleUpdateHostInfo(host, info);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionModel#addEntity(java.lang.String, java.lang.String, com.ixora.rms.EntityDescriptor)
	 */
	public void addEntitySuper(String host, AgentId agentId, EntityDescriptor entity) {
		super.addEntity(host, agentId, entity);
	}


}
