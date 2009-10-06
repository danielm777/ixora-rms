/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Daniel Moraru
 */
public final class JobDefinition implements Serializable {
    /**
     * The date when the job must be executed.
     * If null it will execute immediately.
     */
    private Date date;
    /**
     * Host where the command is to be executed. It could be a regular expression.
     */
    private String host;
    /** Job name */
    private String name;
    /** Job data */
    private JobData jobData;
    /** Host regex */
	private String hostRegex;

    /**
     * Constructor.
     * @param name
     * @param host if it is a string starting with ( and ending with ) then
     * it will be treated as a regular expression
     * @param executionDate
     * @param data
     */
    public JobDefinition(
            String name,
            String host,
            Date executionDate,
            JobData data) {
        super();
        if(name == null || host == null
                || data == null) {
            throw new IllegalArgumentException("null parameters");
        }
        this.name = name;
        this.host = host;
        this.date = executionDate;
        this.jobData = data;
        if(host.startsWith("(") && host.endsWith(")")) {
        	this.hostRegex = this.host.substring(1, this.host.length() - 1);
        }
    }
    /**
     * @return the job data
     */
    public JobData getJobData() {
        return jobData;
    }
    /**
     * @return the job's name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the host.
     */
    public String getHost() {
        return host;
    }    
    
    /**
     * @return whether or not the host is a regular expression
     */
    public boolean isHostRegex() {
    	return hostRegex != null;
    }
	/**
	 * @return
	 */
	public String getHostRegex() {
		return this.hostRegex;
	}
}
