/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.ixora.RMIServiceNames;
import com.ixora.jobs.HostJobManager;
import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobFactory;
import com.ixora.jobs.JobId;
import com.ixora.jobs.JobLogEvent;
import com.ixora.jobs.JobStateEvent;
import com.ixora.remote.RemoteManagedListener;
import com.ixora.common.RMIServices;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class JobManagerImpl extends UnicastRemoteObject implements JobManager {
	private static final long serialVersionUID = 4117509654010486756L;

	/** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(JobManagerImpl.class);

    /**
     * Local class that does the work for this remote
     * interface implementation.
     */
    private HostJobManager hostJobManager;
    /** Remote listener */
    private JobManagerListener listener;

    /**
     * Event handler.
     */
    private final class EventHandler implements HostJobManager.Listener {
        /**
         * @see com.ixora.jobs.HostJobManager.Listener#jobStateEvent(com.ixora.rms.internal.jobs.JobStateEvent)
         */
        public void jobStateEvent(JobStateEvent ev) {
            handleJobStateEvent(ev);
        }
        /**
         * @see com.ixora.jobs.HostJobManager.Listener#jobLogEvent(com.ixora.rms.internal.jobs.JobLogEvent)
         */
        public void jobLogEvent(JobLogEvent ev) {
            handleJobLogEvent(ev);
        }
    }


    /**
     * Constructor.
     * @throws RemoteException
     */
    public JobManagerImpl() throws RemoteException {
        super(RMIServices.instance().getPort(RMIServiceNames.JOBMANAGER));
    }

    /**
     * @see com.ixora.remote.RemoteManaged#initialize(java.lang.String, com.ixora.rms.remote.RemoteManagedListener)
     */
    public void initialize(String host, RemoteManagedListener listener)
            throws RMSException, RemoteException {
       this.hostJobManager = new HostJobManager(
               host,
               new JobFactory(),
               new EventHandler()
       );
       this.listener = (JobManagerListener)listener;
    }

    /**
     * @see com.ixora.remote.RemoteManaged#shutdown()
     */
    public void shutdown() throws RMSException, RemoteException {
    }

    /**
     * @see com.ixora.jobs.remote.JobManager#runJob(com.ixora.rms.internal.jobs.JobDefinition)
     */
    public JobId runJob(JobDefinition job) throws RemoteException, RMSException {
        try {
            return this.hostJobManager.runJob(job);
        } catch(Exception e) {
            logger.error(e);
            throw new RMSException(e);
        }
    }

    /**
     * @see com.ixora.jobs.remote.JobManager#cancelJob(com.ixora.rms.internal.jobs.JobId)
     */
    public void cancelJob(JobId jid) throws RemoteException, RMSException {
        try {
            this.hostJobManager.cancelJob(jid);
        } catch(Exception e) {
            logger.error(e);
            throw new RMSException(e);
        }
    }

    /**
     * @param ev
     */
    private void handleJobLogEvent(JobLogEvent ev) {
        try {
            listener.jobLogEvent(ev);
        } catch(Exception e) {
            logger.error(e);
        }
    }

    /**
     * @param ev
     */
    private void handleJobStateEvent(JobStateEvent ev) {
        try {
            listener.jobStateEvent(ev);
        } catch(Exception e) {
            logger.error(e);
        }
    }
}
