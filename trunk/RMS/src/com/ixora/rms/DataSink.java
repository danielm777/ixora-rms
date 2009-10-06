/*
 * Created on 12-Dec-2003
 */
package com.ixora.rms;

import com.ixora.rms.agents.AgentDataBuffer;


/**
 * Interface implemented by parties interested in receiving
 * agent monitoring data.
 * @author Daniel Moraru
 */
public interface DataSink {
	/**
	 * Invoked when a new data buffer becomes available.
	 * @param buff
	 */
	void receiveDataBuffers(AgentDataBuffer[] buff);
}
