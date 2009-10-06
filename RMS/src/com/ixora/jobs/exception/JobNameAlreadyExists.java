/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.exception;

import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class JobNameAlreadyExists extends RMSException {
    /**
     * Constructor.
     * @param name
     */
    public JobNameAlreadyExists(String name) {
        super(JobsComponent.NAME,
              Msg.JOBS_ERROR_JOB_NAME_ALREADY_EXISTS,
              new String[]{name});
    }
}
