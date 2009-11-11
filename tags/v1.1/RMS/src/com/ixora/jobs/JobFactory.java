/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

import java.io.IOException;

import com.ixora.jobs.impl.command.ExternalCommandJob;
import com.ixora.jobs.impl.command.ExternalCommandJobData;
import com.ixora.jobs.impl.commandtelnet.TelnetCommandJob;
import com.ixora.jobs.impl.commandtelnet.TelnetCommandJobData;
import com.ixora.rms.exception.RMSException;


/**
 * @author Daniel Moraru
 */
public class JobFactory {
    /** The number to be assigned to the next job */
    private int nextJobNumber;

    /**
     * Creates a job from the given definition.
     * @param def
     * @return
     * @throws RMSException
     */
    public Job createJob(JobDefinition def) throws RMSException {
        JobData jd = def.getJobData();
        if(jd instanceof ExternalCommandJobData) {
            ExternalCommandJobData ecd = (ExternalCommandJobData)jd;
            return new ExternalCommandJob(
                ecd.getCommand(),
                createJobId(def));
        } else if(jd instanceof TelnetCommandJobData) {
        	TelnetCommandJobData tjd = (TelnetCommandJobData)jd;
        	try {
				return new TelnetCommandJob(tjd, createJobId(def));
			} catch (IOException e) {
				throw new RMSException(e);
			}
        }
        return null;
    }

    /**
     * Creates a job id.
     * @return
     */
    private JobId createJobId(JobDefinition def) {
       return new JobId(def.getHost(), String.valueOf(++nextJobNumber));
    }
}
