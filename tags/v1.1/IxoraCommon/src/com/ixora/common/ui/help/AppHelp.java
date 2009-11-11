package com.ixora.common.ui.help;

import java.awt.Window;

/**
 * @author Daniel Moraru
 */
public interface AppHelp {
	public enum ProviderType {
		JAVA_HELP, WEB
	}
	
	/**
	 * @return
	 */
	ProviderType getPreferredProviderType();
	
	/**
	 * @param window
	 * @param helpID
	 */
	void showHelp(Window window, String helpID);
}
