/*
 * Created on 10-Apr-2005
 */
package com.ixora.rms.agents.apache;

import com.ixora.rms.agents.impl.RootEntity;

/**
 * @author Daniel Moraru
 */
public final class ApacheEntityRoot extends RootEntity {

	/**
	 * Constructor.
	 * @param c
	 */
	public ApacheEntityRoot(ApacheAgentExecutionContext c) {
		super(c);
	}

	/**
	 * @throws Throwable
	 */
	public void createChildren() throws Throwable {
		addChildEntity(new ApacheEntityStatus(getId(), fContext));
		if(((ApacheAgentExecutionContext)fContext).isExtendedStatusOn()) {
			addChildEntity(new ApacheEntityRequests(getId(), fContext));
			addChildEntity(new ApacheEntityServers(getId(), fContext));
		}
	}
}
