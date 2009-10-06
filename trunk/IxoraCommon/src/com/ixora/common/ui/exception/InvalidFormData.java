/*
 * Created on 10-Feb-2005
 */
package com.ixora.common.ui.exception;

import com.ixora.common.exception.AppException;
import com.ixora.common.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class InvalidFormData extends AppException {

	/**
	 * Constructor.
	 * @param item
	 */
	public InvalidFormData(String item) {
		super(Msg.COMMON_UI_ERROR_INVALID_FORM_DATA,
				new String[] {item});
	}
}
