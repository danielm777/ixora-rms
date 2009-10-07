/*
 * Created on 02-Apr-2005
 */
package com.ixora.rms.agents.websphere.v50;

import java.rmi.RemoteException;

import com.ibm.websphere.pmi.PmiException;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;

/**
 * @author Daniel Moraru
 */
public class WebSphereEntityServer extends WebSphereEntity {

	/**
	 * Constructor.
	 * @param parent
	 * @param desc
	 * @param useDataInDescriptor
	 * @param c
	 * @throws Throwable
	 */
	public WebSphereEntityServer(Entity parent, PerfDescriptor desc,
			boolean useDataInDescriptor, WebSphereAgentContext c)
			throws Throwable {
		super(parent, desc, useDataInDescriptor, c);
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.WebSphereEntity#changeWASMonitoringLevel(int, boolean)
	 */
	protected void changeWASMonitoringLevel(int waslevel, boolean recursive)
			throws RemoteException, PmiException {
		if(getWASContext().getVersionBehaviour().invokeSetInstrumentationLevel()) {
			getClient().setInstrumentationLevel(descriptor.getNodeName(),
					descriptor.getServerName(),
					null,
					waslevel,
					recursive);
		}
	}

	/**
	 * @param pd
	 * @return
	 */
	public static String getEntityNameFromDescriptor(PerfDescriptor pd) {
		return pd.getServerName();
	}

	/**
	 * @param pd
	 * @return
	 */
	public static EntityId getEntityIdFromDescriptor(PerfDescriptor pd) {
		return new EntityId(new String[] {"root", pd.getNodeName(), pd.getServerName()});
	}
}
