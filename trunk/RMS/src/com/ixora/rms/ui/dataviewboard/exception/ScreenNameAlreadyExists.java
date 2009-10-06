/*
 * Created on 20-Feb-2005
 */
package com.ixora.rms.ui.dataviewboard.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ScreenNameAlreadyExists extends RMSException {

	/**
	 * Constructor.
	 * @param name
	 */
	public ScreenNameAlreadyExists(String name) {
		super(Msg.ERROR_SCREEN_NAME_ALREADY_EXISTS, new String[] {name});
	}
}
