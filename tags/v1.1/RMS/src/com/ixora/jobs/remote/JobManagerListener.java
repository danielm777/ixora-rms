/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.remote;

import java.rmi.RemoteException;

import com.ixora.jobs.JobLogEvent;
import com.ixora.jobs.JobStateEvent;
import com.ixora.remote.RemoteManagedListener;

/**
 * Remote job manager listener
 * @author Daniel Moraru
 */
public interface JobManagerListener extends RemoteManagedListener {
    /**
     * @param ev
     */
    void jobStateEvent(JobStateEvent ev) throws RemoteException;
    /**
     * @param ev
     */
    void jobLogEvent(JobLogEvent ev) throws RemoteException;
}
