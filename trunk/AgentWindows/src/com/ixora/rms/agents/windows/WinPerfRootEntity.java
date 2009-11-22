package com.ixora.rms.agents.windows;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.RootEntity;
import com.ixora.rms.agents.windows.exception.WinPerfException;
import com.ixora.rms.agents.windows.exception.WinPerfNotSupportedException;
import com.ixora.rms.data.CounterValue;

/**
 * WinPerfRootEntity Main class responsible with retrieving object and instance
 * data from the native DLL and organizing it into two levels of entities: first
 * level contains objects and second contains instances. If an object has a
 * _Total instance, it is removed and it's counters are moved to the object
 * entity.
 * 
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class WinPerfRootEntity extends RootEntity {
	private static final long serialVersionUID = -3765412206782241384L;

	protected static native NativeObject[] EnumEntities(String machine,
			String userName, String domain, String password)
			throws WinPerfException;

	protected static native CounterValue[] CollectQueryData(int nativeQuery,
			int[] nativeHandles, int[] formats) throws WinPerfException;

	protected static native int[] SetCounters(String machine, int nativeQuery,
			String[] counters) throws WinPerfException;

	protected static native int ResetQuery(int[] oldNativeHandles,
			int oldNativeQuery) throws WinPerfException;

	/**
	 * Builds a string that Windows can understand, identifying a counter and
	 * its parent performance object and instance.
	 * 
	 * Syntax: [\\machine]\object[([parent/]instance[#index])]\counter
	 */
	public static String makeNativePath(String machine, String object,
			String instance, int instanceIndex, String counter) {
		StringBuffer retVal = new StringBuffer();
		if (machine != null) {
			retVal.append(machine);
		}
		retVal.append("\\").append(object);
		if (!Utils.isEmptyString(instance)) {
			retVal.append("(").append(instance);
			// the code below is done in the constructor of NativeInstance
			// if (instanceIndex != -1)
			// retVal += "#" + instanceIndex;
			retVal.append(")");
		}
		retVal.append("\\").append(counter);
		return retVal.toString();
	}

	/**
	 * Machine being monitored, or null for local machine.
	 */
	protected String machine;

	/**
	 * Constructor, throws exception if cannot load DLL for its native methods.
	 * 
	 * @param ctxt
	 */
	public WinPerfRootEntity(AgentExecutionContext ctxt)
			throws WinPerfNotSupportedException {
		super(ctxt);

		// Load native DLL;if not available then this agent
		// is not supported on this platform.
		try {
			System.loadLibrary("winperf");
		} catch (Throwable e) {
			throw new WinPerfNotSupportedException(e);
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		// Some systems don't allow connecting to remote registry, so if this is
		// the local machine get rid of the host name (null = local)
		String monitoredHost = fContext.getAgentConfiguration()
				.getMonitoredHost();
		if (!Utils.isLocalHost(monitoredHost)) {
			machine = "\\\\" + monitoredHost;
		} else {
			machine = null;
		}

		// Details for connection to remote server, if requires a password
		Configuration cfg = (Configuration) fContext.getAgentConfiguration()
				.getAgentCustomConfiguration();
		String userName = cfg.getString(Configuration.USERNAME);
		String password = cfg.getString(Configuration.PASSWORD);
		String domain = cfg.getString(Configuration.DOMAIN);

		// Create the first level of entities: performance objects
		try {
			resetTouchedByUpdateForChildren();
			NativeObject[] objects = EnumEntities(machine, userName, domain,
					password);
			for (int i = 0; i < objects.length; i++) {
				NativeObject object = objects[i];
				if (object != null) {
					WinPerfEntity topLevel = (WinPerfEntity) fChildrenEntities
							.get(createEntityId(fEntityId, object));
					if (topLevel == null) {
						// Create a new one
						addChildEntity(new WinPerfEntity(this.machine,
								fEntityId, objects[i], fContext));
					} else {
						// Update children of an existing one
						topLevel.updateObject(object);
					}
				}
			}
			removeStaleChildren();

			if (recursive) {
				for (Entity child : fChildrenEntities.values()) {
					child.updateChildrenEntities(true);
				}
			}
		} catch (Throwable t) {
			// fire a non fatal error, just to inform the console that
			// something it's not right...
			fContext.error(t);
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.EntityForest#testForChildren()
	 */
	protected boolean testForChildren() {
		// This root entity always has children
		return true;
	}

	/**
	 * @param parent
	 * @param no
	 * @return an entity id that corresponds to the given native object and
	 *         parent
	 */
	private static EntityId createEntityId(EntityId parent, NativeObject no) {
		return new EntityId(parent, no.name);
	}
}
