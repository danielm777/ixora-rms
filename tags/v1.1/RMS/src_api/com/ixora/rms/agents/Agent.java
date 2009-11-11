/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.agents;

import com.ixora.common.Startable;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;

/**
 * Monitoring agent.<br>
 * Notes:
 * <p>
 * Many methods throw Throwable to make
 * agent classes less clutered with exception checking and wrapping
 * code. This exceptions will be handled inside the framework.
 * If agents need to throw a specific logical exception they should use
 * an instance of RMSException; any other type of exception will
 * be considered by the framework an internal application error
 * and it will be marked for an application eror report.
 * </p>
 * <p>
 * Any class initialization errors should be rethrown in the agent
 * constructor as this is the only way to inform the framwork of failure
 * at agent class levels.
 * </p>
 * <p>
 * Agent extends <code>Startable</code> to provide
 * hooks for agents that need to get their data asynchronously
 * or that aquire resources that must be released.
 * </p>
 * @author Daniel Moraru
 */
public interface Agent extends Startable {
	/**
	 * Agent listener.
	 */
	public interface Listener {
		/**
		 * Invoked when new monitoring data becomes
		 * available.
		 * @param data
		 */
		void receiveDataBuffer(AgentDataBuffer data);
		/**
		 * Invoked when the monitored entities changed.
		 * @param host
		 * @param agentId
		 * @param entities the new entity subtree
		 */
		void entitiesChanged(
						String host,
						AgentId agentId,
						EntityDescriptorTree entities);
		/**
		 * Invoked by the agent to notify of a non fatal error.
		 * @param host
		 * @param agentId
		 * @param t
		 */
		void nonFatalError(String host, AgentId agentId, Throwable t);
	}
	/**
	 * Sets the agent listener.
	 * @param l
	 */
	void setListener(Listener l);
	/**
	 * Sets the id for this agent instance.
	 * @param agentId
	 */
	void setId(AgentId agentId);
	/**
	 * Configures this monitoring agent.
	 * @param conf
	 * @return the entities affected by the new configuration
	 * @throws InvalidConfiguration
	 * @throws Throwable
	 */
	EntityDescriptorTree configure(AgentConfiguration conf)
		throws InvalidConfiguration, Throwable;
	/**
	 * Configures this monitoring agent.
	 * @param entity
	 * @param conf
	 * @return the entities affected by the new configuration
	 * @throws InvalidEntity
	 * @throws InvalidConfiguration
	 * @throws Throwable
	 */
	EntityDescriptorTree configureEntity(EntityId entity, EntityConfiguration conf)
		throws InvalidEntity, InvalidConfiguration, Throwable;
	/**
	 * Runs a data collection cycle. If an exception is thrown
	 * the agent is marked with an error flag and data collection
	 * stops. If an agent supports temporary errors during collection
	 * cycles then it should handle the necessary exceptions
	 * inside this method.
	 * @throws Throwable
	 */
	void collectData() throws Throwable;
	/**
	 * @param parent the parent entity
	 * @param recursive
	 * @param refresh
	 * @return the monitored children entities for the parent
	 * entity, <code>parent</code> is null for root entity;
	 * @throws InvalidEntity
	 * @throws Throwable
	 */
	EntityDescriptorTree getEntities(EntityId parent, boolean recursive, boolean refresh) throws InvalidEntity, Throwable;
	/**
	 * @return the current agent configuration
	 */
	AgentConfiguration getConfiguration();
	/**
	 * @return the agent descriptor
	 */
	AgentDescriptor getDescriptor();
	/**
	 * Deactivates the agents. This is where the agent implements cleanup operations.
	 * @throws Throwable
	 */
	void deactivate() throws Throwable;
	/**
	 * This method will be invoked after the first call to <code>configure(AgentConfiguration)</code>
	 * to allow the agent to use the hint in the configuration when returning this value.
	 * @return true if this agent required an external collector thread
	 */
	boolean requiresExternalCollector();
}
