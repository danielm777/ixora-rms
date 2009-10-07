/*
 * Created on 02-Apr-2005
 */
package com.ixora.rms.agents.websphere.v50;

import java.rmi.RemoteException;
import java.util.Iterator;

import com.ibm.websphere.pmi.PmiException;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;

/**
 * @author Daniel Moraru
 */
public class WebSphereEntityNode extends WebSphereEntity {

	/**
	 * Constructor.
	 * @param parent
	 * @param desc
	 * @param useDataInDescriptor
	 * @param c
	 * @throws Throwable
	 */
	public WebSphereEntityNode(Entity parent, PerfDescriptor desc, boolean useDataInDescriptor, WebSphereAgentContext c) throws Throwable {
		super(parent, desc, useDataInDescriptor, c);
	}

	/**
	 * @throws RemoteException
	 * @throws PmiException
	 */
	protected void build(boolean useDataInDescriptor) throws RemoteException, PmiException {
		if(descriptor.getType() == TYPE_NODE) {
			// set the level to the level of the agent
			this.fConfiguration.setMonitoringLevel(fContext.getAgentConfiguration().getMonitoringLevel());
		}
		super.build(useDataInDescriptor);
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.WebSphereEntity#getWASMembers(boolean)
	 */
	protected PerfDescriptor[] getWASMembers(boolean recursive) throws RemoteException, PmiException {
		// get members, list servers
		PerfDescriptor[] servers = getWASContext().getClient().listServers(descriptor);
		if(recursive) {
			// prepare recursive subtree
			if(!Utils.isEmptyArray(servers)) {
				for(PerfDescriptor server : servers) {
					server.setChildren(getClient().listMembers(server, true));
				}
			}
		}
		return servers;
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.WebSphereEntity#createChild(com.ixora.rms.agents.websphere.v501.proxy.PerfDescriptor, boolean, com.ixora.rms.agents.websphere.v50.WebSphereAgentContext)
	 */
	protected WebSphereEntity createChild(
			PerfDescriptor pd, boolean useDataFromDescriptor,
			WebSphereAgentContext context) throws Throwable {
		return new WebSphereEntityServer(this, pd, useDataFromDescriptor, context);
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.WebSphereEntity#changeWASMonitoringLevel(int, boolean)
	 */
	protected void changeWASMonitoringLevel(int waslevel, boolean recursive)
			throws RemoteException, PmiException {
		for(Iterator iter = fChildrenEntities.values().iterator(); iter.hasNext();) {
			PerfDescriptor pd = ((WebSphereEntity)iter.next()).descriptor;
			try {
				if(getWASContext().getVersionBehaviour().invokeSetInstrumentationLevel()) {
					// catch exceptions as some servers might be stopped
					getClient().setInstrumentationLevel(
								pd.getNodeName(),
								pd.getServerName(),
								null,
								waslevel,
								true);
				}
			} catch(Exception e) {
				fContext.error(e);
			}
		}
	}

	/**
	 * @param pd
	 * @return
	 */
	public static String getEntityNameFromDescriptor(PerfDescriptor pd) {
		return pd.getNodeName();
	}

	/**
	 * @param pd
	 * @return
	 */
	public static EntityId getEntityIdFromDescriptor(PerfDescriptor pd) {
		return new EntityId(new String[] {"root", pd.getNodeName()});
	}

	/**
	 * @return
	 * @throws PmiException
	 * @throws RemoteException
	 */
	public PerfDescriptor[] getPerfDescriptorsForServers() throws RemoteException, PmiException {
		return getWASContext().getClient().listServers(descriptor);
	}
}
