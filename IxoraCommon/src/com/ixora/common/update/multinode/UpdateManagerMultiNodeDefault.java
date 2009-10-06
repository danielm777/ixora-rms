/*
 * Created on 17-Jun-2004
 */
package com.ixora.common.update.multinode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ixora.common.remote.RMIPingable;
import com.ixora.common.remote.ServiceInfo;
import com.ixora.common.remote.ServiceMonitor;
import com.ixora.common.remote.ServiceState;
import com.ixora.common.update.Module;
import com.ixora.common.update.ModuleUpdateDescriptor;
import com.ixora.common.update.UpdateManagerDefault;
import com.ixora.common.update.UpdateMgr;
import com.ixora.common.update.UpdatePartDescriptor;
import com.ixora.common.xml.exception.XMLException;

/**
 * UpdateManagerMultiNodeDefault.
 * @author Daniel Moraru
 */
public final class UpdateManagerMultiNodeDefault
	extends UpdateManagerDefault implements UpdateManagerMultiNode {
	/** Node modules */
	private Module[] registeredNodeModules;
	/** Service monitor for Updateable remote interfaces */
	private ServiceMonitor monitor;
	/** Listener */
	private Listener listener;
	/**
	 * Event handler.
	 */
	private final class EventHandler implements ServiceMonitor.Listener {
		/**
		 * @see com.ixora.common.remote.ServiceMonitor.Listener#serviceStateChanged(java.lang.String, int, com.ixora.common.remote.ServiceState, com.ixora.common.remote.ServiceState)
		 */
		public void serviceStateChanged(String host, int serviceID, ServiceState oldState, ServiceState newState) {
			listener.nodeStateChanged(host, newState);
		}
	}

	/**
	 * Constructor.
	 * @param factory
	 * @param listener
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws StartableError
	 */
	public UpdateManagerMultiNodeDefault(UpdateableFactory factory, Listener listener) throws URISyntaxException, IOException {
		super();
		if(listener == null) {
			throw new IllegalArgumentException("null listener");
		}
		this.registeredNodeModules = UpdateMgr.getRegisteredNodeModules();
		this.listener = listener;
		this.monitor = new ServiceMonitor(
				null,
				factory.getRMIPingableFactory(),
				3); // hard code the ping interval
		this.monitor.addListener(new EventHandler());
	}

	/**
	 * @see com.ixora.common.update.multinode.UpdateManagerMultiNode#getRegisteredNodeModules()
	 */
	public synchronized Module[] getRegisteredNodeModules() {
		return this.registeredNodeModules;
	}

	/**
	 * @see com.ixora.common.update.UpdateManager#getRegisteredModules()
	 */
	public Module[] getRegisteredModules() {
		Set ret = new HashSet();
		for(int i = 0; i < registeredModules.length; i++) {
			ret.add(registeredModules[i]);
		}
		for(int i = 0; i < registeredNodeModules.length; i++) {
			ret.add(registeredNodeModules[i]);
		}
		return (Module[])ret.toArray(new Module[ret.size()]);
	}

	/**
	 * @see com.ixora.common.update.UpdateManager#installAllUpdates()
	 */
	public synchronized void installAllUpdates() throws IOException,
			XMLException {
		// install local modules
		super.installAllUpdates();
		// install remote modules...
		Collection nodes = this.monitor.getHosts();
		Updateable updateable;
		RMIPingable pingable;
		String host;
		String os;
		for(Iterator iter = nodes.iterator(); iter.hasNext();) {
			host = (String)iter.next();
			pingable = (RMIPingable)this.monitor.getPingable(host);
			if(pingable == null) {
				listener.nodeUpdateStatus(host, false);
				continue;
			}
			updateable = (Updateable)pingable.getRemoteObject();
			try {
				os = updateable.getOsName();
				// get first the list of update part descriptors
				// for all node modules
				ModuleUpdateDescriptor mud;
				for(int i = 0; i < this.registeredNodeModules.length; i++) {
					mud = getLatestUpdate(this.registeredModules[i]);
					UpdatePartDescriptor[] upd = mud.getComponents(os);

				}
				updateable.update(null);
				listener.nodeUpdateStatus(host, true);
			} catch(Exception e) {
				listener.nodeUpdateStatus(host, false);
			}
		}
	}

	/**
	 * @see com.ixora.common.update.multinode.UpdateManagerMultiNode#getNodesInfo()
	 */
	public ServiceInfo[] getNodesInfo() {
		return this.monitor.getServicesStates();
	}

	/**
	 * @see com.ixora.common.update.multinode.UpdateManagerMultiNode#addNodes(java.lang.String[])
	 */
	public synchronized void addNodes(String[] nodes) {
		this.monitor.addHosts(Arrays.asList(nodes), false);
	}

	/**
	 * @see com.ixora.common.update.multinode.UpdateManagerMultiNode#removeNodes(java.lang.String[])
	 */
	public synchronized void removeNodes(String[] nodes) {
		this.monitor.removeHosts(Arrays.asList(nodes));
	}
}