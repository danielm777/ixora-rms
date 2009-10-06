/*
 * Created on 03-Jul-2004
 */
package com.ixora.common.ui.exception;

import com.ixora.common.exception.AppException;

/**
 * FailedToSaveDocument. Implementors of
 * <code>com.ixora.common.ui.EmbeddedEditorPanel.SaveCallback</code>
 * should provide here details as to why the save operation failed.
 * @author Daniel Moraru
 */
public class FailedToSaveDocument extends AppException {
	/**
	 * Constructor.
	 * @param message message key
	 */
	public FailedToSaveDocument(String message) {
		super(message, true);
	}

	/**
	 * Constructor.
	 * @param message message key
	 * @param tokens message token values
	 */
	public FailedToSaveDocument(String message, String[] tokens) {
		super(message, tokens);
	}

	/**
	 * Constructor.
	 * @param component component used in localization
	 * @param message message key
	 */
	public FailedToSaveDocument(String component, String message) {
		super(component, message, true);
	}

	/**
	 * Constructor.
	 * @param component component used in localization
	 * @param message message key
	 * @param tokens message token values
	 */
	public FailedToSaveDocument(String component, String message, String[] tokens) {
		super(component, message, tokens);
	}
}
