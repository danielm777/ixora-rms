/*
 * Created on 03-Apr-2004
 */
package com.ixora.rms.agents.websphere.v50;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.websphere.pmi.PerfModules;
import com.ibm.websphere.pmi.PmiConstants;
import com.ibm.websphere.pmi.PmiDataInfo;
import com.ibm.websphere.pmi.PmiException;
import com.ibm.websphere.pmi.client.CpdCollection;
import com.ibm.websphere.pmi.client.CpdData;
import com.ibm.websphere.pmi.client.PerfLevelSpec;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;
import com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy;
import com.ixora.rms.data.CounterValueDouble;

/**
 * @author Daniel Moraru
 */
public class WebSphereEntity extends Entity implements PmiConstants {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(WebSphereEntity.class);
	/** Performace descriptor */
	protected PerfDescriptor descriptor;
	/** Parent */
	protected Entity parent;
	/** Cached members */
	protected PerfDescriptor[] members;
	/** Last data received from WAS */
	protected CpdCollection data;
	/** Counters by WAS id */
	protected Map<Integer, WebSphereCounter> countersByIds;

	/**
	 * @param parent
	 * @param desc
	 * @param c
	 */
	public WebSphereEntity(Entity parent, PerfDescriptor desc, boolean useDataInDescriptor, WebSphereAgentContext c) throws Throwable {
		super(new EntityId(parent.getId(), getEntityNameFromDescriptor(desc)),
				c.getTranslatedText(desc.getName(), desc.getModuleName()), // alternate name
				c.getTranslatedText(desc.getName(), desc.getModuleName()),
				c);
		this.parent = parent;
		this.descriptor = desc;
		this.fSupportedLevels = new MonitoringLevel[]{
				MonitoringLevel.LOW,
				MonitoringLevel.MEDIUM,
				MonitoringLevel.HIGH,
				MonitoringLevel.MAXIMUM};
		this.countersByIds = new HashMap<Integer, WebSphereCounter>();
		build(useDataInDescriptor);
		if(useDataInDescriptor) {
			// build children recursivelly
			if(!Utils.isEmptyArray(members)) {
				for(PerfDescriptor member : members) {
					// filter out counters
					if(member.getType() != TYPE_DATA) {
						if(getWASContext().getVersionBehaviour().acceptPerfDescriptorAsEntity(member)) {
							addChildEntity(createChild(member, useDataInDescriptor, c));
						}
					}
				}
			}
		}
	}

	/**
	 * Override the default behaviour to improve performance as websphere
	 * loads the entire entity tree at startup.
	 * @see com.ixora.rms.agents.impl.Entity#isLazilyLoadingEntityTree()
	 */
	public boolean isLazilyLoadingEntityTree() {
		return false;
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		if(data == null) {
			// reset enabled counter values
			for(Iterator<Counter> iter = fCounters.values().iterator(); iter.hasNext();) {
				Counter c = iter.next();
				if(c.isEnabled()) {
					c.reset();
				}
			}
			return;
		}
		try {
			CpdData[] ds = data.dataMembers();
			if(ds == null) {
				if(logger.isTraceEnabled()) {
					logger.error("Entity " + fEntityId + " has no websphere data");
				}
				return;
			}
			for(int i = 0; i < ds.length; i++) {
				CpdData d = ds[i];
				// iterate for id
				WebSphereCounter c = (WebSphereCounter)this.countersByIds.get(new Integer(d.getId()));
				if(c != null && c.isEnabled()) {
					c.dataReceived(new CounterValueDouble(d.getValue().getValue()));
				}
			}
		} finally {
			data = null;
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		// rebuild info repository to get the latest settings (monitoring level)
		getWASContext().rebuildInfoRepository();

		descriptor.setChildren(getWASMembers(recursive));

		// if recursive, data for subtree elements will be found
		// in the perf descriptors for members retrieved above
		updateTree(descriptor, recursive);
	}

	/**
	 * Updates the subtree from recursive perf descriptor.
	 * @throws Throwable
	 */
	protected void updateTree(PerfDescriptor perfDesc, boolean useDataFromDescriptor) throws Throwable {
		// update descriptor data
		update(perfDesc, useDataFromDescriptor);
		// then update the children and their descriptors
		if(!Utils.isEmptyArray(members)) {
			for(PerfDescriptor pd : members) {
				if(pd.getType() != TYPE_DATA) {
					// update children
					EntityId eid = createEntityId(this, pd);
					WebSphereEntity ce = (WebSphereEntity)fChildrenEntities.get(eid);
					if(ce == null) {
						if(getWASContext().getVersionBehaviour().acceptPerfDescriptorAsEntity(pd)) {
							addChildEntity(createChild(pd, useDataFromDescriptor, (WebSphereAgentContext)fContext));
						}
					} else {
						// ask child to update its descriptor
						ce.updateTree(pd, useDataFromDescriptor);
					}
				}
			}
		}
	}

	/**
	 * Allows subclasses to override the type of child.
	 * @param pd
	 * @param useDataFromDescriptor
	 * @param context
	 * @return
	 * @throws Throwable
	 */
	protected WebSphereEntity createChild(PerfDescriptor pd, boolean useDataFromDescriptor, WebSphereAgentContext context) throws Throwable {
		return new WebSphereEntity(this, pd, useDataFromDescriptor, context);
	}

	/**
	 * @return the current pmi client
	 */
	protected PmiClientProxy getClient() {
		return ((WebSphereAgentContext)fContext).getClient();
	}

	/**
	 * @return the current pmi client
	 */
	protected WebSphereAgentContext getWASContext() {
		return (WebSphereAgentContext)fContext;
	}

	/**
	 * @throws RemoteException
	 * @throws PmiException
	 */
	protected void build(boolean useDataInDescriptor) throws RemoteException, PmiException {
		WebSphereAgentContext wasctxt = getWASContext();
		// cache WAS members, this includes was performance descriptors for
		// both children entities as well as counters
		retrieveWASMembers(useDataInDescriptor);
		// complete counter info with data from context
		EntityData edata = wasctxt.getEntityData(fEntityId);
		if(edata != null) {
			// set the initial configuration
			this.fConfiguration.setMonitoringLevel(edata.level);
			buildOrUpdateCounters(edata);
		}
	}

	/**
	 * Retrieves the WAS members of this entity. In this method <code>hasChildren</code>
	 * is set.
	 * @param pd if not null it will use info from this descriptor, otherwise
	 * calls to the pmi client will be made
	 * @param useDataInPerfDesc
	 * @throws PmiException
	 * @throws RemoteException
	 */
	private void retrieveWASMembers(boolean useDataInDescriptor) throws RemoteException, PmiException {
		WebSphereAgentContext wasctxt = getWASContext();
		// get members
		if(!useDataInDescriptor) {
			// if the server is not started, ignore it
			// as it will cause errors if left in the entity tree
			try {
				members = getWASMembers(false);
			} catch(Throwable t) {
				fContext.error(t);
			}
		} else {
			members = descriptor.getChildren();
		}

		// analyse members
		analyseWASMembers();
	}

	/**
	 * @return the was perf descriptors for children.
	 * @param recursive
	 * @throws PmiException
	 * @throws RemoteException
	 */
	protected PerfDescriptor[] getWASMembers(boolean recursive) throws RemoteException, PmiException {
		return getClient().listMembers(descriptor, recursive);
	}

	/**
	 *
	 */
	protected void analyseWASMembers() {
		if(members != null && members.length > 0) {
			for(int i = 0; i < members.length; i++) {
				PerfDescriptor desc = members[i];
				if(desc.getType() != TYPE_DATA) {
					fHasChildren = true;
					break;
				}
			}
		}
	}
	/**
	 * Creates or updates entity counters.
	 * @param edata
	 */
	protected void buildOrUpdateCounters(EntityData edata) {
		WebSphereAgentContext wasctxt = getWASContext();
		PerfLevelSpec spec = edata.spec;
		// set level to max so we can get all counters (for all levels) at
		// this stage
		spec.setLevel(LEVEL_MAX);
		// get module from context
		ModuleData mdata = wasctxt.getModuleData(
				descriptor.getModuleName());
		if(mdata != null) {
			PmiDataInfo[] dis = mdata.config.listData(PerfModules.getGroupName(spec));
			if(dis == null || dis.length == 0) {
				// check if collection, if so
				// get the counters from the parent
				if(descriptor.getType() == TYPE_COLLECTION) {
					Collection<Counter> coll = ((WebSphereEntity)parent).fCounters.values();
					for(Iterator<Counter> iter = coll.iterator(); iter.hasNext();) {
						WebSphereCounter wasc = (WebSphereCounter)iter.next();
						if(getCounter(wasc.getId()) == null) {
							WebSphereCounter c = new WebSphereCounter(wasc);
							addCounter(c);
							this.countersByIds.put(new Integer(c.getWasId()), c);
						}
					}
				}
			} else {
				for(int i = 0; i < dis.length; i++) {
					PmiDataInfo di = dis[i];
					if(wasctxt.getVersionBehaviour().acceptPmiDataAsCounter(this, di)) {
						if(getCounter(new CounterId(di.getName())) == null) {
							WebSphereCounter c = new WebSphereCounter(descriptor, di, wasctxt);
							addCounter(c);
							this.countersByIds.put(new Integer(c.getWasId()), c);
						}
					}
				}
			}
		}
	}

	/**
	 * @throws Throwable
	 * @see com.ixora.rms.agents.impl.Entity#configMonitoringLevelChanged()
	 */
	protected void configMonitoringLevelChanged() throws Throwable {
		boolean rec = fConfiguration.isRecursiveMonitoringLevel() == null ? false :
			fConfiguration.isRecursiveMonitoringLevel().booleanValue();
		int waslevel = getWASContext().mapLevel(fConfiguration.getMonitoringLevel());
		changeMonitoringLevel(waslevel, rec);
		reconfigureAfterChangingMonitoringLevel(rec);
	}

	/**
	 * Changes the monitoring level in was.
	 * @param waslevel
	 * @param recursive
	 * @throws PmiException
	 * @throws RemoteException
	 */
	protected void changeWASMonitoringLevel(int waslevel, boolean recursive) throws RemoteException, PmiException {
		EntityData ed = getWASContext().getEntityData(fEntityId);
		// if normal entity
		if(ed != null) {
			if(getWASContext().getVersionBehaviour().invokeSetInstrumentationLevel()) {
				getClient().setInstrumentationLevel(descriptor.getNodeName(),
						descriptor.getServerName(),
						ed.spec.getPath(),
						waslevel,
						recursive);
			}
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#disable()
	 */
	protected void disable() {
		getWASContext().disablePerfDescriptor(fEntityId, descriptor);
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#enable()
	 */
	protected void enable() {
		getWASContext().enablePerfDescriptor(fEntityId, descriptor);
	}

	/**
	 * This is called by the parent entity on all its children
	 * after the monitoring level has been changed. Every entity must update
	 * their configuration with the new level (from the entity info offered by their
	 * context which contains refreshed data) as well as delegating further
	 * to their children if the setting was recursive.
	 * @param rec
	 */
	public void updateMonitoringLevel(boolean rec) {
		EntityData ed = getWASContext().getEntityData(fEntityId);
		if(ed != null) {
			// this is a regular entity, so update config
			// with the lates data (ed.level)
			this.fConfiguration.setMonitoringLevel(ed.level);
			this.fConfiguration.setRecursiveMonitoringLevel(rec);
		}
		if(rec) {
			// call this method on all children
			for(Iterator<Entity> iter = this.fChildrenEntities.values().iterator(); iter.hasNext();) {
				((WebSphereEntity)iter.next()).updateMonitoringLevel(rec);
			}
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#addChildEntity(com.ixora.rms.agents.impl.Entity)
	 */
	protected void addChildEntity(Entity entity) throws Throwable {
		Boolean recLevel = this.fConfiguration.isRecursiveMonitoringLevel();
		if(recLevel != null && recLevel.booleanValue()) {
			WebSphereEntity we = (WebSphereEntity)entity;
			we.fConfiguration.setRecursiveMonitoringLevel(recLevel.booleanValue());
		}
		super.addChildEntity(entity);
	}

	/**
	 * @param col
	 */
	public void dataAvailable(CpdCollection col) {
		this.data = col;
	}

	/**
	 * @param pd
	 * @return
	 */
	public static String getEntityNameFromDescriptor(PerfDescriptor pd) {
		return pd.getName() == null ? pd.getDataDescriptor().getName() : pd.getName();
	}

	/**
	 * @param pd
	 * @return
	 */
	public static EntityId getEntityIdFromDescriptor(com.ibm.websphere.pmi.client.PerfDescriptor pd) {
		String[] path = pd.getPath();
		String[] newPath = new String[path.length + 1];
		newPath[0] = "root";
		System.arraycopy(path, 0, newPath, 1, path.length);
		return new EntityId(newPath);
	}

// package access
	/**
	 * @return
	 */
	static EntityId createEntityId(Entity parent, PerfDescriptor desc) {
		return new EntityId(parent.getId(), getEntityNameFromDescriptor(desc));
	}

	/**
	 * Updates only the data which is needed in the EntityDescriptor.
	 * This is needed when an entity is refreshed and it's children's descriptors
	 * are required only.
	 * @param pd
	 * @throws PmiException
	 * @throws RemoteException
	 */
	void update(PerfDescriptor pd, boolean useDataInDescriptor) throws RemoteException, PmiException {
		this.descriptor = pd;
		EntityData ed = getWASContext().getEntityData(fEntityId);
		if(ed != null) {
			// update monitoring level
			this.fConfiguration.setMonitoringLevel(ed.level);
		}
		// check if this entity has children, if it doesn't
		// run once more <code>retrieveWASMembers</code>, maybe
		// now it has
		retrieveWASMembers(useDataInDescriptor);
		// counters don't change in WAS so stop here
	}

	/**
	 * Changes the WAS monitoring level and updates descriptors.
	 * @param waslevel
	 * @param recursive
	 * @throws RemoteException
	 * @throws PmiException
	 */
	void changeMonitoringLevel(int waslevel, boolean recursive) throws RemoteException, PmiException {
		try {
			changeWASMonitoringLevel(waslevel, recursive);
		} catch(Exception t) {
			fContext.error(t);
			return;
		}
		// refresh was entities info
		getWASContext().rebuildInfoRepository();
		// update level for this entity
		updateMonitoringLevel(recursive);
	}
}
