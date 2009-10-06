/*
 * Created on 27-Mar-2005
 */
package com.ixora.rms;

import java.util.Collection;
import java.util.LinkedHashMap;


/**
 * @author Daniel Moraru
 */
public class EntityDescriptorImplMutable extends EntityDescriptorImpl {

	/**
	 * Constructor.
	 * @param entityId
	 */
	public EntityDescriptorImplMutable(EntityId entityId) {
		super();
		this.fEntityId = entityId;
		this.fName = entityId.getName();
	}

	/**
	 * Constructor.
	 * @param entityId
	 * @param desc
	 */
	public EntityDescriptorImplMutable(EntityId entityId, EntityDescriptor desc) {
		super(entityId, desc);
	}

	/**
	 * @param configuration the fConfiguration to set.
	 */
	public void setConfiguration(EntityConfiguration configuration) {
		fConfiguration = configuration;
	}
	/**
	 * @param counterDescriptors the fCounterDescriptors to set.
	 */
	public void setCounterDescriptors(
			Collection<CounterDescriptor> counterDescriptors) {
	    this.fCounterDescriptors = new LinkedHashMap<CounterId, CounterDescriptorImpl>();
	    for(CounterDescriptor cd : counterDescriptors) {
	    	this.fCounterDescriptors.put(cd.getId(), new CounterDescriptorImpl(cd));
	    }
	}
	/**
	 * @param hasChildren the fHasChildren to set.
	 */
	public void setHasChildren(boolean hasChildren) {
		fHasChildren = hasChildren;
	}
	/**
	 * @param supportedLevels the fSupportedLevels to set.
	 */
	public void setSupportedLevels(MonitoringLevel[] supportedLevels) {
		fSupportedLevels = supportedLevels;
	}
	/**
	 * @param supportsIndependentSamplingInterval the fSupportsIndependentSamplingInterval to set.
	 */
	public void setSupportsIndependentSamplingInterval(
			boolean supportsIndependentSamplingInterval) {
		fSupportsIndependentSamplingInterval = supportsIndependentSamplingInterval;
	}
	/**
	 * @param alternateName the fAlternateName to set.
	 */
	public void setAlternateName(String alternateName) {
		fAlternateName = alternateName;
	}
	/**
	 * @param description the fDescription to set.
	 */
	public void setDescription(String description) {
		fDescription = description;
	}
	/**
	 * @param enabled the fEnabled to set.
	 */
	public void setEnabled(boolean enabled) {
		fEnabled = enabled;
	}
	/**
	 * @param level the fLevel to set.
	 */
	public void setLevel(MonitoringLevel level) {
		fLevel = level;
	}
	/**
	 * @param name the fName to set.
	 */
	public void setName(String name) {
		fName = name;
	}

	/**
	 * @param b
	 */
	public void setCanRefreshChildren(boolean b) {
		fCanRefreshChildren = b;
	}

	/**
	 * @param b
	 */
	public void setSafeToRefreshRecursivelly(boolean b) {
		fSafeToRefreshRecursivelly = b;
	}
}
