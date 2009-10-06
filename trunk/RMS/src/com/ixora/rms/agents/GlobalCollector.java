/**
 * 19-Mar-2006
 */
package com.ixora.rms.agents;

import java.util.List;

import com.ixora.rms.Collector;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.remote.agents.RemoteAgentManager;

/**
 * This class is used to collect data from remote HostAgentManagers
 * @author Daniel Moraru
 */
final class GlobalCollector extends Collector {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(GlobalCollector.class);

	/**
	 * Callback.
	 */
	public interface Callback {
		/**
		 * @return the list of remote agent managers for which
		 * to collect data.
		 */
		List<RemoteAgentManager> getRemoteAgentManagers();
	}

	private HostAgentManager.Listener fListener;
	private Callback fCallback;

	/**
	 * @param listener
	 * @param callback
	 */
	public GlobalCollector(HostAgentManager.Listener listener, Callback callback) {
		super("GlobalCollectorAgents");
		if(listener == null) {
			throw new IllegalArgumentException("Null listener");
		}
		if(callback == null) {
			throw new IllegalArgumentException("Null callback");
		}
		fListener = listener;
		fCallback = callback;
	}

	/**
	 * @see com.ixora.rms.Collector#collect()
	 */
	protected void collect() throws Throwable {
		try {
			List<RemoteAgentManager> rams = fCallback.getRemoteAgentManagers();
			if(Utils.isEmptyCollection(rams)) {
				return;
			}
			for(RemoteAgentManager ram : rams) {
				if(ram == null) {
					continue;
				}
				AgentPollBuffer buff = ram.getAgentPollBuffer();
				AgentDataBuffer[][] adbs = buff.getDataBuffers();
				if(!Utils.isEmptyArray(adbs)) {
					for(AgentDataBuffer[] adb : adbs) {
						fListener.receiveDataBuffers(adb);
					}
				}

				AgentPollBuffer.StateChange[] scs = buff.getStateChanges();
				if(!Utils.isEmptyArray(scs)) {
					for(AgentPollBuffer.StateChange sc : scs) {
						fListener.agentStateChanged(sc.fHost, sc.fAgentId,
								sc.fAgentState, sc.fError);
					}
				}

				AgentPollBuffer.EntitiesChange[] ecs = buff.getEntitiesChanges();
				if(!Utils.isEmptyArray(ecs)) {
					for(AgentPollBuffer.EntitiesChange esc : ecs) {
						fListener.entitiesChanged(esc.fHost, esc.fAgentId,
								esc.fEntityTree);
					}
				}

				AgentPollBuffer.NonFatalError[] nfes = buff.getNonFatalErrors();
				if(!Utils.isEmptyArray(nfes)) {
					for(AgentPollBuffer.NonFatalError nfe : nfes) {
						fListener.agentNonFatalError(nfe.fHost, nfe.fAgentId,
								nfe.fError);
					}
				}
			}
		} catch(Throwable e) {
			logger.error(e);
		}
	}
}
