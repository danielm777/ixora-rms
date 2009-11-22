/*
 * Created on 20-May-2004
 */
package com.ixora.rms.agents.websphere.v50;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.websphere.pmi.PmiConstants;
import com.ibm.websphere.pmi.PmiException;
import com.ibm.websphere.pmi.client.CpdCollection;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityId;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.RootEntity;
import com.ixora.rms.agents.websphere.exception.ServerFailedToReturnData;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * @author Daniel Moraru
 */
public class WebSphereRootEntity extends RootEntity implements PmiConstants {
	/** Last discovered nodes */
	private PerfDescriptor[] nodes;
	private Map<String, PerfDescriptor[]> servers;
	private boolean fDisconnected;

	/**
	 * @param ctxt
	 * @throws Throwable
	 */
	public WebSphereRootEntity(AgentExecutionContext ctxt) throws Throwable {
		super(ctxt);
		servers = new HashMap<String, PerfDescriptor[]>();
	}

	/**
	 * @see com.ixora.rms.agents.impl.EntityTree#updateChildrenEntities()
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		if(fDisconnected) {
			// try to reconnect
			((WebSphereAgentContext)fContext).reconnect();
			fDisconnected = false;
		}
		loadNodeDescriptors();
		loadServerDescriptors();

		if(Utils.isEmptyArray(nodes)) {
			return;
		}

		for(int i = 0; i < nodes.length; i++) {
			PerfDescriptor pd = nodes[i];
			if(pd.getType() != TYPE_DATA) {
				// update children
				EntityId eid = WebSphereEntity.createEntityId(this, pd);
				WebSphereEntity ce = (WebSphereEntity)fChildrenEntities.get(eid);
				if(ce == null) {
					addChildEntity(createEntityNode(pd));
				} else {
					// update
					ce.update(pd, false);
				}
			}
		}
		if(recursive) {
			for(Entity child : fChildrenEntities.values()) {
				((WebSphereEntity)child).updateChildrenEntities(true);
			}
		}
	}

	/**
	 * This is called by the agent when it's monitoring level has changed.
	 * @throws Throwable
	 * @throws InvalidConfiguration
	 */
	public void changeMonitoringLevel(MonitoringLevel level) throws InvalidConfiguration, Throwable {
		for(Iterator<Entity> iter = fChildrenEntities.values().iterator(); iter.hasNext();) {
			EntityConfiguration nConf = new EntityConfiguration();
			nConf.setMonitoringLevel(level);
			nConf.setRecursiveMonitoringLevel(true);
			((WebSphereEntity)iter.next()).configure(nConf);
		}
	}

	/**
	 * Invoked when a new sample has been retrieved from WAS.
	 * @param data nust be not null
	 * @throws Throwable
	 */
	public void dataAvailable(CpdCollection[] data) throws Throwable {
		for(int i = 0; i < data.length; i++) {
			CpdCollection col = data[i];
            if(col == null) {
                fContext.error(new ServerFailedToReturnData(new Date()));
                fDisconnected = true;
                break;
            }
			EntityId eid = WebSphereEntity.getEntityIdFromDescriptor(col.getDescriptor());
			WebSphereEntity wasen = (WebSphereEntity)findEntity(eid, false);
			wasen.dataAvailable(col);
		}
	}

    /**
     * @param pd
     * @return
     * @throws Throwable
     */
    protected WebSphereEntityNode createEntityNode(PerfDescriptor pd) throws Throwable {
        return new WebSphereEntityNode(this, pd, true, (WebSphereAgentContext)fContext);
    }

	/**
	 * @return
	 * @throws PmiException
	 * @throws RemoteException
	 */
	public PerfDescriptor[] getPerfDescriptorsForNodes() throws RemoteException, PmiException {
		return nodes;
	}

	/**
	 * @param nodeName
	 * @throws PmiException
	 * @throws RemoteException
	 */
	public PerfDescriptor[] getPerfDescriptorsForServers(String nodeName) throws RemoteException, PmiException {
		if(Utils.isEmptyArray(nodes)) {
			return null;
		}
		return servers.get(nodeName);
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#isLazilyLoadingEntityTree()
	 */
	public boolean isLazilyLoadingEntityTree() {
		return false;
	}

	//	 package access
	/**
	 * Will intialize all nodes and their servers.
	 * @throws Throwable
	 */
	void initNodesAndServers() throws Throwable {
		if(fChildrenEntities.size() > 0) {
			return;
		}

		// load the entire tree at startup to improve performance
		updateChildrenEntities(true);
	}

	/**
	 * @param disconnected
	 */
	void setDisconnected(boolean disconnected) {
		fDisconnected = true;
	}

	/**
	 * Loads perf descriptors for nodes.
	 * @throws PmiException
	 * @throws RemoteException
	 */
	private void loadNodeDescriptors() throws RemoteException, PmiException {
		nodes = ((WebSphereAgentContext)fContext).getClient().listNodes();
	}

	/**
	 * Loads perf descriptors for nodes.
	 * @throws PmiException
	 * @throws RemoteException
	 */
	private void loadServerDescriptors() throws RemoteException, PmiException {
		if(Utils.isEmptyArray(nodes)) {
			return;
		}
		for(PerfDescriptor pd : nodes) {
			PerfDescriptor[] ret = ((WebSphereAgentContext)fContext).getClient().listServers(pd);
			servers.put(pd.getNodeName(), ret);
		}
	}
}
