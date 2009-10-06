/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ParserIsMissing extends RMSException {
    /**
     * Constructor.
     * @param msg
     */
    public ParserIsMissing(String msg) {
        super(Msg.RMS_ERROR_PARSER_IS_MISSING, new String[]{msg});
    }
}
