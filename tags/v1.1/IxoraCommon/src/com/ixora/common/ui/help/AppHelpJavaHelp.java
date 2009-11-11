/**
 * 
 */
package com.ixora.common.ui.help;

import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.help.messages.Msg;
import com.ixora.common.utils.Utils;

/**
 * Application help provided by JavaHelp.
 * @author Daniel Moraru
 */
public class AppHelpJavaHelp implements AppHelpProvider {
	/** Help broker */
	private HelpBroker fHelpBroker;
	/** Appearance customizer */
	private HelpCustomizer fCustomizer;

    public AppHelpJavaHelp(JFrame appFrame, JMenuItem helpMenuItem) 
    			throws MalformedURLException, HelpSetException, InstantiationException, 
    			IllegalAccessException, ClassNotFoundException {
		super();
		String helpSetPath = ConfigurationMgr.getString(HelpComponent.NAME, 
				HelpConfiguration.HELP_PROVIDER_JHELP_HELP_SET_PATH);
		String defaultItem = ConfigurationMgr.getString(HelpComponent.NAME, 
				HelpConfiguration.HELP_PROVIDER_JHELP_DEFAULT_HELP_ITEM);
        URL url = new URL("file:" + helpSetPath);
		HelpSet hs = new HelpSet(null, url);
		fHelpBroker = hs.createHelpBroker();
		fHelpBroker.enableHelpKey(appFrame.getRootPane(), defaultItem, hs);
		helpMenuItem.addActionListener(new CSH.DisplayHelpFromSource(fHelpBroker));
		String helpCustomizerClass = null;
		if(ConfigurationMgr.get(HelpComponent.NAME).hasProperty(HelpConfiguration.HELP_PROVIDER_JHELP_CUSTOMIZER_CALLBACK_CLASS)) {
			helpCustomizerClass = ConfigurationMgr.getString(HelpComponent.NAME, 
					HelpConfiguration.HELP_PROVIDER_JHELP_CUSTOMIZER_CALLBACK_CLASS);
		}
		HelpCustomizerDefaultCallback helpCustomizer = null;
		if(helpCustomizerClass == null) {
			helpCustomizer = new HelpCustomizerDefaultCallback(
					UIConfiguration.getIcon("application.gif"),
					UIConfiguration.getIcon("help_prev.gif"),
		    		UIConfiguration.getIcon("help_next.gif"),
		    		UIConfiguration.getIcon("help_print.gif"),
		    		UIConfiguration.getIcon("help_setuppage.gif"),
		    		UIConfiguration.getIcon("help_toc.gif"),
		    		UIConfiguration.getIcon("help_index.gif"),
		    		UIConfiguration.getIcon("help_search.gif"),
					MessageRepository.get(Msg.TITLE_TOC),
					MessageRepository.get(Msg.TITLE_INDEX),
					MessageRepository.get(Msg.TITLE_SEARCH),
					MessageRepository.get(Msg.TOOLTIP_TOC),
					MessageRepository.get(Msg.TOOLTIP_INDEX),
					MessageRepository.get(Msg.TOOLTIP_SEARCH),
					UIConfiguration.getPanelPadding(),
					UIConfiguration.getSplitPaneDividerSize());
		} else {
			helpCustomizer = (HelpCustomizerDefaultCallback)Utils.getClassLoader(getClass())
				.loadClass(helpCustomizerClass).newInstance();
		}
		if(helpCustomizer != null) {
			fCustomizer = new HelpCustomizer(appFrame, helpCustomizer);
		}
	}
    
	/**
	 * @see com.ixora.common.ui.help.AppHelp#showHelp(java.awt.Window, java.lang.String)
	 */
	public void showHelp(Window window, String helpID) {
        fCustomizer.addPotentialOwner(window);
        fHelpBroker.setCurrentID(helpID);
        fHelpBroker.setDisplayed(true);
	}
}
