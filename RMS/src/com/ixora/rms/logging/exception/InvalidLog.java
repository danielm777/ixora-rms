/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.logging.exception;

import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.messages.Msg;


/**
 * InvalidLog.
 * @author Daniel Moraru
 */
public final class InvalidLog extends DataLogException {
	private static final long serialVersionUID = -6999395544863120520L;

	public InvalidLog(Exception e) {
		super(LogComponent.NAME, Msg.INVALID_LOG, e, true);
	}
}
