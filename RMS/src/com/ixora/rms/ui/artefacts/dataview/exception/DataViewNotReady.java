/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.ui.artefacts.dataview.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.artefacts.dataview.messages.Msg;

/**
 * This is thrown in log replay mode when a view's query
 * doesn't have all the required counters.
 * @author Daniel Moraru
 */
public final class DataViewNotReady extends RMSException {
    /**
     * Constructor.
     * @param view
     */
    public DataViewNotReady(String view) {
        super(Msg.ERROR_DATAVIEW_NOT_READY, new String[]{view});
    }

}
