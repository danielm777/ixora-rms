/*
 * Created on 07-Jun-2004
 */
package com.ixora.rms.logging;

import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;

/**
 * DataLoggerRepository.
 * @author Daniel Moraru
 */
public interface DataLogRepository {
	/**
	 * @param rep
	 * @return a reader from the given repository
	 * @throws InvalidLogRepository
	 * @throws DataLogException
	 */
	DataLogReader getReader(LogRepositoryInfo rep) throws InvalidLogRepository, DataLogException;
	/**
	 * @param rep
	 * @return a writer to the given repository
	 * @throws InvalidLogRepository
	 * @throws DataLogException
	 */
	DataLogWriter getWriter(LogRepositoryInfo rep) throws InvalidLogRepository, DataLogException;
}
