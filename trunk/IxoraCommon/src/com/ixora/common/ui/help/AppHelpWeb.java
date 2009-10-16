/**
 * 
 */
package com.ixora.common.ui.help;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.utils.Utils;

/**
 * Application help provided by JavaHelp.
 * @author Daniel Moraru
 */
public class AppHelpWeb implements AppHelpProvider {	
    private String fBaseURL;

	public AppHelpWeb(JFrame appFrame, JMenuItem helpMenuItem) {
		super();
		fBaseURL = ConfigurationMgr.getString(HelpComponent.NAME, 
				HelpConfiguration.HELP_PROVIDER_WEB_URL);
		helpMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				launchHelp(fBaseURL);
			}});		
	}
    
	/**
	 * @see com.ixora.common.ui.help.AppHelp#showHelp(java.awt.Window, java.lang.String)
	 */
	public void showHelp(Window window, String helpID) {
		launchHelp(fBaseURL + "/" + helpID);
	}

	private void launchHelp(final String url) {
		try {
			Utils.launchBrowser(new URL(url));
		} catch(Throwable e1) {
			UIExceptionMgr.userException(e1);
		}
	}
}
