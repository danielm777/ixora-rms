package com.ixora.rms.services;

import com.ixora.rms.HostId;
import com.ixora.common.Service;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.DataLogReplayConfiguration;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;

/**
 * Log replay service.
 * @author Daniel Moraru
 */
public interface DataLogReplayService extends Service {
	/**
     * ScanListener.
     */
    public interface ScanListener {
       	/**
    	 * Invoked when a entity is discovered during the initial scanning.
         * @param host
         * @param aid
         * @param ed
    	 */
    	void newEntity(HostId host, AgentId aid, EntityDescriptor ed);
        /**
         * Invoked when a new agent descriptor is  discovered during the initial scanning.
         * @param host
         * @param ad
         */
        void newAgent(HostId host, AgentDescriptor ad);
        /**
         * Invoked when the end of log is reached for scanning.
         * @param beginTimestamp
         * @param endTimestamp
         * @return the configuration to use for parsing logged data
         */
        void finishedScanningLog(long beginTimestamp, long endTimestamp);
        /**
         * Invoked when a fatal error occured during replay.
         * @param e
         */
        void fatalScanError(Exception e);
    }

	/**
     * ReadListener.
     */
    public interface ReadListener {
        /**
         * Invoked when the end of log is reached for reading.
         */
        void finishedReadingLog();
        /**
         * Reports progress on the read process.
         * @param time
         */
        void readProgress(long time);
        /**
         * Invoked when a fatal error occured during replay.
         * @param e
         */
        void fatalReadError(Exception e);
    }

	/**
	 * Specifies the repository to read data from.
	 * @param rep the log repository details
	 * @throws InvalidLogRepository
	 * @throws DataLogException
	 */
	void loadLog(LogRepositoryInfo rep) throws InvalidLogRepository, DataLogException;
	/**
	 * @return the monitoring scheme describing the
	 * system at the time the data was logged
	 * @throws DataLogException
	 */
	MonitoringSessionDescriptor getScheme() throws DataLogException;
	/**
	 * Replays the logs.
	 * @param
	 * @throws DataLogException
	 */
	void startReplay(DataLogReplayConfiguration config) throws DataLogException;
	/**
	 * Pauses the replay.
	 * @throws DataLogException
	 */
	void pauseReplay() throws DataLogException;
	/**
	 * Stops the replaying and resets the log position.
	 * @throws DataLogException
	 * @throws InvalidLogRepository
	 */
	void stopReplay() throws DataLogException, InvalidLogRepository;
	/**
	 * Starts scanning.
	 * @throws DataLogException
	 */
	void startScanning() throws DataLogException;
	/**
	 * Stops the scanning.
	 * @throws DataLogException
	 */
	void stopScanning() throws DataLogException;
	/**
	 * Sets the replay speed.
	 * @param ms pause between data buffers in milliseconds
	 */
	void setReplaySpeed(int ms);
	/**
	 * Adds a read listener.
	 * @param l
	 */
	void addReadListener(ReadListener l);
	/**
	 * Adds a scan listener.
	 * @param l
	 */
	void addScanListener(ScanListener l);
	/**
	 * Removes a listener.
	 * @param l
	 */
	void removeReadListener(ReadListener l);
	/**
	 * Removes a listener.
	 * @param l
	 */
	void removeScanListener(ScanListener l);
}
