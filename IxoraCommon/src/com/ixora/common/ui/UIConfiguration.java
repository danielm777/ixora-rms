package com.ixora.common.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.exception.FailedToLoadConfiguration;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;


/**
 * UI configuration.
 * @author: Daniel Moraru
 */
public final class UIConfiguration extends ComponentConfiguration {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(UIConfiguration.class);
    /** Single instance */
    private static UIConfiguration instance;
	/** Icon folder */
    private static String iconsFolder;

    static {
    	try {
	    	instance = new UIConfiguration(
	    			"config/ui/config.properties",
					"config/ui/icons/");
    	} catch(FailedToLoadConfiguration e) {
    		logger.error(e);
    	}
    }

    /**
     * @param icon
     * @return
     */
    public static ImageIcon getIcon(String icon) {
    	File file = new File(iconsFolder + icon);
    	if(file.exists()) {
    		return new ImageIcon(file.getAbsolutePath());
    	} else {
    		return null;
    	}
    }

    /**
     * @return The panel padding.
     */
    public static int getPanelPadding() {
    	return instance.getInt("common.ui.dialog.padding");
    }

	/**
	 * @return The split pane divider size.
	 */
    public static int getSplitPaneDividerSize() {
		return instance.getInt("common.ui.splitpane.dividersize");
	}

	/**
	 * @return The split pane divider size.
	 */
    public static int getMaximumLineLengthForToolTipText() {
		return instance.getInt("common.ui.tooltip.max_line_length");
	}

	/**
	 * @return The split pane divider size.
	 */
    public static int getToolTipDismissDelay() {
		return instance.getInt("common.ui.tooltip.dismiss_delay");
	}

    /**
     * @return the URL with the location of the icons
     */
    public static URL getIconsURL() {
    	try {
			return new URL("file:" + iconsFolder);
		} catch (MalformedURLException e) {
			logger.error(e);
		}
		return null;
    }

    /**
     * Configuration constructor comment.
     * @param confFile the path to the configuration file
     * @param icons the path to the icons folder
     * @throws FailedToLoadConfiguration
     */
    private UIConfiguration(String confFile, String icons) throws FailedToLoadConfiguration {
		super(confFile);
		iconsFolder = Utils.getPath(icons);
    }
}