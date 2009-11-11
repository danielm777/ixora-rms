/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.ui.artefacts.dataview.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.artefacts.dataview.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class SaveConflict extends RMSException {
	private static final long serialVersionUID = -5349594045085097217L;

	/**
     * Constructor.
     */
    public SaveConflict(String dv) {
        super(Msg.ERROR_DATAVIEW_SAVING_CONFLICT,
                new String[] {dv});
    }

}
