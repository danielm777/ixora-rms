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
 * Log data reader.
 * @author Daniel Moraru
 */
public interface DataLogReader {
    /**
     * Callback for the initial scanning phase.
     */
    public interface ScanCallback {
    	/**
    	 * Invoked when a entity is discovered during the initial scanning.
    	 * @param source the source of the callback.
    	 */
    	void handleEntity(DataLogReader source, HostId host, AgentId aid, EntityDescriptor ed);
        /**
         * Invoked when an agent is discovered during the initial scanning.
         * @param source the source of the callback.
         * @param host the host id.
         * @param ad the agent descriptor.
         */
        void handleAgent(DataLogReader source, HostId host, AgentDescriptor ad);
    	/**
    	 * Invoked when scanning finished.
    	 * @param source the source of the callback.
    	 * @param beginTimestamp
    	 * @param endTimestamp
    	 */
    	void finishedScanning(DataLogReader source, long beginTimestamp, long endTimestamp);
        /**
         * @param source the source of the callback.
         * @param e fatal error encountered during scanning
         */
        void handleScanFatalError(DataLogReader source, Exception e);
    }

	/**
     * Reader callback.
     */
    public interface ReadCallback {
        /**
         * @param source the source of the callback.
         * @param db a newly read data buffer, null if
         * the end of the log has been reached.
         */
        void handleDataBuffer(DataLogReader source, AgentDataBuffer db);
        /**
         * @param source the source of the callback.
         * @param e fatal error encountered during reading
         */
        void handleReadFatalError(DataLogReader source, Exception e);
        /**
         * @param source the source of the callback.
         * @param time
         */
        void handleReadProgress(DataLogReader source, long time);
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
