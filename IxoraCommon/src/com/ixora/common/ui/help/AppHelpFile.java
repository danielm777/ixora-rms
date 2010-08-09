/**
 * 
 */
package com.ixora.common.ui.help;

import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.utils.Utils;

/**
 * Application help provided by a file.
 * 
 * @author Daniel Moraru
 */
public class AppHelpFile implements AppHelpProvider {
	private String fFileLocation;

	public AppHelpFile(JFrame appFrame, JMenuItem helpMenuItem) {
		super();
		fFileLocation = ConfigurationMgr.getString(HelpComponent.NAME,
				HelpConfiguration.HELP_PROVIDER_FILE_LOCATION);
		helpMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchHelp(fFileLocation);
			}
		});
	}

	/**
	 * @see com.ixora.common.ui.help.AppHelp#showHelp(java.awt.Window,
	 *      java.lang.String)
	 */
	public void showHelp(Window window, String helpID) {
		launchHelp(fFileLocation);
	}

	private void launchHelp(final String file) {
		try {
			Desktop.getDesktop().open(new File(Utils.getPath(file)));
		} catch (Throwable e1) {
			UIExceptionMgr.userException(e1);
		}
	}
}
