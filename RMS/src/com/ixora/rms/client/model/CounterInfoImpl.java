/*
 * Created on 22-Jul-2004
 */
package com.ixora.rms.client.model;

import org.w3c.dom.Node;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.MonitoringLevel;

/**
 * Class that holds data on a counter.
 * @author Daniel Moraru
 */
final class CounterInfoImpl extends ArtefactInfoImpl
		implements CounterInfo {
	/** CounterDescriptor */
	private CounterDescriptor counter;

	/**
	 * @param msgRep
	 * @param cd
     * @throws IllegalArgumentException if the id of the counter is equal to ResourceId.EMPTY_COUNTER
	 */
	public CounterInfoImpl(String msgRep, CounterDescriptor cd, SessionModel model) {
	    super(msgRep, cd.getName(), cd.getDescription(), model);
		this.counter = cd;
        // check that the id of this counter is not EMPTY_COUNTER
        // as this is an invalid value
        if(ResourceId.EMPTY_COUNTER.equals(cd.getId())) {
            throw new IllegalArgumentException("Invalid counter id: " + ResourceId.EMPTY_COUNTER);
        }
		// do some extra processing here on name and description
		// that is specific to a counter
		String alternateName = counter.getAlternateName();
		if(!Utils.isEmptyString(alternateName)) {
			this.translatedName = alternateName;
		} else {
			this.translatedName = MessageRepository.get(msgRep, counter.getName());
		}
		this.translatedDescription = MessageRepository.get(msgRep, counter.getDescription());
		this.flags.set(ENABLED, cd.isEnabled());
		this.inEditEnabled = cd.isEnabled();
	}
	/**
	 * @see com.ixora.rms.CounterDescriptor#getId()
	 */
	public CounterId getId() {
		return counter.getId();
	}
	/**
	 * @see com.ixora.rms.CounterDescriptor#getType()
	 */
	public CounterType getType() {
		return counter.getType();
	}
	/**
	 * @see com.ixora.rms.CounterDescriptor#isDiscreet()
	 */
	public boolean isDiscreet() {
		return counter.isDiscreet();
	}
	/**
	 * @see com.ixora.rms.MonitoringDescriptor#getName()
	 */
	public String getName() {
		return counter.getName();
	}
	/**
	 * @see com.ixora.rms.MonitoringDescriptor#appliesToLevel(com.ixora.rms.MonitoringLevel)
	 */
	public boolean appliesToLevel(MonitoringLevel level) {
		return counter.appliesToLevel(level);
	}
	/**
	 * @see com.ixora.rms.MonitoringDescriptor#getDescription()
	 */
	public String getDescription() {
		return counter.getDescription();
	}
	/**
	 * @see com.ixora.rms.MonitoringDescriptor#getLevel()
	 */
	public MonitoringLevel getLevel() {
		return counter.getLevel();
	}
	/**
	 * @see com.ixora.rms.MonitoringDescriptor#getAlternateName()
	 */
	public String getAlternateName() {
		return counter.getAlternateName();
	}
	/**
	 * @see com.ixora.rms.CounterDescriptor#getViewboardClassName()
	 */
	public String getViewboardClassName() {
		return counter.getViewboardClassName();
	}
    /**
     * @see com.ixora.rms.MonitoringDescriptor#isEnabled()
     */
    public boolean isEnabled() {
        return getFlag(ENABLED);
    }
    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
    	counter.toXML(parent);
    }
    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
    	counter.fromXML(node);
    }

// package
    /**
     * @return the counter description.
     */
    CounterDescriptor getCounterDescriptor() {
        return counter;
    }
}