package com.ixora.rms.services;

import com.ixora.common.Service;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.HostId;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.logging.TimeInterval;
import com.ixora.rms.logging.exception.DataLogException;

/**
 * @author Daniel Moraru
 */
public interface DataLogScanningService extends Service {
	/**
     * ScanListener.
     */
    public interface ScanListener {
       	/**
    	 * Invoked when a entity is discovered during the initial scanning.
    	 * @param rep
         * @param host
         * @param aid
         * @param ed
    	 */
    	void newEntity(LogRepositoryInfo rep, HostId host, AgentId aid, EntityDescriptor ed);
        /**
         * Invoked when a new agent descriptor is  discovered during the initial scanning.
         * @param rep
         * @param host
         * @param ad
         */
        void newAgent(LogRepositoryInfo rep, HostId host, AgentDescriptor ad);
        /**
         * Invoked when the end of log is reached for scanning.
         * @param rep
         * @param ti
         * @return the configuration to use for parsing logged data
         */
        void finishedScanningLog(LogRepositoryInfo rep, TimeInterval ti);
        /**
         * Invoked when a fatal error occurred during replay.
         * @param rep
         * @param e
         */
        void fatalScanError(LogRepositoryInfo rep, Exception e);
    }
	
	/**
	 * Starts scanning.
	 * @param log
	 * @throws DataLogException
	 */
	void startScanning(LogRepositoryInfo log) throws DataLogException;

	/**
	 * Stops the scanning.
	 * @throws DataLogException
	 */
	void stopScanning() throws DataLogException;

	/**
	 * Adds a scan listener.
	 * @param l
	 */
	void addScanListener(ScanListener l);

	/**
	 * Removes a listener.
	 * @param l
	 */
	void removeScanListener(ScanListener l);

}