/**
 * 08-Jan-2006
 */
package com.ixora.rms.ui.artefacts.dataview.wizard.exception;

import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class NoResourcesDefined extends RMSException {
	private static final long serialVersionUID = 2518024201819215587L;

	/**
	 *
	 */
	public NoResourcesDefined() {
		super("Resources are missing from the definition of this data view.");
	}
}
