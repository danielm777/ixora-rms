/*
 * Created on 24-Jul-2004
 */
package com.ixora.rms.client.model;
import com.ixora.rms.HostInformation;
import com.ixora.rms.HostState;

/**
 * Host info.
 * @author Daniel Moraru
 */
public interface HostInfo extends ArtefactInfoContainer {
	/**
	 * @return the host info
	 */
	HostInformation getInfo();
	/**
	 * @return the host state
	 */
	HostState getState();
	/**
	 * @return the host name
	 */
	String getName();
}