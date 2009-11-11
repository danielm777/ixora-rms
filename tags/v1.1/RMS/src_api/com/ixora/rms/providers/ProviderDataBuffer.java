/**
 * 12-Jul-2005
 */
package com.ixora.rms.providers;

import java.io.Serializable;

import com.ixora.rms.HostDataBuffer;

/**
 * @author Daniel Moraru
 */
public interface ProviderDataBuffer extends HostDataBuffer {

	/**
	 * @return the data.
	 */
	Serializable getData();

	/**
	 * @return
	 */
	ProviderId getProviderId();

}