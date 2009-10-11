package com.ixora.jobs.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.ixora.RMIServiceNames;
import com.ixora.jobs.HostJobManager;
import com.ixora.jobs.JobLogEvent;
import com.ixora.jobs.JobStateEvent;
import com.ixora.common.RMIServices;
import com.ixora.common.thread.RunQueue;

/**
 * This class handles events from remote job managers and
 * used with JobManager.Listener helps interested parties to handle
 * remote events just by implementing a local interface.
 * @author Daniel Moraru
 */
public final class JobManagerEventHandler
	extends UnicastRemoteObject
	implements JobManagerListener {
	private static final long serialVersionUID = 1085646319981969468L;
	/**
	 * Local listener.
	 */
	private HostJobManager.Listener localListener;
	/**
	 * Event queue. The processing of events is done
	 * asynchronously to break the call chain and avoid
	 * deadlocks.
	 */
	private RunQueue processor;

	/**
	 * Constructor.
	 * @param listener
	 * @throws RemoteException
	 */
	public JobManagerEventHandler(HostJobManager.Listener listener) throws RemoteException {
		super(RMIServices.instance().getPort(RMIServiceNames.JOBMANAGEREVENTHANDLER));
		if(listener == null) {
			throw new IllegalArgumentException("null local listener");
		}
		this.localListener = listener;
		this.processor = new RunQueue();
		this.processor.start();
	}

    /**
     * @see com.ixora.jobs.remote.JobManagerListener#jobStateEvent(com.ixora.rms.internal.jobs.JobStateEvent)
     */
    public void jobStateEvent(final JobStateEvent ev) throws RemoteException {
        this.processor.run(new Runnable() {
            public void run() {
                localListener.jobStateEvent(ev);
            }
        });
    }

    /**
     * @see com.ixora.jobs.remote.JobManagerListener#jobLogEvent(com.ixora.rms.internal.jobs.JobLogEvent)
     */
    public void jobLogEvent(final JobLogEvent ev) throws RemoteException {
        this.processor.run(new Runnable() {
            public void run() {
                localListener.jobLogEvent(ev);
            }
        });
    }
}
