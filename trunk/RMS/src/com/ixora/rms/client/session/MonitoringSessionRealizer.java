/*
 * Created on Feb 7, 2004
 */
package com.ixora.rms.client.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.Progress;
import com.ixora.common.ProgressProvider;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentActivationTuple;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.model.DashboardInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.dataengine.definitions.SingleCounterQueryDef;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.exception.UnreachableHostManager;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewMap;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.services.MonitoringSessionService;

/**
 * MonitoringSessionRealizer.
 * @author Daniel Moraru
 */
public final class MonitoringSessionRealizer {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(MonitoringSessionRealizer.class);

	/** Cancel flag */
	private volatile boolean canceled;
	/** Listener */
	private SessionModel sessionModel;
	/** Progress provider */
	private Progress progress;

	/**
	 * Constructor.
	 * @param sessionModel
	 */
	public MonitoringSessionRealizer(SessionModel sessionModel) {
		super();
		this.sessionModel = sessionModel;
		this.progress = new Progress();
	}

	/**
	 * @return the progress provider
	 */
	public ProgressProvider getProgressProvider() {
		return this.progress;
	}

	/**
	 * Realizes the given monitoring session for a live monitoring
	 * session. Used by a live session.
	 * @param rmsHostMonitor
	 * @param rmsSession
	 * @param session
	 * @throws RMSException
	 */
	public void realize(
			HostMonitorService rmsHostMonitor,
			AgentRepositoryService rmsAr,
			MonitoringSessionService rmsSession,
			MonitoringSessionDescriptor session)
				throws UnreachableHostManager,
				AgentIsNotInstalled,
				InvalidAgentState,
				InvalidConfiguration,
				RMSException,
				RemoteException {
		progress.reset();
		canceled = false;
		// prepare hosts to add to the host manager
		// in one go and also prepare the progress information
		final Collection<HostDetails> hosts = session.getHostDetails();
		final List<String> hostNames = new ArrayList<String>(hosts.size());
		String host;
		int max = hosts.size();
		for(HostDetails hd : hosts) {
			host = hd.getHost();
			hostNames.add(host);
			sessionModel.addHost(host);
			Collection<AgentDetails> agents = hd.getAgents();
			max += agents.size();
			for(AgentDetails ad : agents) {
				max += ad.getEntities().size();
			}
		}
		progress.setMax(max);
		rmsHostMonitor.addHosts(hostNames, true);
		// for each host...
		for(Iterator<HostDetails> iter1 = hosts.iterator();
				iter1.hasNext() && !canceled;) {
			HostDetails hd = iter1.next();
			host = hd.getHost();
			progress.setTask(host);
			Collection<AgentDetails> agents = hd.getAgents();
			// each agent...
            AgentDetails agentSessionDetails = null;
			for(Iterator<AgentDetails> iter2 = agents.iterator();
					iter2.hasNext() && !canceled;) {
				agentSessionDetails = iter2.next();
				AgentInstallationData agentInstallationData = null;
				try {
					AgentInstanceData agentInstanceData = agentSessionDetails.getAgentDeploymentDtls();
					agentInstallationData = rmsAr.
						getInstalledAgents().get(agentSessionDetails.getAgentDeploymentDtls().getAgentId().getInstallationId());
					if(agentInstallationData == null) {
						// session is out of synch with
						// the control center
						throw new AgentIsNotInstalled(agentSessionDetails.getAgentDeploymentDtls().getAgentId().getInstallationId());
					}
					progress.setTask(host + EntityId.DELIMITER +
							MessageRepository.get(
									agentInstallationData.getMessageCatalog(),
									agentInstallationData.getAgentName()));
					AgentActivationTuple tuple = rmsSession.activateAgent(
                            agentInstanceData.getAgentActivationData());
					agentInstanceData = new AgentInstanceData(agentInstanceData.getAgentActivationData(), tuple.getDescriptor());
					progress.setDelta(1);
					sessionModel.addAgent(host, agentInstallationData, agentInstanceData);
					Collection<EntityDetails> entities = agentSessionDetails.getEntities();
					// each entity...
                    EntityDetails ed = null;
                    for(Iterator<EntityDetails> iter3 = entities.iterator();
						iter3.hasNext() && !canceled;) {
					    ed = iter3.next();
						progress.setTask(
								host
								+ EntityId.DELIMITER
								+ MessageRepository.get(agentInstallationData.getMessageCatalog(), agentInstallationData.getAgentName())
								+ EntityId.DELIMITER
								+ EntityId.getTranslatedPath(agentInstallationData.getMessageCatalog(), ed.getEntityId()));
						try {
							EntityDescriptorTree descriptors = rmsSession.configureEntity(
									host,
									agentInstanceData.getAgentId(),
									ed.getEntityId(),
									ed.getConfiguration());
							// update the model
							sessionModel.updateEntities(
									host,
									agentInstanceData.getAgentId(),
									descriptors);
						} catch(Exception e) {
                            // TODO localize
                            if(ed == null) {
                                progress.nonFatalError("Failed to configure entity", e);
                            } else {
                                progress.nonFatalError("Failed to configure entity "
                                        + ed.getEntityId(), e);
                            }
						    // log and keep going...
							// this error is natural when there are
							// volatile entities in the session so enable
							// output just for tracing
							if(logger.isTraceEnabled()) {
								logger.error(e);
							}
						}
						progress.setDelta(1);
					}
				} catch(Exception e) {
                    // TODO localize
                    if(agentSessionDetails == null) {
                        progress.nonFatalError("Failed to activate agent", e);
                    } else {
                    	String agentName;
                    	if(agentInstallationData != null) {
                    		agentName = MessageRepository.get(agentInstallationData.getMessageCatalog(), agentInstallationData.getAgentName());
                    	} else {
                    		agentName = agentSessionDetails.getAgentDeploymentDtls().getAgentId().toString();
                    	}
                        progress.nonFatalError("Failed to activate agent " + agentName, e);
                    }
				    // log and keep going...
                    progress.nonFatalError("Failed to activate agent ", e);
				    logger.error(e);
				}
			}
		}
		progress.done();
	}

	/**
	 * Realizes the data views from the given session. This method is required because it
	 * reads the session model whereas the realize() method only updats the model and it allows the
	 * callers to separate the calls. (the Swing implementation of the session model has all updates
	 * occuring on the event dispatcher thread and without this separation race conditions occur)
	 * @param realizer
	 * @param session
	 * @param rmsDataViewRepository
	 */
	public void realizeQueries(
			QueryRealizer realizer,
			MonitoringSessionDescriptor session,
			DataViewRepositoryService rmsDataViewRepository) {
		Map<QueryId, QueryDef> queries = session.getQueries();
		if(Utils.isEmptyMap(queries)) {
			return;
		}
		canceled = false;
		progress.reset();
		progress.setMax(queries.size());
		// realizing queries
	    for(Map.Entry<QueryId, QueryDef> query : queries.entrySet()) {
	    	if(canceled) {
	    		break;
	    	}
			try {
				ResourceId queryContext = query.getKey().getContext();
				String agentVersion = getAgentVersion(queryContext, session);
				QueryDef queryDef = query.getValue();
				if(!(queryDef instanceof SingleCounterQueryDef)) {
					// update the definition of the query
					// using data from the data view repository only
					// if the name of the query is equal with the name
					// of a data view in the repository
					String id = queryDef.getIdentifier();
					DataViewMap dvs = rmsDataViewRepository.getDataViewMap(queryContext);
					if(dvs != null) {
						// get the data views only for the
						// required agent version
						Collection<DataView> alldv = dvs.getForAgentVersion(agentVersion);
						if(!Utils.isEmptyCollection(alldv)) {
							// replace query def
							for(DataView dv : alldv) {
								if(dv.getName().equals(id)) {
									queryDef = dv.getQueryDef();
									break;
								}
							}
						}
					}
				}
				// register with query realizer
			    realizer.realizeQuery(query.getKey().getContext(), queryDef, null);
			} catch(Exception e) {
			    // log and keep going...
				progress.nonFatalError("Failed to realize query " + query.getKey(), e);
			    logger.error(e);
			}
			progress.setDelta(1);
        }
	    progress.done();
	}

	/**
	 * @param queryContext
	 * @param session
	 * @return
	 */
	private String getAgentVersion(ResourceId queryContext, MonitoringSessionDescriptor session) {
		if(queryContext == null) {
			return null;
		}
		String host = queryContext.getHostId().toString();
		Collection<HostDetails> hosts = session.getHostDetails();
		for(HostDetails hostDtls : hosts) {
			if(hostDtls.getHost().equals(host)) {
				AgentId agent = queryContext.getAgentId();
				Collection<AgentDetails> agents = hostDtls.getAgents();
				for(AgentDetails agentDtls : agents) {
					if(agentDtls
						.getAgentDeploymentDtls().getAgentId()
						.equals(agent)) {
						return agentDtls.getAgentDeploymentDtls()
							.getAgentActivationData().getConfiguration()
							.getSystemUnderObservationVersion();
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param queryRealizer
	 * @param session
	 */
	public void realizeDashboards(QueryRealizer queryRealizer, MonitoringSessionDescriptor session) {
		Collection<DashboardId> dashboards = session.getDashboards();
		if(Utils.isEmptyCollection(dashboards)) {
			return;
		}
		canceled = false;
		progress.reset();
		progress.setMax(dashboards.size());
		// realizing dashboards
	    for(DashboardId did : dashboards) {
	    	if(canceled) {
	    		break;
	    	}
	    	try {
	    		sessionModel.getDashboardHelper().setDashboardFlag(DashboardInfo.ENABLED,
	    				did.getContext(), did.getName(), true, true);
	    	} catch(Exception e) {
			    // log and keep going...
				progress.nonFatalError("Failed to realize dashboard " + did, e);
	    		logger.error("Failed to realize dashboard " + did + ". Error: " + e.getLocalizedMessage());
			}
	    }
	    progress.done();
	}

	/**
	 * Realizes the given monitoring session for a replayed monitoring
	 * session. Used by a log replay session.
	 * @param rmsAr
	 * @param session
	 * @throws RemoteException
	 * @throws RMSException
	 * @throws InvalidAgentState
	 * @throws InvalidConfiguration
	 * @throws InvalidEntity
	 */
	public void realize(
			AgentRepositoryService rmsAr,
			MonitoringSessionDescriptor session)
				throws InvalidEntity, InvalidConfiguration, InvalidAgentState, RMSException, RemoteException {
	    sessionModel.setSessionName(session.getName());
	    canceled = false;
		// prepare progress info
		Collection<HostDetails> hosts = session.getHostDetails();
		List hostNames = new ArrayList(hosts.size());
		HostDetails hd;
		String host;
		int max = hosts.size();
		for(Iterator<HostDetails> iter1 = hosts.iterator(); iter1.hasNext();) {
			hd = iter1.next();
			host = hd.getHost();
			hostNames.add(host);
			sessionModel.addHost(host);
			Collection<AgentDetails> agents = hd.getAgents();
			max += agents.size();
			AgentDetails ad;
			for(Iterator<AgentDetails> iter2 = agents.iterator(); iter2.hasNext(); ) {
				ad = iter2.next();
				max += ad.getEntities().size();
			}
		}
		progress.setMax(max);
		// do the actual work
		// for each host...
		for(Iterator<HostDetails> iter1 = hosts.iterator();
				iter1.hasNext() && !canceled;) {
			hd = iter1.next();
			host = hd.getHost();
			progress.setTask(host);
			Collection<AgentDetails> agents = hd.getAgents();
			// each agent...
			for(Iterator<AgentDetails> iter2 = agents.iterator();
					iter2.hasNext() && !canceled;) {
				AgentDetails ad = iter2.next();
				try {
					AgentInstanceData dd = ad.getAgentDeploymentDtls();
					AgentInstallationData id = rmsAr.
						getInstalledAgents().get(ad.getAgentDeploymentDtls().getAgentId().getInstallationId());
					if(id == null) {
						// session is out of synch with
						// the control center
						throw new AgentIsNotInstalled(ad.getAgentDeploymentDtls().getAgentId().getInstallationId());
					}
					progress.setTask(host + EntityId.DELIMITER +
							MessageRepository.get(
									id.getMessageCatalog(),
									id.getAgentName()));
					progress.setDelta(1);
					sessionModel.addAgent(host, id, dd);
					Collection entities = ad.getEntities();
					// each entity...
					for(Iterator<EntityDetails> iter3 = entities.iterator();
						iter3.hasNext() && !canceled;) {
						EntityDetails ed = iter3.next();
						try {
							progress.setTask(
									host
									+ EntityId.DELIMITER
									+ MessageRepository.get(id.getMessageCatalog(), id.getAgentName())
									+ EntityId.DELIMITER
									+ EntityId.getTranslatedPath(id.getMessageCatalog(), ed.getEntityId()));
							// update the model
							sessionModel.addEntity(
							        host,
							        dd.getAgentId(), ed.getDescriptor());
							progress.setDelta(1);
						} catch(Exception e) {
							logger.error(e);
						}
					}
				} catch(Exception e) {
					logger.error(e);
				}
			}
		}
		progress.done();
	}

	/**
	 * Cancels the current activity.
	 */
	public void cancel() {
		canceled = true;
	}
}
