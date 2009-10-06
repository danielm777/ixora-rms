package com.ixora.rms.services;

import com.ixora.common.Service;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;

/**
 * LogReplayService.
 * @author Daniel Moraru
 */
public interface DataLogService extends Service {
	/** Listener */
	public interface Listener {
		/**
		 * Invoked when a fatal error occurs during logging;
		 * when such an error occurs the logging stops as the entire
		 * log data might be corrupted.
		 * @param t
		 */
		void error(Throwable t);
	}

	/**
	 * Logs data into the given repository.
	 * @param rep the log repository details
	 * @param scheme the scheme describing the state of the
	 * system at the time this method was called
	 * @throws InvalidLogRepository
	 * @throws DataLogException
	 */
	void startLogging(LogRepositoryInfo rep, MonitoringSessionDescriptor scheme, Listener listener)
		throws InvalidLogRepository, DataLogException;
	/**
	 * Stops logging.
	 * @throws DataLogException
	 */
	void stopLogging() throws DataLogException;
}
