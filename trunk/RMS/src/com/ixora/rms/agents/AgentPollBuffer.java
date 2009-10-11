/**
 * 19-Mar-2006
 */
package com.ixora.rms.agents;

import java.io.Serializable;

import com.ixora.rms.EntityDescriptorTree;

/**
 * This is the data retrieved from the remote agent manager by the global collector.
 * @author Daniel Moraru
 */
public class AgentPollBuffer implements Serializable {
	private static final long serialVersionUID = -3528131953658025671L;

	public final static class NonFatalError implements Serializable {
		private static final long serialVersionUID = 6290532096739678411L;
		public String fHost;
		public AgentId fAgentId;
		public Throwable fError;
		public NonFatalError(String host, AgentId id, Throwable error) {
			super();
			fHost = host;
			fAgentId = id;
			fError = error;
		}
	}
	public final static class StateChange implements Serializable {
		private static final long serialVersionUID = -2501608052648646021L;
		public String fHost;
		public AgentId fAgentId;
		public AgentState fAgentState;
		public Throwable fError;
		public StateChange(String host, AgentId agentId, AgentState state, Throwable e) {
			super();
			fHost = host;
			fAgentId = agentId;
			fAgentState = state;
			fError = e;
		}
	}
	public final static class EntitiesChange implements Serializable {
		private static final long serialVersionUID = 3880805261085665111L;
		public String fHost;
		public AgentId fAgentId;
		public EntityDescriptorTree fEntityTree;
		public EntitiesChange(String host, AgentId id, EntityDescriptorTree tree) {
			super();
			fHost = host;
			fAgentId = id;
			fEntityTree = tree;
		}
	}

	private AgentDataBuffer[][] fDataBuffers;
	private NonFatalError[] fNonFatalErrors;
	private StateChange[] fStateChanges;
	private EntitiesChange[] fEntitiesChanges;

	/**
	 * @param buffs
	 * @param nfe
	 * @param sc
	 * @param ec
	 */
	public AgentPollBuffer(AgentDataBuffer[][] buffs,
				NonFatalError[] nfe,
				StateChange[] sc,
				EntitiesChange[] ec) {
		super();
		fDataBuffers = buffs;
		fStateChanges = sc;
		fEntitiesChanges = ec;
		fNonFatalErrors = nfe;
	}

	/**
	 * @return
	 */
	public AgentDataBuffer[][] getDataBuffers() {
		return fDataBuffers;
	}

	/**
	 * @return
	 */
	public EntitiesChange[] getEntitiesChanges() {
		return fEntitiesChanges;
	}

	/**
	 * @return
	 */
	public NonFatalError[] getNonFatalErrors() {
		return fNonFatalErrors;
	}

	/**
	 * @return
	 */
	public StateChange[] getStateChanges() {
		return fStateChanges;
	}
}
