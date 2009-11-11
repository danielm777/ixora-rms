/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.ui.artefacts.dashboard.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.artefacts.dashboard.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class SaveConflict extends RMSException {
	private static final long serialVersionUID = 739784191741712167L;

	/**
     * Constructor.
     */
    public SaveConflict(String dashboard) {
        super(Msg.ERROR_DASHBOARD_SAVING_CONFLICT,
                new String[] {dashboard});
    }

}
