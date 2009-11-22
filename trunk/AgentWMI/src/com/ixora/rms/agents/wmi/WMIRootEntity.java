/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.wmi;

import java.util.LinkedList;
import java.util.List;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.RootEntity;
import com.ixora.rms.agents.wmi.exceptions.WMIAgentException;
import com.ixora.rms.agents.wmi.exceptions.WMINotSupportedException;
import com.ixora.rms.agents.wmi.messages.Msg;
import com.ixora.rms.data.CounterValue;

/**
 * WMIRootEntity
 * Arranges WMI classes and instances on multiple levels:
 * 1. first levels (variable number) are just for grouping since there
 * are a lot of classes. The groups are known and are static.
 * 2. WMI classes go on the following level, under groups. Also static.
 * 3. WMI instances for each class are on the next level. They are dynamic
 * and will be retrieved when the above entity (class level) is expanded. At
 * the same time their properties will be retrieved and converted into counters
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class WMIRootEntity extends RootEntity {
	private static final long serialVersionUID = -3084319062930795149L;

	/** These are native methods which collect and return WMI information. */
	private native int ConnectServer(String serverAndNamespace,
			String userName, String password) throws WMIAgentException;
	private native String[] GetClassInstances(
			int nativeHandle, String className) throws WMIAgentException;
//	private native NativeWMIRelationship[] GetAssociatedInstances(
//			int nativeHandle, String className, String escapedInstanceName)
//		throws WMIAgentException;
	private native NativeWMIProperty[] GetClassProperties(
			int nativeHandle, String className) throws WMIAgentException;
	private native CounterValue[] GetCounterValues(int nativeHandle,
			String className, String instanceName, String[] counterNames)
		throws WMIAgentException;
	private native void CloseHandle(int nativeHandle) throws WMIAgentException;

	/** Used to communicate with the native code */
	private int fNativeHandle;

	/**
	 * Stores the value extracted from a property of a WMI instance
	 */
	private class WMICounter extends Counter {
		private static final long serialVersionUID = 7079492374390043557L;

		/**
		 * @param id
		 * @param description
		 * @param type
		 */
		public WMICounter(String id, String description, CounterType type) {
			super(id, description, type);
		}

		/**
		 * Updates counter details in place
		 * @param description
		 * @param type
		 */
		public void updateData(String description, CounterType type) {
			this.fDescription = description;
			this.fType = type;
		}
	}

	/**
	 * WMIEntity
	 * Base class for entities on all levels
	 */
	private abstract class WMIEntity extends Entity {
		private static final long serialVersionUID = 6273568719569661200L;

		/**
		 * Constructor
		 */
		public WMIEntity(EntityId entityId, AgentExecutionContext ctx) {
			super(entityId, ctx);
		}

		/**
		 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
		 */
		protected void retrieveCounterValues() throws Throwable {
			; // nothing by default, only the last level has values
		}

	}

	/**
	 * WMIGroupEntity
	 * Appears on the first level and is a static entity which groups
	 * more classes under it
	 */
	private class WMIGroupEntity extends WMIEntity {
		private static final long serialVersionUID = 8765598337056977972L;

		/**
		 * Constructor
		 * @param parentEntity
		 * @param pathComponents
		 * @param pathIndex
		 * @param ctx
		 * @throws Throwable
		 */
		public WMIGroupEntity(EntityId parentEntity, String[] pathComponents,
				int pathIndex, AgentExecutionContext ctx) throws Throwable {
			super(new EntityId(parentEntity, pathComponents[pathIndex]), ctx);
			if(pathIndex == 0) {
				fSafeToRefreshRecursivelly = false;
			}
			updateData(pathComponents, pathIndex);
		}

		/**
		 * Changes the hierarchy of entities under this one
		 */
		public void updateData(String[] pathComponents, int pathIndex) throws Throwable {
			setTouchedByUpdate(true);
			// If next is the last entity, create a WMIClassEntity from it
			if (pathIndex+1 >= pathComponents.length-1) {
				addChildEntity(new WMIClassEntity(this.getId(),
						pathComponents[pathComponents.length-1], fContext));
			} else {
				// Just keep creating WMIGroupEntities until the last one
				WMIGroupEntity oldEntity = (WMIGroupEntity)getChildEntity(
						new EntityId(this.getId(), pathComponents[pathIndex + 1]));
				if (oldEntity == null) {
					addChildEntity(new WMIGroupEntity(this.getId(),
							pathComponents, pathIndex + 1, fContext));
				} else {
					oldEntity.updateData(pathComponents, pathIndex + 1);
				}
			}
		}

		/**
		 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
		 */
		public void updateChildrenEntities(boolean recursive) throws Throwable {
			if(recursive) {
				for(Entity childEntity : fChildrenEntities.values()) {
					childEntity.updateChildrenEntities(recursive);
				}
			}
		}
	}

	/**
	 * WMIClassEntity
	 * Defines a real WMI class, which will be populated with its instances
	 */
	private class WMIClassEntity extends WMIEntity {
		private static final long serialVersionUID = 3669356819876035736L;
		private String fClassName;

		/**
		 * Constructor
		 * @param parentEntity
		 * @param name
		 * @param ctx
		 */
		public WMIClassEntity(EntityId parentEntity, String name, AgentExecutionContext ctx) {
			super(new EntityId(parentEntity, name), ctx);
			this.fClassName = name;
//			if (name.equals("Win32_PerfFormattedData_PerfProc_Process")) {
//				int a = 0;
//			}
			this.fHasChildren = true;
		}

		/**
		 * @return the class name
		 */
		public String getClassName() {
			return fClassName;
		}

		/**
		 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
		 */
		public void updateChildrenEntities(boolean recursive) throws Throwable {
			resetTouchedByUpdateForChildren();
			// Generate children from class instances
			// 1. call GetClassProperties to retrieve the list of counters
			// (all instances will have the same counters)
			NativeWMIProperty[] classProperties = GetClassProperties(
					fNativeHandle, getClassName());

			// 2. call GetClassInstances to retrieve all instances and
			// create child entities
			String[] classInstances = GetClassInstances(
					fNativeHandle, getClassName());

			// 3. Create WMIInstanceEntity from all instances. All will share
			// the same counter names and types.
			for (String instanceName : classInstances) {

				if (!Utils.isEmptyString(instanceName)) {
					WMIInstanceEntity oldEntity = (WMIInstanceEntity)getChildEntity(
							new EntityId(this.getId(), instanceName));
					if (oldEntity == null) {
						addChildEntity(new WMIInstanceEntity(this.getId(),
								getClassName(), instanceName, fContext, classProperties));
					} else {
						oldEntity.updateData(getClassName(), instanceName, classProperties);
					}
				}
			}
			removeStaleChildren();
			if (recursive) {
				for (Entity childEntity : fChildrenEntities.values()) {
					childEntity.updateChildrenEntities(recursive);
				}
			}
		}


	}

	/**
	 * WMIInstanceEntity
	 * Holds the actual counters and their values
	 */
	private class WMIInstanceEntity extends WMIEntity {
		private static final long serialVersionUID = 5039817860198717349L;
		private String fWMIClassName;
		private String fWMIInstanceName;

		/**
		 * Constructor
		 * @param parentEntity
		 * @param name
		 * @param ctx
		 * @param props
		 */
		public WMIInstanceEntity(EntityId parentEntity,
				String className, String instanceName,
				AgentExecutionContext ctx, NativeWMIProperty[] props) {
			super(new EntityId(parentEntity, instanceName), ctx);
			updateData(className, instanceName, props);
		}

		/**
		 * Updates the node in place
		 * @param className
		 * @param instanceName
		 * @param properties
		 */
		public void updateData(String className, String instanceName, NativeWMIProperty[] props) {
			setTouchedByUpdate(true);
			this.fWMIClassName = className;
			this.fWMIInstanceName = instanceName;

			// Create counters from class properties
			for (NativeWMIProperty property : props) {
				WMICounter oldCounter = (WMICounter)getCounter(new CounterId(property.getName()));
				if (oldCounter == null) {
					addCounter(new WMICounter(property.getName(),
							property.getDescription(), property.getType()));
				} else {
					oldCounter.updateData(property.getDescription(), property.getType());
				}
			}
		}

		/**
		 * @return Returns the WMI Class.
		 */
		public String getWMIClass() {
			return fWMIClassName;
		}

		/**
		 * @return Returns the WMI Instance.
		 */
		public String getWMIInstance() {
			return fWMIInstanceName;
		}

		/**
		 * @see com.ixora.rms.agents.wmi.WMIRootEntity.WMIEntity#retrieveCounterValues()
		 */
		protected void retrieveCounterValues() throws Throwable {
			// Make a list of counter names
			List<String> counterNames = new LinkedList<String>();
			for (Counter counter : fCounters.values()) {
				if (counter.isEnabled()) {
					counterNames.add(counter.getName());
				}
			}

			// Call native code to get counter values
			CounterValue[] counterValues = GetCounterValues(
					fNativeHandle, getWMIClass(), getWMIInstance(),
					counterNames.toArray(new String[counterNames.size()]));

			// Sanity check
			if(counterValues.length != counterNames.size()) {
				throw new WMIAgentException(Msg.WMI_INTERNAL_ERROR);
			}

			// Place values into appropriate counters
			int counterIndex = 0;
			for (String counterName : counterNames) {
				Counter counter = getCounter(new CounterId(counterName));
				if (counter != null) {
					counter.dataReceived(counterValues[counterIndex]);
				}
				counterIndex++;
			}

			super.retrieveCounterValues();
		}

		/**
		 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
		 */
		public void updateChildrenEntities(boolean recursive) throws Throwable {
			// WMI seems to eat 100% CPU when asked to do this. Disable for now.
			/*
			// Get all related instances
			// The instance name is escaped here because the code is simpler
			NativeWMIRelationship[] related = GetAssociatedInstances(
					fNativeHandle, getWMIClass(), escape(getWMIInstance()));

			// Create WMIInstanceEntity from all instances. All will share
			// the same counter names and types.
			for (NativeWMIRelationship rel : related) {
				// Get properties for the related class
				NativeWMIProperty[] classProperties = GetClassProperties(
						fNativeHandle, rel.getWMIClass());

				// Create the related instance as a subentity
				addChildEntity(new WMIInstanceEntity(this.getId(),
						rel.getWMIClass(), rel.getWMIInstance(), fContext,
						classProperties));
			}
			*/

			if(recursive) {
				for(Entity childEntity : fChildrenEntities.values()) {
					childEntity.updateChildrenEntities(recursive);
				}
			}
		}


	}

	/**
	 * Constructor, throws exception if cannot load DLL for its
	 * native methods.
	 * @param ctxt
	 */
	public WMIRootEntity(AgentExecutionContext ctxt) throws WMIAgentException {
		super(ctxt);
		fSafeToRefreshRecursivelly = false;

		// Load native DLL;if not available then this agent
		// is not supported on this platform.
		try {
			System.loadLibrary("wmi");
		} catch (Throwable e) {
			throw new WMINotSupportedException(e);
		}
	}

	/**
	 * Closes the connection to native code
	 */
	public void cleanup() throws WMIAgentException {
		CloseHandle(fNativeHandle);
	}

	/**
	 * @return the input string escaped for including in a SQL string
	 */
//	private String escape(String in) {
//		return Utils.replace(Utils.replace(in, "'", "\\'"), "\\", "\\\\");
//	}

	/**
	 * @see com.ixora.rms.agents.impl.RootEntity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		// Create group entities
		if(this.getChildrenCount() == 0) {
			// Connect to WMI server or throw exception here
			String monitoredHost = fContext.getAgentConfiguration().getMonitoredHost();
	        Configuration cfg = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
	        String userName = cfg.getString(Configuration.USERNAME);
	        String password = cfg.getString(Configuration.PASSWORD);
	        String namespace = "root\\cimv2";//cfg.getString(Configuration.NAMESPACE);
	        String serverAndNamespace = namespace;
			if (!Utils.isLocalHost(monitoredHost))
				serverAndNamespace = "\\\\" + monitoredHost + "\\" + namespace;

			fNativeHandle = ConnectServer(serverAndNamespace, userName, password);

			// For each namespace there may be a well known grouping hierarchy.
			// For some namespaces there is no grouping, so we just enumerate classes
			String[] wmiStructure = HierarchyDefinition.getWMIStructure(namespace);
			if (wmiStructure != null) {
				for (String group : wmiStructure) {
					String[] pathComponents = group.split("/");
					if (pathComponents.length == 1) {
						// Top level entity, no groups above it
						addChildEntity(new WMIClassEntity(getId(), pathComponents[0],
								this.fContext));
					} else if (pathComponents.length > 1) {
						// Normal grouping
						WMIGroupEntity oldEntity = (WMIGroupEntity)getChildEntity(
								new EntityId(getId(), pathComponents[0]));
						if (oldEntity == null) {
							addChildEntity(new WMIGroupEntity(getId(),
									pathComponents, 0, this.fContext));
						} else {
							oldEntity.updateData(pathComponents, 0);
						}
					}
				}
			}
		}

		if (recursive) {
			for (Entity childEntity : fChildrenEntities.values()) {
				childEntity.updateChildrenEntities(recursive);
			}
		}
	}
}
