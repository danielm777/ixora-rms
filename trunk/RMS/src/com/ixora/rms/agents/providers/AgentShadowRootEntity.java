/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.agents.providers;

import java.util.Iterator;
import java.util.Map;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.RootEntity;
import com.ixora.rms.agents.providers.parsers.MonitoringDataParser;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.providers.ProviderId;

/**
 * @author Daniel Moraru
 */
public class AgentShadowRootEntity extends RootEntity {
	/** Logger */
	private static final AppLogger sLogger = AppLoggerFactory.getLogger(AgentShadowRootEntity.class);

	/**
	 * Constructor.
	 * @param pid
	 * @param c
	 */
	public AgentShadowRootEntity(AgentExecutionContext c) {
		super(c);
	}

	/**
	 * Populates the entity tree with entities created from the given
	 * descriptors.
	 * @param parser
	 * @param pid
	 * @param descs
	 */
	public void populateFromDescriptors(MonitoringDataParser parser,
			ProviderId pid, Map<EntityId, EntityDescriptor> descs) {
		if(descs == null) {
			return;
		}

		try {
			for(EntityDescriptor ed : descs.values()) {
				EntityId[] na = ed.getId().getAncestors();
				EntityId[] a = new EntityId[na.length + 1];
				int i;
				for(i = 0; i < na.length; ++i) {
					a[i] = na[i];
				}
				a[i] = ed.getId();

				// check if all entities are in place and add
				// any missing ones
				for(i = 1; i < a.length; ++i) {
					EntityId eid = a[i];
					Entity ae = findEntity(eid, false);
					Entity parent;
					if(i == 1) {
						parent = this;
					} else {
						parent = findEntity(a[i - 1], false);
					}

					EntityDescriptor ad = descs.get(eid);
					// add or update ancestor?
					if(ae == null) {
						// add it if non volatile
						if(parser.isEntityFinal(eid)) {
							if(ad == null) {
								// build a minimal entity descriptor
								ad = new EntityDescriptorShell(eid, true);
							}
							AgentShadowEntity ase = new AgentShadowEntity(ad, pid, this.fContext);
							if(i == 1) {
								((AgentShadowRootEntity)parent).addChildEntity(ase);
							} else {
								((AgentShadowEntity)parent).addChildEntity(ase);
							}
						}
					} else {
						// update
						if(ae instanceof AgentShadowEntity) {
							if(ad != null) {
								((AgentShadowEntity)ae).updateFromDescriptor(pid, ad);
							}
						}
					}
				}
			}
		} catch(Throwable t) {
			// AgentShadowEntity shouldn't throw any exception
			// so I'll catch this here
			sLogger.error(t);
		}
	}

	/**
	 * @param pid
	 * @param conf
	 * @throws Throwable
	 * @throws InvalidConfiguration
	 */
	public void configureEntitiesForProvider(ProviderId pid, EntityConfiguration conf) throws InvalidConfiguration, Throwable {
		for(Iterator iter = fChildrenEntities.values().iterator(); iter.hasNext();) {
			((AgentShadowEntity)iter.next()).configureEntitiesForProvider(pid, conf);
		}
	}

    /**
     * Increased visibility and only adds it if not already there.
     * @see com.ixora.rms.agents.impl.RootEntity#addChildEntity(com.ixora.rms.agents.impl.Entity)
     */
    public void addChildEntity(Entity entity) throws Throwable {
        if(fChildrenEntities.get(entity.getId()) == null) {
            super.addChildEntity(entity);
        }
    }

    /**
     * Increased visibility
     * @see com.ixora.rms.agents.impl.Entity#fireChildrenEntitiesChanged()
     */
    public void fireChildrenEntitiesChanged() {
        super.fireChildrenEntitiesChanged();
    }


}
