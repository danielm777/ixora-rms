/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.services;

import java.rmi.RemoteException;

import com.ixora.jobs.HostJobManager;
import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobHistory;
import com.ixora.jobs.JobId;
import com.ixora.jobs.exception.CantFindJobManagerForHost;
import com.ixora.jobs.exception.MatchingHostsNotFound;
import com.ixora.common.Service;
import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public interface JobEngineService extends Service {
    /** Listener */
    public interface Listener extends HostJobManager.Listener {
    }

    /**
     * Runs the given job.
     * @param job
     * @return the id assigned for the given job
     * @throws MatchingHostsNotFound
     * @throws CantFindJobManagerForHost
     * @throws AppException
     * @throws RemoteException
     */
    JobId[] runJob(JobDefinition job) throws MatchingHostsNotFound,
    			CantFindJobManagerForHost, AppException, RemoteException;
    /**
     * Cancels the job with the given id.
     * @param jid
     * @throws AppException
     * @throws RemoteException
     */
    void cancelJob(JobId jid) throws AppException, RemoteException;
    /**
     * @return the job history
     */
    JobHistory getJobHistory();
	/**
	 * Adds a listener.
	 * @param listener
	 */
	void addListener(Listener listener);
	/**
	 * Removes a listener.
	 * @param listener
	 */
	void removeListener(Listener listener);
}
