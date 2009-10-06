/*
 * Created on 16-Feb-2005
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

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.help.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class HelpMgr {
	/** Help broker */
	private static HelpBroker sHelpBroker;
    /** Customizer */
    private static HelpCustomizer sCustomizer;

    /**
	 * Constructor.
	 */
	private HelpMgr() {
		super();
	}

	/**
	 * Initializes the help manager using the help set at
	 * the given path.
	 * @param appFrame the application frame
	 * @param helpSetPath the path to the help set
	 * @param defaultItem default help item to be shown
	 * @param helpMenuItem the menu item that launches the help
	 * @param helpCustomizer customizer of the JavaHelp frame, if null the default
	 * customizer is used
	 * @throws MalformedURLException
	 * @throws HelpSetException
	 */
	public static void initialize(
			JFrame appFrame,
			String helpSetPath,
			String defaultItem,
			JMenuItem helpMenuItem,
			HelpCustomizer.Callback helpCustomizer) throws MalformedURLException, HelpSetException {
        URL url = new URL("file:" + helpSetPath);
		HelpSet hs = new HelpSet(null, url);
		sHelpBroker = hs.createHelpBroker();
		sHelpBroker.enableHelpKey(appFrame.getRootPane(), defaultItem, hs);
		helpMenuItem.addActionListener(new CSH.DisplayHelpFromSource(sHelpBroker));
		if(helpCustomizer == null) {
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
		}
	    sCustomizer = new HelpCustomizer(appFrame, helpCustomizer);
	}


	/**
	 * @return the help broker.
	 */
	public static HelpBroker getBroker() {
		return sHelpBroker;
	}

	/**
     * @param window
	 * @param fHelp
	 */
	public static void showHelp(Window window, String helpID) {
        sCustomizer.addPotentialOwner(window);
        sHelpBroker.setCurrentID(helpID);
        sHelpBroker.setDisplayed(true);
	}
}
