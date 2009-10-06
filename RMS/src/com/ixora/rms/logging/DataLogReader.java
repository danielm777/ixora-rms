/*
 * Created on 06-Jun-2004
 */
package com.ixora.rms.logging;

import com.ixora.rms.HostId;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.exception.DataLogException;

/**
 * Log data writer.
 * @author Daniel Moraru
 */
public interface DataLogReader {
    /**
     * Callback for the initial scanning phase.
     */
    public interface ScanCallback {
    	/**
    	 * Invoked when a entity is discovered during the initial scanning.
    	 */
    	void handleEntity(HostId host, AgentId aid, EntityDescriptor ed);
        /**
         * Invoked when an agent is discovered during the initial scanning.
         */
        void handleAgent(HostId host, AgentDescriptor ad);
    	/**
    	 * Invoked when scanning finished.
    	 * @param beginTimestamp
    	 * @param endTimestamp
    	 */
    	void finishedScanning(long beginTimestamp, long endTimestamp);
        /**
         * @param e fatal error encountered during scanning
         */
        void handleScanFatalError(Exception e);
    }

	/**
     * Reader callback.
     */
    public interface ReadCallback {
        /**
         * @param db a newly read data buffer, null if
         * the end of the log has been reached.
         */
        void handleDataBuffer(AgentDataBuffer db);
        /**
         * @param e fatal error encountered during reading
         */
        void handleReadFatalError(Exception e);
        /**
         * @param time
         */
        void handleReadProgress(long time);
    }

	/**
	 * Reads the monitoring scheme.<br>
	 * This method will be called once before the call
	 * to <code>read()</code>.
	 * @return
	 * @throws DataLogException
	 */
	MonitoringSessionDescriptor readSessionDescriptor() throws DataLogException;
	/**
	 * Starts scanning.
	 * @param cb callback to handle scan events
	 * @throws DataLogException
	 */
	void scan(ScanCallback cb) throws DataLogException;
	/**
	 * Starts reading.
	 * @param cb callback to handle read events
	 * @param timestampEnd
	 * @param timestampBegin
	 * @throws DataLogException
	 */
	void read(ReadCallback cb, long timestampBegin, long timestampEnd) throws DataLogException;
	/**
	 * Stops reading and closes this reader.
	 * @throws DataLogException
	 */
	void close() throws DataLogException;
}
