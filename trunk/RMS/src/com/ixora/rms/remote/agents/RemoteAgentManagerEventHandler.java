package com.ixora.rms.remote.agents;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.ixora.RMIServiceNames;
import com.ixora.common.RMIServices;
import com.ixora.common.remote.ClientSocketFactory;
import com.ixora.common.remote.ServerSocketFactory;
import com.ixora.common.thread.RunQueue;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.agents.HostAgentManager;

/**
 * This class handles events from remote agent managers and
 * used with HostAgentManager.Listener helps interested parties to handle remote
 * events just by implementing a local interface.
 * @author Daniel Moraru
 */
public final class RemoteAgentManagerEventHandler
	extends UnicastRemoteObject
	implements RemoteAgentManagerListener {
	private static final long serialVersionUID = 8918276091597465398L;
	/**
	 * Local listener.
	 */
	private HostAgentManager.Listener localListener;
	/**
	 * Event queue. The processing of events is done
	 * asynchronously to break the call chain and avoid
	 * deadlocks.
	 */
	private RunQueue processor;

    /**
     * Constructor for HostManagerEventHandler.
     * @param listener
     * @throws RemoteException
     */
    public RemoteAgentManagerEventHandler(HostAgentManager.Listener listener) throws RemoteException {
        super(RMIServices.instance().getPort(RMIServiceNames.REMOTEAGENTMANAGEREVENTHANDLER));
        init(listener);
    }

	/**
	 * Constructor for HostManagerEventHandler.
     * @param csf
     * @param ssf
	 * @param listener
	 * @throws RemoteException
	 */
	public RemoteAgentManagerEventHandler(
            ClientSocketFactory csf,
            ServerSocketFactory ssf, HostAgentManager.Listener listener) throws RemoteException {
		super(RMIServices.instance().getPort(RMIServiceNames.REMOTEAGENTMANAGEREVENTHANDLER), csf, ssf);
        init(listener);
	}

    /**
     * @param listener
     * @throws StartableError
     */
    private void init(HostAgentManager.Listener listener) {
        if(listener == null) {
            throw new IllegalArgumentException("null local listener");
        }
        this.localListener = listener;
        this.processor = new RunQueue();
        this.processor.start();
    }

	/**
	 * @see com.ixora.rms.remote.event.RemoteAgentManagerListener#monitoringAgentStateChanged(String, AgentId, AgentState, Exception)
	 */
	public void monitoringAgentStateChanged(
		final String host,
		final AgentId agentId,
		final AgentState state,
		final Throwable e) {
		this.processor.run(new Runnable() {
			public void run() {
				localListener.agentStateChanged(host, agentId, state, e);
			}});
	}

	/**
	 * @see com.ixora.rms.control.RemoteDataSink#addDataBuffer(DataBuffer)
	 */
	public void receiveDataBuffers(final AgentDataBuffer[] buff) {
		if(buff == null) {
			return;
		}
		this.processor.run(new Runnable() {
			public void run() {
				localListener.receiveDataBuffers(buff);
			}});
	}

	/**
	 * @see com.ixora.rms.remote.event.RemoteAgentManagerListener#monitoredEntitiesChanged(String, AgentId, EntityDescriptorTree)
	 */
	public void monitoredEntitiesChanged(
		final String host,
		final AgentId agentId,
		final EntityDescriptorTree entities) {
		this.processor.run(new Runnable() {
			public void run() {
				localListener.entitiesChanged(host, agentId, entities);
			}});
	}

	/**
	 * @see com.ixora.rms.remote.agents.RemoteAgentManagerListener#monitoringAgentNonFatalError(java.lang.String, AgentId, java.lang.Throwable)
	 */
	public void monitoringAgentNonFatalError(
			final String host,
			final AgentId agentId,
			final Throwable t) throws RemoteException {
		this.processor.run(new Runnable() {
			public void run() {
				localListener.agentNonFatalError(host, agentId, t);
			}});
	}
}
