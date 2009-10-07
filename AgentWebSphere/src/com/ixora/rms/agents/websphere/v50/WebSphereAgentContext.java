/*
 * Created on 04-Apr-2004
 */
package com.ixora.rms.agents.websphere.v50;

import java.rmi.RemoteException;

import com.ibm.websphere.pmi.PmiException;
import com.ixora.rms.EntityId;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;
import com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * Execution context for websphere.
 * @author Daniel Moraru
 */
interface WebSphereAgentContext extends AgentExecutionContext {
    /**
     * Reconnects to websphere
     * @throws Throwable
     * @throws InvalidConfiguration
     */
    void reconnect() throws InvalidConfiguration, Throwable;
    /**
	 * @return the current pmi client
	 */
	PmiClientProxy getClient();
	/**
	 * @return
	 * @throws PmiException
	 * @throws RemoteException
	 */
	PerfDescriptor[] getNodes() throws RemoteException, PmiException;
	/**
	 * @param node
	 * @return
	 * @throws RemoteException
	 * @throws PmiException
	 */
	PerfDescriptor[] getServers(String node) throws RemoteException, PmiException;
	/**
	 * Returns the translated name of the given
	 * name in the given module.
	 * @param text
	 * @param module
	 * @return
	 */
	String getTranslatedText(String text, String module);
	/**
	 * Returns data stored by the context for the specified entity.
	 * @param id
	 * @return
	 */
	EntityData getEntityData(EntityId id);
	/**
	 * Returns module data stored by the context.
	 * @param module
	 * @return
	 */
	ModuleData getModuleData(String module);
	/**
	 * Enables the given performance descriptor.
	 */
	void enablePerfDescriptor(EntityId eid, PerfDescriptor desc);
	/**
	 * Enables the given performance descriptor.
	 */
	void disablePerfDescriptor(EntityId eid, PerfDescriptor desc);
	/**
	 * Maps WAS monitoring levels to RMS levels.
	 * @param l
	 * @return
	 */
	MonitoringLevel mapLevel(int l);
	/**
	 * Maps RMS monitoring levels to WAS levels.
	 * @param l
	 * @return
	 */
	int mapLevel(MonitoringLevel l);
	/**
	 * Rebuild the info repository.
	 * @throws PmiException
	 * @throws RemoteException
	 */
	void rebuildInfoRepository() throws RemoteException, PmiException;

    /**
     * @return
     */
    RelatedVersionBehaviour getVersionBehaviour();
}
