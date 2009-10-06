/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.logging.exception;

import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.messages.Msg;


/**
 * DataLogException.
 * @author Daniel Moraru
 */
public final class NoLogWasLoaded extends DataLogException {
	public NoLogWasLoaded() {
		super(LogComponent.NAME, Msg.LOGGING_NO_LOG_WAS_LOADED, true);
	}
}
