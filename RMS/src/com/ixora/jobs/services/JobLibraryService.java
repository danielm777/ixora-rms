/**
 * 30-Jul-2005
 */
package com.ixora.jobs.services;

import java.util.Collection;
import java.util.Map;

import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.library.JobLibraryId;
import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public interface JobLibraryService {
	/**
	 * @param id
	 * @return
	 */
	JobLibraryDefinition getJob(JobLibraryId id);
	/**
	 * @return
	 * @throws AppException
	 */
	Map<JobLibraryId, JobLibraryDefinition> getAllJobs() throws AppException;
	/**
	 * @param job
	 * @return
	 * @throws AppException
	 */
	JobLibraryId storeJob(JobLibraryDefinition job) throws AppException;
	/**
	 * @param id
	 * @throws AppException
	 */
	void deleteJob(JobLibraryId id) throws AppException;
	/**
	 * @param ids
	 */
	void deleteJobs(Collection<JobLibraryId> ids) throws AppException;
}
