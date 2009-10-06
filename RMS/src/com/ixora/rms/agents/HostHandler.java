package com.ixora.rms.agents;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.rms.HostMonitor;
import com.ixora.rms.HostRemoteManagedHandler;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.remote.agents.RemoteAgentManager;
import com.ixora.rms.remote.agents.RemoteAgentManagerImpl;
import com.ixora.rms.remote.agents.RemoteAgentManagerListener;
import com.ixora.rms.services.AgentRepositoryService;

/**
 * HostHandlers(remote and local).
 */
final class HostHandler extends HostRemoteManagedHandler {
	static final class AgentData {
		AgentLocation fLocation;
		AgentData(AgentLocation location) {
			this.fLocation = location;
		}
	}
	/** Manager for the local agents */
	private HostAgentManager fHostAgentManager;
	/** Registered agents */
	private Map<AgentId, HostHandler.AgentData> fAgentData;

	/**
	 * HostHandlers.
	 * @param ars
	 * @param hm
	 * @param host host to handle
	 * @param listenerLocal HostAgentManager listener
	 * @param listenerRemote RemoteAgentManager listener
	 */
	public HostHandler(
			AgentRepositoryService ars,
			HostMonitor hm,
			String host,
			HostAgentManager.Listener listenerLocalAgent,
			RemoteAgentManagerListener listenerRemoteAgent) {
		super(hm, host, listenerRemoteAgent);
		if(ars == null) {
			throw new IllegalArgumentException("null agent repository");
		}
		if(host == null) {
			throw new IllegalArgumentException("null host");
		}
		if(listenerLocalAgent == null) {
			throw new IllegalArgumentException("null local agent listener");
		}
		// create the local manager immediately and use
		// lazy initialization for the remote one
		this.fHostAgentManager = new HostAgentManagerImpl(ars, host, listenerLocalAgent);
		this.fAgentData = new HashMap<AgentId, HostHandler.AgentData>();
	}
	/**
	 * @return the HostAgentManager for this host.
	 */
	public HostAgentManager createLocalAgentManager() {
		return fHostAgentManager;
	}
	/**
	 * @return the HostAgentManager for this host.
	 */
	public HostAgentManager getLocalAgentManager() {
		return fHostAgentManager;
	}
	/**
	 * @return a remote agent reference
	 * @throws RMSException
	 * @throws RemoteException
	 */
	public RemoteAgentManager createRemoteAgentManager() throws RMSException, RemoteException {
		return (RemoteAgentManager)createRemoteManaged(RemoteAgentManagerImpl.class.getName());
	}

	/**
	 * @return the RemoteAgentManager for this host.
	 */
	public RemoteAgentManager getRemoteAgentManager() {
		return (RemoteAgentManager)this.fRemoteManaged;
	}
	/**
	 * Registers an agent as being deployed according to <code>location</code>.
	 * @param agent
	 * @param location
	 */
	public synchronized void registerAgent(AgentId agent, AgentLocation location) {
		this.fAgentData.put(agent, new AgentData(location));
	}
	/**
	 * Unregisters an agent as being deployed remotely.
	 * @param agent
	 */
	public synchronized void unregisterAgent(AgentId agent) {
		this.fAgentData.remove(agent);
	}
	/**
	 * Unregisters all agents.
	 */
	public synchronized void unregisterAllAgents() {
		this.fAgentData.clear();
	}
	/**
	 * @return the list of agents that have been deployed
	 * according to <code>location</code>
	 */
	// this is why synch is necessary on this class,
	// this method is called from MonitoringSession when
	// a HostMonitor event is trigerred
	public synchronized List<AgentId> getAgents(AgentLocation location) {
		List<AgentId> ret = new LinkedList<AgentId>();
		for(Map.Entry<AgentId, HostHandler.AgentData> me : this.fAgentData.entrySet()) {
			HostHandler.AgentData ad = me.getValue();
			if((ad.fLocation == location)) {
				ret.add(me.getKey());
			}
		}
		return ret;
	}
	/**
	 * @param agentId
	 * @return true if the given agent has already been
	 * activated on this host
	 */
	public synchronized boolean isAgentActivated(AgentId agentId) {
		if(this.fAgentData.get(agentId) != null) {
			return true;
		}
		return false;
	}

	/**
	 *
	 */
	public void destroyRemoteAgentManager() {
		destroyRemoteManaged();
	}
}