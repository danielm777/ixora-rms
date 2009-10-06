package com.ixora.rms.remote.agents;

import java.awt.image.DataBuffer;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

import com.ixora.RMIServiceNames;
import com.ixora.remote.RemoteManagedListener;
import com.ixora.remote.exception.RemoteManagedListenerIsUnreachable;
import com.ixora.common.RMIServices;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentActivationTuple;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentConfigurationTuple;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentPollBuffer;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.agents.HostAgentManager;
import com.ixora.rms.agents.HostAgentManagerImpl;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.InvalidAgentState;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentRepositoryManager;
import com.ixora.rms.services.AgentRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class RemoteAgentManagerImpl
			extends UnicastRemoteObject
					implements RemoteAgentManager, Unreferenced {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(RemoteAgentManagerImpl.class);
	/** Agent manager */
	private HostAgentManager fAgentManager;
	/** Listener */
	private RemoteAgentManagerListener fListener;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Agent repository */
	private AgentRepositoryService fAgentRepository;
    /** True if in the process of shutting down */
    private volatile boolean fShuttingDown;
    /** Used only when the listener is null, otherwise it's null */
    private AgentPollBufferManager fPollBuffManager;

	/**
	 * Event handler.
	 */
    // event dispatchig will be disable during shutdown
	private final class EventHandler implements HostAgentManager.Listener {

		/**
		 * @see com.ixora.rms.RemoteAgentManagerListener#addDataBuffer(DataBuffer)
		 */
		public void receiveDataBuffers(AgentDataBuffer[] buff) {
			handleDataBuffers(buff);
		}

		/**
		 * @see com.ixora.rms.RemoteAgentManagerListener#monitoredEntitiesChanged(String, AgentId, EntityDescriptorTree)
		 */
		public void entitiesChanged(
			String host,
			AgentId agentId,
			EntityDescriptorTree entities) {
			handleEntitiesChanged(host, agentId, entities);
		}

		/**
		 * @see com.ixora.rms.RemoteAgentManagerListener#monitoringAgentStateChanged(String, AgentId, AgentState, Throwable)
		 */
		public void agentStateChanged(
			String host,
			AgentId agentId,
			AgentState state,
			Throwable e) {
			handleAgentStateChanged(host, agentId, state, e);
		}

		/**
		 * @see com.ixora.rms.agents.HostAgentManager.Listener#agentNonFatalError(java.lang.String, AgentId, java.lang.Throwable)
		 */
		public void agentNonFatalError(String host, AgentId agentId, Throwable t) {
			handleAgentNonFatalError(host, agentId, t);
		}
	}

	/**
	 * Constructor for RemoteAgentManagerImpl.
	 * @throws RemoteException
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public RemoteAgentManagerImpl() throws RemoteException, XMLException, FileNotFoundException {
		super(RMIServices.instance().getPort(RMIServiceNames.REMOTEAGENTMANAGER));
		// entry point on the remote host for agents,
		// this class will play the part of application container
		// so objects required by all other objects in this application
		// must be initialized by this class (lazy or not)
		this.fAgentRepository = new AgentRepositoryManager();
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#activateMonitoringAgent(String, String, AgentConfiguration)
	 */
	public AgentActivationTuple activateAgent(
		String agentInstallationId,
		AgentConfiguration conf)
		throws AgentIsNotInstalled, InvalidConfiguration,
		InvalidAgentState,
		RMSException {
		try {
			return this.fAgentManager.activateAgent(agentInstallationId, conf);
		} catch (RMSException e) {
			throw e;
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#deactivateMonitoringAgent(String)
	 */
	public void deactivateAgent(AgentId agentId)
		throws RMSException, RemoteException {
		try {
			this.fAgentManager.deactivateAgent(agentId);
		} catch (RMSException e) {
			throw e;
		} catch (Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#startMonitoringAgent(String)
	 */
	public void startAgent(AgentId agentId)
		throws InvalidAgentState, RMSException {
		try {
			this.fAgentManager.startAgent(agentId);
		} catch(RMSException e) {
			throw e;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#stopMonitoringAgent(AgentId)
	 */
	public void stopAgent(AgentId agentId)
		throws InvalidAgentState, RMSException {
		try {
			this.fAgentManager.stopAgent(agentId);
		} catch (RMSException e) {
			throw e;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#configureMonitoringAgent(AgentId, AgentConfiguration)
	 */
	public AgentConfigurationTuple configureAgent(
		AgentId agentId,
		AgentConfiguration conf)
		throws InvalidConfiguration, RMSException {
		try {
			return this.fAgentManager.configureAgent(agentId, conf);
		} catch(RMSException e) {
			throw e;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#getAgentMonitoredEntities(String, EntityId)
	 */
	public EntityDescriptorTree getEntities(
		AgentId agentId,
		EntityId parent,
		boolean recursive,
		boolean refresh) throws RMSException {
		try {
			return this.fAgentManager.getEntities(agentId, parent, recursive, refresh);
		} catch (RMSException e) {
			throw e;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#configureEntity(String, EntityId, EntityConfiguration)
	 */
	public EntityDescriptorTree configureEntity(
		AgentId agentId,
		EntityId entity,
		EntityConfiguration conf)
		throws
			InvalidConfiguration,
			InvalidEntity,
			InvalidAgentState,
			RMSException {
		try {
			return this.fAgentManager.configureEntity(agentId, entity, conf);
		} catch(RMSException e) {
			throw e;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#getAgentState(AgentId)
	 */
	public AgentState getAgentState(AgentId agentId)
		throws RMSException {
		try {
			return this.fAgentManager.getAgentState(agentId);
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#deactivateAllAgents()
	 */
	public void deactivateAllAgents()
		throws RMSException, RemoteException {
		try {
			this.fAgentManager.deactivateAllAgents();
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#startAllAgents()
	 */
	public void startAllAgents() throws RMSException, RemoteException {
		try {
			this.fAgentManager.startAllAgents();
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#stopAllAgents()
	 */
	public void stopAllAgents() throws RMSException, RemoteException {
		try {
			this.fAgentManager.stopAllAgents();
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#configureAllAgents(com.ixora.rms.struct.AgentConfiguration)
	 */
	public AgentConfigurationTuple[] configureAllAgents(AgentConfiguration conf) throws InvalidConfiguration, RMSException, RemoteException {
		try {
			return this.fAgentManager.configureAllAgents(conf);
		} catch(RMSException e) {
			throw e;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @throws RemoteManagedListenerIsUnreachable
	 * @see com.ixora.remote.RemoteManaged#initialize(java.lang.String, com.ixora.rms.remote.RemoteManagedListener)
	 */
	public void initialize(String host, RemoteManagedListener listener) throws RemoteManagedListenerIsUnreachable {
		if(host == null) {
			throw new IllegalArgumentException("null host name");
		}
		if(listener != null) {
			testRemoteListener(listener);
		} else {
			fPollBuffManager = new AgentPollBufferManager();
		}
		this.fEventHandler = new EventHandler();
		this.fListener = (RemoteAgentManagerListener)listener;
		this.fAgentManager = new HostAgentManagerImpl(fAgentRepository, host, this.fEventHandler);
	}

	/**
	 * @see com.ixora.remote.RemoteManaged#shutdown()
	 */
	public void shutdown() {
		_shutdown();
	}

	/**
	 * @return
	 */
	private void testRemoteListener(RemoteManagedListener listener) throws RemoteManagedListenerIsUnreachable {
		if(listener == null) {
			return;
		}
		try {
			((RemoteAgentManagerListener)listener).receiveDataBuffers(null);
		} catch(Exception e) {
			throw new RemoteManagedListenerIsUnreachable(e);
		}
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManager#getAgentPollBuffer()
	 */
	public AgentPollBuffer getAgentPollBuffer() throws RMSException, RemoteException {
		return fPollBuffManager == null ? null : fPollBuffManager.getAgentPullBuffer();
	}

	/**
	 * @param buff
	 */
	private void handleDataBuffers(AgentDataBuffer[] buff) {
		try {
		    if(!fShuttingDown) {
		    	if(fListener != null) {
		    		fListener.receiveDataBuffers(buff);
		    	} else {
		    		fPollBuffManager.receiveDataBuffers(buff);
		    	}
		    }
		} catch (RemoteException e) {
			logger.error(e);
		}
	}

	/**
	 * @param host
	 * @param agentId
	 * @param entities
	 */
	private void handleEntitiesChanged(String host, AgentId agentId, EntityDescriptorTree entities) {
		try {
		    if(!fShuttingDown) {
		    	if(fListener != null) {
		    		fListener.monitoredEntitiesChanged(host, agentId, entities);
		    	} else {
		    		fPollBuffManager.entitiesChanged(host, agentId, entities);
		    	}
		    }
		} catch (RemoteException e) {
			logger.error(e);
		}
	}

	/**
	 * @param host
	 * @param agentId
	 * @param state
	 * @param e
	 */
	private void handleAgentStateChanged(String host, AgentId agentId, AgentState state, Throwable e) {
		try {
		    if(!fShuttingDown) {
		    	if(fListener != null) {
		    		fListener.monitoringAgentStateChanged(host, agentId, state, e);
		    	} else {
		    		fPollBuffManager.agentStateChanged(host, agentId, state, e);
		    	}
		    }
		} catch (RemoteException ex) {
			logger.error(ex);
		}
	}

	/**
	 * @param host
	 * @param agentId
	 * @param t
	 */
	private void handleAgentNonFatalError(String host, AgentId agentId, Throwable t) {
		try {
		    if(!fShuttingDown) {
		    	if(fListener != null) {
		    		fListener.monitoringAgentNonFatalError(host, agentId, t);
		    	} else {
		    		fPollBuffManager.agentNonFatalError(host, agentId, t);
		    	}
		    }
		} catch (RemoteException ex) {
			logger.error(ex);
		}
	}

	/**
	 * @see java.rmi.server.Unreferenced#unreferenced()
	 */
	public void unreferenced() {
		_shutdown();
	}

	/**
	 *
	 */
	private void _shutdown() {
       fShuttingDown = true;
       this.fAgentManager.deactivateAllAgents();
	}
}
