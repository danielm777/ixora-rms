/*
 * Created on 27-May-2005
 *
 */
package com.ixora.rms.exception;

import com.ixora.rms.messages.Msg;

/**
 * NonExistentResourceIdInReactionDef
 * @author Daniel Moraru
 */
public final class NonExistentResourceIdInReactionDef extends QueryException {
	private static final long serialVersionUID = -5914527371707112601L;

	/**
	 * Constructor.
	 */
	public NonExistentResourceIdInReactionDef(String rid) {
		super(Msg.RMS_NON_EXISTENT_RESOURCEID_IN_REACTION_DEF, new String[] {rid});
	}
}
