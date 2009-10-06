/*
 * Created on 02-Aug-2004
 */
package com.ixora.rms.client.model;

import com.ixora.common.MessageRepository;
import com.ixora.rms.messages.Msg;


/**
 * @author Daniel Moraru
 */
public final class SessionInfoImpl
	extends ArtefactInfoContainerImpl implements SessionInfo {
	/** Scheme name */
	private String schemeName;

	/**
	 * Constructor.
	 * @param model
	 */
	SessionInfoImpl(SessionModel model) {
		super(model);
		this.schemeName = MessageRepository.get(Msg.RMS_TEXT_UNTITLED_SESSION);
	}
	/**
	 * @see com.ixora.rms.client.model.SessionInfo#getName()
	 */
	public String getName() {
		return this.schemeName;
	}

// package
	void setName(String name) {
		this.schemeName = name;
	}
}
