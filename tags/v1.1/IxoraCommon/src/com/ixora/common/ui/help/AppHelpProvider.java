package com.ixora.common.ui.help;

import java.awt.Window;

/**
 * @author Daniel Moraru
 */
public interface AppHelpProvider {
	/**
	 * @param window
	 * @param helpID
	 */
	void showHelp(Window window, String helpID);
}
