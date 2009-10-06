/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.rms.EntityId;
import com.ixora.rms.messages.Msg;

/**
 * RecordDefinitionNotFound.
 * @author Daniel Moraru
 */
public final class RecordDefinitionNotFound extends RMSException {
	/**
	 * Constructor.
	 * @param eid
	 */
	public RecordDefinitionNotFound(EntityId eid) {
		super(Msg.RMS_RECORD_DEFINITION_NOT_FOUND,
				new String[] {eid.toString()});
	}
}
