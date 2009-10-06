/*
 * Created on 01-Jan-2005
 */
package com.ixora.rms.client.model;

import java.util.Collection;

import com.ixora.rms.ResourceId;
import com.ixora.rms.repository.ProviderInstance;

/**
 * @author Daniel Moraru
 */
public interface ProviderInstanceModelHelper {
	/**
	 * @param rid
	 * @param data
	 */
	void setProviderInstanceData(ResourceId rid, Collection<ProviderInstance> data);

	/**
     * Adds the given provider instance to the given context.
     * @param id
     * @param pi
     */
    void addProviderInstance(ResourceId id, ProviderInstance pi);

	/**
     * Removes the given provider instance.
     * @param id
     * @param instanceName
     */
    void removeProviderInstance(ResourceId id, String instanceName);
}
