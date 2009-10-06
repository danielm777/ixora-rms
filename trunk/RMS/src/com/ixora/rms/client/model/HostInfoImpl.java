/*
 * Created on 24-Jul-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.HostInformation;
import com.ixora.rms.HostState;


/** Holds host data */
final class HostInfoImpl extends ArtefactInfoContainerImpl
		implements HostInfo {
	private HostInformation info;
	private HostState state;

	/**
	 * Constructor.
	 * @param info
	 * @param state
	 * @param model
	 */
	HostInfoImpl(HostInformation info, HostState state, SessionModel model) {
	    super(model);
		this.info = info;
		this.state = state;
		this.model = model;
	}
	/**
	 * @return the host info
	 */
	public HostInformation getInfo() {
		return info;
	}
	/**
	 * @return the host state
	 */
	public HostState getState() {
		return state;
	}
	/**
	 * @see com.ixora.rms.client.model.HostInfo#getName()
	 */
	public String getName() {
		return this.state.getHost();
	}

// package
	/**
	 * @param dtls
	 */
	void setInfo(HostInformation dtls) {
		info = dtls;
	}
	/**
	 * @param state
	 */
	void setState(HostState state) {
		this.state = state;
	}
}