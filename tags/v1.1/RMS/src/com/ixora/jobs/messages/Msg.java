/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
    public static final String JOBS_ERROR_CANT_FIND_JOB_MANAGER =
    	"jobs.error.cant_find_job_manager";
    public static final String JOBS_ENUM_JOBSTATE_SCHEDULED =
        "jobs.enum.jobstate.scheduled";
    public static final String JOBS_ENUM_JOBSTATE_STARTED =
        "jobs.enum.jobstate.started";
    public static final String JOBS_ENUM_JOBSTATE_FINISHED =
        "jobs.enum.jobstate.finished";
	public static final String JOBS_ERROR_MATCHING_HOST_NOT_FOUND = 
		"jobs.error.matching_host_not_found";
    public static final String JOBS_ENUM_JOBSTATE_ERROR =
        "jobs.enum.jobstate.error";
    public static final String JOBS_ENUM_JOBSTATE_CANCELED =
        "jobs.enum.jobstate.canceled";    
	public static final String JOBS_ERROR_JOB_NAME_ALREADY_EXISTS = 
		"jobs.error.job_name_already_exists";	
    public static final String TEXT_JOBS_HOST =
    	"jobs.ui.text.jobs.host";
    public static final String TEXT_JOBS_JOB_NAME =
    	"jobs.ui.text.jobs.job_name";
    public static final String TEXT_JOBS_SCHEDULED_DATE =
    	"jobs.ui.text.jobs.scheduled_date";    
}
