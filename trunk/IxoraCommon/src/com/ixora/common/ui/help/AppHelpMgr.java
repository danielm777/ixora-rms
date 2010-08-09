/*
 * Created on 16-Feb-2005
 */
package com.ixora.common.ui.help;

import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.exception.AppException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class AppHelpMgr implements AppHelp {
	private static final AppLogger logger = AppLoggerFactory.getLogger(HelpComponent.NAME);
	private static AppHelpProvider provider;
	
	/**
	 * @param appFrame the application frame
	 * @param helpMenuItem the menu item that launches the help
	 * @throws AppException
	 */
	public AppHelpMgr(
			JFrame appFrame,
			JMenuItem helpMenuItem) throws AppException {
		String configuredHelpType = ConfigurationMgr.getString(HelpComponent.NAME, 
				HelpConfiguration.HELP_PROVIDER);
		if(!Utils.isEmptyString(configuredHelpType)) {
			if(HelpConfiguration.HELP_PROVIDER_JHELP.equalsIgnoreCase(configuredHelpType)) {
				try {
					provider = new AppHelpJavaHelp(appFrame, helpMenuItem);
				} catch(Exception e) {
					throw new AppException(e);
				}
			} else if(HelpConfiguration.HELP_PROVIDER_WEB.equalsIgnoreCase(configuredHelpType)) {
				try {
					provider = new AppHelpWeb(appFrame, helpMenuItem);
				} catch(Exception e) {
					throw new AppException(e);
				}			
			}  else if(HelpConfiguration.HELP_PROVIDER_FILE.equalsIgnoreCase(configuredHelpType)) {
				try {
					provider = new AppHelpFile(appFrame, helpMenuItem);
				} catch(Exception e) {
					throw new AppException(e);
				}			
			}
		}
	}

	/**
     * @param window
	 * @param fHelp
	 */
	public void showHelp(Window window, String helpID) {
		if(provider == null) {
			// a provider hasn't been configured... just log a warning
			logger.warn("Help manager was not initialized. Cannot show help " + helpID);
		} else {
			provider.showHelp(window, helpID);
		}
	}

	/**
	 * @see com.ixora.common.ui.help.AppHelp#getPreferredProviderType()
	 */
	public ProviderType getPreferredProviderType() {
		if(provider instanceof AppHelpJavaHelp) {
			return ProviderType.JAVA_HELP;
		} else if(provider instanceof AppHelpWeb) {
			return ProviderType.WEB;
		} else if(provider instanceof AppHelpFile) {
			return ProviderType.FILE;
		}
		return null;
	}
}
