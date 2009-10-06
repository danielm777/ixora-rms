/*
 * Created on 17-Jun-2004
 */
package com.ixora.common.update.multinode;

import com.ixora.common.remote.ServiceInfo;
import com.ixora.common.remote.ServiceState;
import com.ixora.common.update.Module;
import com.ixora.common.update.UpdateManager;

/**
 * Update manager for a multi node system, i.e a system
 * with a network of remote nodes implementing <code>Updateable</code>.
 * @author Daniel Moraru
 */
public interface UpdateManagerMultiNode extends UpdateManager {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Update status.
		 * @param node
		 * @param ok
		 */
		void nodeUpdateStatus(String node, boolean ok);
		/**
		 * New node state.
		 * @param node
		 * @param state
		 */
		void nodeStateChanged(String node, ServiceState state);
	}
	/**
	 * @return the modules registered for remote nodes
	 */
	Module[] getRegisteredNodeModules();
	/**
	 * @return availability info on configured nodes
	 */
	ServiceInfo[] getNodesInfo();
	/**
	 * Adds nodes to be updated.
	 * @param nodes
	 */
	void addNodes(String[] nodes);
	/**
	 * Removes nodes to be updated.
	 * @param nodes
	 */
	void removeNodes(String[] nodes);
}
