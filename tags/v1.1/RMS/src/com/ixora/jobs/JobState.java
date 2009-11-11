package com.ixora.jobs;

import com.ixora.jobs.messages.Msg;
import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;

/**
 * Enumeration type for the monitoring agent state.
 * @author: Daniel Moraru
 */
public final class JobState extends Enum {
	private static final long serialVersionUID = 4657850754926034919L;
	public static final JobState SCHEDULED =
    	new JobState(0, MessageRepository.get(
    	        JobsComponent.NAME, Msg.JOBS_ENUM_JOBSTATE_SCHEDULED));
    public static final JobState STARTED =
    	new JobState(1, MessageRepository.get(
    	        JobsComponent.NAME, Msg.JOBS_ENUM_JOBSTATE_STARTED));
    public static final JobState FINISHED =
    	new JobState(2, MessageRepository.get(
    	        JobsComponent.NAME, Msg.JOBS_ENUM_JOBSTATE_FINISHED));
    public static final JobState ERROR =
    	new JobState(3, MessageRepository.get(
    	        JobsComponent.NAME, Msg.JOBS_ENUM_JOBSTATE_ERROR));
    public static final JobState CANCELED =
    	new JobState(4, MessageRepository.get(
    	        JobsComponent.NAME, Msg.JOBS_ENUM_JOBSTATE_CANCELED));

	/**
	 * Constructor.
	 * @param s
	 * @param name
	 */
    private JobState(int s, String name) {
		super(s, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
        switch(key) {
        case 0:
            return SCHEDULED;

        case 1:
            return STARTED;

        case 2:
            return FINISHED;

        case 3:
            return ERROR;

        case 4:
            return CANCELED;
        }
        return null;
    }
}
