/*
 * Created on 27-May-2005
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * NonExistentResourceIdInReactionDef
 */
public final class NonExistentResourceIdInReactionDef extends QueryException {
	/**
	 * Constructor.
	 */
	public NonExistentResourceIdInReactionDef(String rid) {
		super(Msg.RMS_NON_EXISTENT_RESOURCEID_IN_REACTION_DEF, new String[] {rid});
	}
}
