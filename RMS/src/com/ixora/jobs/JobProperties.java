/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs;

import com.ixora.jobs.messages.Msg;
import com.ixora.common.typedproperties.TypedProperties;

/**
 * @author Daniel Moraru
 */
public class JobProperties extends TypedProperties {
	private static final long serialVersionUID = 6293866032260423657L;
	public static final String JOB_NAME = Msg.TEXT_JOBS_JOB_NAME;
    public static final String JOB_DATE = Msg.TEXT_JOBS_SCHEDULED_DATE;
    public static final String JOB_HOST = Msg.TEXT_JOBS_HOST;

    /**
     * Constructor.
     * @param hosts
     */
    public JobProperties(String[] hosts, boolean forLibrary) {
        super();
        setProperty(JOB_NAME,
                JobProperties.TYPE_STRING, true);
        if(!forLibrary) {
        	setProperty(JOB_DATE,
                JobProperties.TYPE_DATE, true, false); // not required
        }
        setProperty(JOB_HOST,
                JobProperties.TYPE_STRING, true);
    }
}
