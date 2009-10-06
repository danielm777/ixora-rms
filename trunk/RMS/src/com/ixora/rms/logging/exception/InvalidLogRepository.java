/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.logging.exception;


/**
 * InvalidLogRepository.
 * @author Daniel Moraru
 */
public final class InvalidLogRepository extends DataLogException {
    /**
     * Constructor.
     * @param msgKey
     * @param msgTokens
     */
    public InvalidLogRepository(String msgKey, String[] msgTokens) {
        super(msgKey, msgTokens);
    }
	public InvalidLogRepository(String comp, String msg, String[] tokens) {
		super(comp, msg, tokens);
	}
	public InvalidLogRepository(String comp, String msg) {
		super(comp, msg);
	}
	public InvalidLogRepository(String s) {
		super(s);
	}
	public InvalidLogRepository(Exception e) {
		super(e);
	}
}
