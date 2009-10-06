/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.exception;

import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * Thrown when the JobManager can't be found for a host.
 * @author Daniel Moraru
 */
public final class CantFindJobManagerForHost extends RMSException {

    /**
     * Constructor.
     * @param host
     */
    public CantFindJobManagerForHost(String host) {
        super(JobsComponent.NAME,
              Msg.JOBS_ERROR_CANT_FIND_JOB_MANAGER,
              new String[]{host});
    }
}
