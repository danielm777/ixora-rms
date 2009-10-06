/**
 * 02-Jan-2006
 */
package com.ixora.common.ui.wizard;

import javax.swing.JPanel;

/**
 * @author Daniel Moraru
 */
public abstract class WizardStep extends JPanel {
	protected String fName;
	protected String fDescription;

	protected WizardStep(String name, String description) {
		super();
		fName = name;
		fDescription = description;
	}

	public String getDescription() {
		return fDescription;
	}

	public String getName() {
		return fName;
	}

	public abstract void activate(Object obj) throws Exception;

	public abstract Object deactivate() throws Exception;
}
