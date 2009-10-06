/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.ixora.jobs.exception.CantFindJobManagerForHost;
import com.ixora.jobs.exception.MatchingHostsNotFound;
import com.ixora.jobs.remote.JobManager;
import com.ixora.jobs.remote.JobManagerEventHandler;
import com.ixora.jobs.remote.JobManagerImpl;
import com.ixora.jobs.services.JobEngineService;
import com.ixora.remote.HostManager;
import com.ixora.rms.HostMonitor;
import com.ixora.rms.HostState;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class JobEngine implements JobEngineService {
    /** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(JobEngine.class);

    /** Local job manager */
    private HostJobManager fLocalJobManager;
    /** Cache with JobManager references */
    private HashMap<String, JobManager> fJobManagersMap;
    /** Host monitor */
    private HostMonitor fHostMonitor;
    /** Job history */
    private JobHistory fJobHistory;
    /** RMI event handler */
    private JobManagerEventHandler fJobManagerEventHandler;
    /** Event handler */
    private EventHandler fEventHandler;
    /** Listeners */
    private List<JobEngineService.Listener> fListeners;

    /** Event handler */
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
     * @param hm
     * @throws RemoteException
     */
    public JobEngine(HostMonitor hm) throws RemoteException, Throwable {
        super();
        if(hm == null) {
            throw new IllegalArgumentException("null host monitor");
        }
        this.fHostMonitor = hm;
        this.fListeners = new LinkedList<JobEngineService.Listener>();
        this.fJobManagersMap = new HashMap<String, JobManager>();
        this.fEventHandler = new EventHandler();
        this.fJobManagerEventHandler = new JobManagerEventHandler(this.fEventHandler);
        this.fJobHistory = new JobHistory(this);
        this.fLocalJobManager = new HostJobManager("localhost", new JobFactory(), this.fEventHandler);
    }

    /**
     * @throws RemoteException
     * @throws RMSException
     * @see com.ixora.jobs.services.JobEngineService#runJob(com.ixora.rms.jobs.JobLibraryDefinition)
     */
    public JobId[] runJob(JobDefinition job) throws MatchingHostsNotFound, CantFindJobManagerForHost, RMSException, RemoteException {
    	if(!job.isHostRegex()) {
    		return new JobId[]{runOneJob(job)};
    	} else {
    		String hostRegex = job.getHostRegex();
    		HostState[] hostStates = this.fHostMonitor.getHostsStates();
    		if(!Utils.isEmptyArray(hostStates)) {
    			Pattern pat = Pattern.compile(hostRegex);
    			List<JobId> ids = new LinkedList<JobId>();
    			for(HostState hostState : hostStates) {
    				String host = hostState.getHost();
    				if(pat.matcher(host).find()) {
    					JobDefinition newDef = new JobDefinition(job.getName(), host,
    							job.getDate(), job.getJobData());
   						ids.add(runOneJob(newDef));
    				}
    			}
    			if(ids.size() != 0) {
    				return ids.toArray(new JobId[ids.size()]);
    			}
    			throw new MatchingHostsNotFound(hostRegex);
    		}
    		throw new MatchingHostsNotFound(hostRegex);
    	}
    }

    /**
     * Runs one job from the given definition where the host is not a regex.
     * @param job
     * @return
     * @throws RMSException
     * @throws RemoteException
     */
    private synchronized JobId runOneJob(JobDefinition job) throws RMSException, RemoteException {
    	JobId ret = null;
    	if(job.getJobData().runOnHost()) {
	    	String host = job.getHost();
	        JobManager jm = getJobManagerForHost(host);
	        if(jm == null) {
	            throw new CantFindJobManagerForHost(host);
	        }
	        ret = jm.runJob(job);
    	} else {
    		ret = fLocalJobManager.runJob(job);
    	}
        this.fJobHistory.recordJob(ret, job, JobState.SCHEDULED);
        return ret;
    }

    /**
     * @see com.ixora.jobs.services.JobEngineService#cancelJob(com.ixora.rms.internal.jobs.JobId)
     */
    public void cancelJob(JobId jid) throws RMSException, RemoteException {
        if(fLocalJobManager.hasJob(jid)) {
        	fLocalJobManager.cancelJob(jid);
        } else {
	    	String host = jid.getHost();
	        JobManager jm;
	        synchronized(this) {
	        	jm = getJobManagerForHost(host);
	        }
	        if(jm == null) {
	            throw new CantFindJobManagerForHost(host);
	        }
	        jm.cancelJob(jid);
        }
    }

    /**
     * @see com.ixora.jobs.services.JobEngineService#getJobHistory()
     */
    public JobHistory getJobHistory() {
       return this.fJobHistory;
    }

    /**
     * @param host
     * @return
     * @throws RMSException
     * @throws RemoteException
     */
    private JobManager getJobManagerForHost(String host) throws RMSException, RemoteException {
        JobManager ret = fJobManagersMap.get(host);
        if(ret == null) {
          	HostManager hostManager = this.fHostMonitor
          		.getHostManager(host);
          	if(hostManager == null) {
          	    // try and add host and try again
          	    fHostMonitor.addHost(host, false);
          	    // this time forced
          	    hostManager = fHostMonitor.getHostManager(host, true);
          	    if(hostManager == null) {
          	        return null;
          	    }
          	}
          	ret = (JobManager)hostManager.createManaged(
          	        JobManagerImpl.class.getName(),
          	        host, fJobManagerEventHandler);
          	ret.initialize(host, fJobManagerEventHandler);
          	this.fJobManagersMap.put(host, ret);
        }
       return ret;
    }

    /**
     * @see com.ixora.jobs.services.JobEngineService#addListener(com.ixora.jobs.services.JobEngineService.Listener)
     */
    public void addListener(Listener listener) {
    	// see comments on handleJobStateEvent()
    	synchronized(this) {
            if(!this.fListeners.contains(listener)) {
                this.fListeners.add(listener);
            }
        }
    }

    /**
     * @see com.ixora.jobs.services.JobEngineService#removeListener(com.ixora.jobs.services.JobEngineService.Listener)
     */
    public synchronized void removeListener(Listener listener) {
    	// see comments on handleJobStateEvent()
        synchronized(this) {
            this.fListeners.remove(listener);
        }
    }

    /**
     * @see com.ixora.common.Service#shutdown()
     */
    public void shutdown() {
    	try {
    		HostState[] hosts = this.fHostMonitor.getHostsStates();
    		if(!Utils.isEmptyArray(hosts)) {
    			for(HostState state : hosts) {
    				synchronized(this) {
	    				JobManager jm = this.fJobManagersMap.get(state.getHost());
	    				if(jm != null) {
	    					HostManager hostManager = this.fHostMonitor.getHostManager(state.getHost());
	    					if(hostManager != null) {
	    						hostManager.destroyManaged(jm);
	    					}
	    				}
    				}
    			}
    		}
    	} catch(Exception e) {
    		logger.error(e);
		}
    }

    /**
     * @param ev
     */
    private void handleJobStateEvent(JobStateEvent ev) {
    	// needs to be synch on this instead of fListeners to guarantee
    	// that runJob and recordJob in runOneJob() have
    	// been run before processing events
        synchronized(this) {
            for(Iterator iter = fListeners.iterator(); iter.hasNext();) {
	            try {
	                ((Listener)iter.next()).jobStateEvent(ev);
	            } catch(Exception e) {
	                logger.error(e);
	            }
            }
        }
    }

    /**
     * @param ev
     */
    private void handleJobLogEvent(JobLogEvent ev) {
    	// see comments on handleJobStateEvent()
        synchronized(this) {
            for(Iterator iter = fListeners.iterator(); iter.hasNext();) {
	            try {
	                ((Listener)iter.next()).jobLogEvent(ev);
	            } catch(Exception e) {
	                logger.error(e);
	            }
            }
        }
    }
}
