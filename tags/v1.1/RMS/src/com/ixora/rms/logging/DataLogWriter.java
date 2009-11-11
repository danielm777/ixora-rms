/*
 * Created on 06-Jun-2004
 */
package com.ixora.rms.logging;

import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.exception.DataLogException;

/**
 * Log data writer.
 * @author Daniel Moraru
 */
public interface DataLogWriter {
	/**
	 * Writes the given monitoring scheme to the repository.<br>
	 * This method will be called once at the end of the logging
	 * process so it can be used to 'close' this writer.
	 * @param scheme
	 * @throws DataLogException
	 */
	void writeSessionDescriptor(MonitoringSessionDescriptor scheme) throws DataLogException;
	/**
	 * Writes the given data buffer to the repository.
	 * @param db
	 * @throws DataLogException
	 */
	void writeBuffer(AgentDataBuffer db) throws DataLogException;
	/**
	 * Closes this writer.
	 * @throws DataLogException
	 */
	void close() throws DataLogException;
}
