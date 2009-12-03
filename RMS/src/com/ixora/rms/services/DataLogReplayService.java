package com.ixora.rms.services;

import com.ixora.common.Service;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.DataLogCompareAndReplayConfiguration;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;

/**
 * Log replay service.
 * @author Daniel Moraru
 */
public interface DataLogReplayService extends Service {
	/**
     * ReadListener.
     */
    public interface ReadListener {
        /**
         * Invoked when the end of log is reached for reading.
         * @param rep
         */
        void finishedReadingLog(LogRepositoryInfo rep);
        /**
         * Reports progress on the read process.
         * @param rep
         * @param time
         */
        void readProgress(LogRepositoryInfo rep, long time);
        /**
         * Invoked when a fatal error occurred during replay.
         * @param rep
         * @param e
         */
        void fatalReadError(LogRepositoryInfo rep, Exception e);
    }
	
	/**
	 * @param config
	 * @return the monitoring scheme describing the
	 * system at the time the data was logged
	 * @throws DataLogException
	 */
	MonitoringSessionDescriptor configure(DataLogCompareAndReplayConfiguration config) throws DataLogException;	
	/**
	 * Replays the logs.
	 * @throws DataLogException
	 */
	void startReplay() throws DataLogException;
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
	 * Removes a listener.
	 * @param l
	 */
	void removeReadListener(ReadListener l);
}
