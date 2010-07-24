/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.repository.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.RepositoryComponent;
import com.ixora.rms.repository.messages.Msg;

/**
 * FailedToSaveRepository.
 * @author Daniel Moraru
 */
public final class FailedToSaveRepository extends RMSException {
	private static final long serialVersionUID = 5551595052176076949L;

	/**
	 * Constructor.
	 * @param reason
	 */
	public FailedToSaveRepository(Exception reason) {
		super(RepositoryComponent.NAME, Msg.REPOSITORY_FAILED_TO_SAVE_REPOSITORY,
				new String[] {reason.getMessage()}, reason);
	}
}
