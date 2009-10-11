/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.rms.EntityId;
import com.ixora.rms.messages.Msg;

/**
 * InvalidEntity.
 * @author Daniel Moraru
 */
public final class InvalidEntity extends RMSException {
	private static final long serialVersionUID = 2642400404527275487L;

	/**
	 * Constructor.
	 * @param entity
	 */
	public InvalidEntity(EntityId entity) {
		super(Msg.RMS_ERROR_INVALID_ENTITY, new String[]{entity.toString()});
	}
}
