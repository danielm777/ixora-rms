/*
 * Created on 05-Jan-2005
 */
package com.ixora.rms.agents.providers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.providers.ProviderId;

/**
 * @author Daniel Moraru
 */
public final class AgentShadowEntity extends Entity {
	private static final long serialVersionUID = -8835967062824866316L;
	/** The id of the provider that generates this entity */
	private ProviderId fProviderId;
	/**
	 * True if this entity has been built from a descriptor shell.
	 */
	private boolean hasShellDescriptor;

	/**
	 * Constructor.
	 * @param ed
	 * @param pid
	 * @param c
	 */
	public AgentShadowEntity(EntityDescriptor ed, ProviderId pid, AgentExecutionContext c) {
		super(ed, c);
		this.fProviderId = pid;
		this.fSupportsIndependentSamplingInterval = true;
		if(ed instanceof EntityDescriptorShell) {
			hasShellDescriptor = true;
		}
	}


	/**
	 * Increased visibility and only adds it if not already there.
	 * @see com.ixora.rms.agents.impl.EntityTree#addChildEntity(com.ixora.rms.agents.impl.Entity)
	 */
	public void addChildEntity(Entity entity) throws Throwable {
        if(fChildrenEntities.get(entity.getId()) == null) {
            super.addChildEntity(entity);
        }
	}

	/**
	 * Increased visibility.
	 * @param eid
	 */
	public void removeChildEntity(EntityId eid) {
		super.removeChildEntity(eid);
	}

	/**
	 * Increased visibility.
	 * @see com.ixora.rms.agents.impl.EntityTree#fireChildrenEntitiesChanged()
	 */
	public void fireChildrenEntitiesChanged() {
		super.fireChildrenEntitiesChanged();
	}

	/**
	 * @param counters
	 * @param values
	 */
	public void setCounterValues(List<CounterId> counters, List<CounterValue> values) {
        if(counters.size() != values.size()) {
            throw new IllegalArgumentException("Counters/values list size mismatch");
        }
		int len = counters.size();
		for(int i = 0; i < len; ++i) {
			CounterId cid = counters.get(i);
			Counter counter = getCounter(cid);
			if(counter != null && counter.isEnabled()) {
				counter.dataReceived(values.get(i));
			}
		}
	}

	/**
	 * @return
	 */
	public ProviderId getProviderId() {
		return fProviderId;
	}

	/**
	 * @param pid
	 * @param conf
	 * @throws Throwable
	 * @throws InvalidConfiguration
	 */
	public void configureEntitiesForProvider(ProviderId pid, EntityConfiguration conf) throws InvalidConfiguration, Throwable {
		if(fProviderId.equals(pid)) {
			super.configure(conf);
		}
		for(Iterator<Entity> iter = fChildrenEntities.values().iterator(); iter.hasNext();) {
			AgentShadowEntity ase = (AgentShadowEntity)iter.next();
			ase.configureEntitiesForProvider(pid, conf);
		}
		// trigger refresh, it's not very efficient but being local and usually
		// referring to a small number of entities or rather a shallow tree...
		if(fProviderId.equals(pid)) {
			fireChildrenEntitiesChanged();
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		;// do nothing, this is a push entity, the values will be populated
		// by the parsers
	}


	/**
	 * Updates this entity with info from the given descriptor.
	 * The update takes place only if the given descriptor is more than a shell and
	 * if this entity was last build from a descriptor shell.
     * @param providerId
	 * @param ad
	 */
	public void updateFromDescriptor(ProviderId providerId, EntityDescriptor ed) {
		if(ed instanceof EntityDescriptorShell || !hasShellDescriptor) {
			return;
		}
		if(!this.fHasChildren) {
			if(ed.hasChildren()) {
				this.fHasChildren = true;
			}
		}
        this.fProviderId = providerId;
		this.fLevel = ed.getLevel();
		this.fAlternateName = ed.getAlternateName();
		this.fDescription = ed.getDescription();
		this.fSupportedLevels = ed.getSupportedLevels();
		Collection<CounterDescriptor> counters = ed.getCounterDescriptors();
		if(!Utils.isEmptyCollection(counters)) {
			for(CounterDescriptor cd : counters) {
				addCounter(new Counter(cd));
			}
		}
	}
}
