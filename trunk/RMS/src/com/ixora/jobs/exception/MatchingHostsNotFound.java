/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.exception;

import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * Thrown when the JobManager can't find any host that match the regular
 * expression as specified in the job definition.
 * @author Daniel Moraru
 */
public final class MatchingHostsNotFound extends RMSException {

    /**
     * Constructor.
     * @param host
     */
    public MatchingHostsNotFound(String host) {
        super(JobsComponent.NAME,
              Msg.JOBS_ERROR_MATCHING_HOST_NOT_FOUND,
              new String[]{host});
    }
}
