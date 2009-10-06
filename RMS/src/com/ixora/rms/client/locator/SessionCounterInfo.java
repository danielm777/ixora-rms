/*
 * Created on 26-Feb-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.client.model.EntityInfo;
import com.ixora.rms.client.model.ResourceInfo;

/**
 * @author Daniel Moraru
 */
public class SessionCounterInfo extends AbstractSessionArtefactInfo {
	private String fTranslatedCounterPath;
	private CounterDescriptor fCounterDescriptor;

    /**
	 * Constructor.
	 */
	public SessionCounterInfo(ResourceInfo counterInfo) {
		super();
		fTranslatedName = counterInfo.getCounterInfo().getTranslatedName();
		fTranslatedDescription = counterInfo.getCounterInfo().getTranslatedDescription();
		EntityInfo entityInfo = counterInfo.getEntityInfo();
        fCounterDescriptor = counterInfo.getCounterInfo();
		fTranslatedCounterPath = counterInfo.getHostInfo().getName()
			+ EntityId.DELIMITER
			+ counterInfo.getAgentInfo().getTranslatedName()
			+ EntityId.DELIMITER
			+ entityInfo.getTranslatedPath()
			+ EntityId.DELIMITER
			+ fTranslatedName;
	}

	/**
	 * @return the translated full counter path
	 */
	public String getTranslatedCounterPath() {
		return fTranslatedCounterPath;
	}

    /**
     * @return the counter descriptor
     */
    public CounterDescriptor getDescriptor() {
        return fCounterDescriptor;
    }
}
