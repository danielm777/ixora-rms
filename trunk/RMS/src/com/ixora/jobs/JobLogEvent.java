/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs;

/**
 * @author Daniel Moraru
 */
public final class JobLogEvent extends JobEventAbstract {
// data type
    public static final int OUTPUT = 0;
    public static final int ERROR = 1;

    /** Type of output. OUTPUT or ERROR */
    private int type;
    /** Log */
    private String data;

    /**
     * Constructor.
     * @param host
     * @param jid
     * @param type
     * @param data
     */
    public JobLogEvent(String host,
            JobId jid, int type, String log) {
        super(host, jid);
        this.type = type;
        this.data = log;
    }
    /**
     * @return the type of log data. OUTPUT or ERROR.
     */
    public int getType() {
        return type;
    }

    /**
     * @return the log.
     */
    public String getData() {
        return data;
    }
}
