/*
 * Created on 29-Oct-2004
 */
package com.ixora.rms.ui.logchooser.db;

import javax.swing.ImageIcon;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.logchooser.LogChooserHandler;

/**
 * @author Daniel Moraru
 */
public final class LogChooserHandlerDB implements LogChooserHandler {
	/** Icon for the scheme files */
	private ImageIcon icon;
	/** ViewContainer */
	private RMSViewContainer vc;

	/**
	 * Constructor.
	 * @param vc
	 */
	public LogChooserHandlerDB(RMSViewContainer vc) {
		this.icon = UIConfiguration.getIcon("log_file.gif");
		this.vc = vc;
	}

    /**
     * @see com.ixora.rms.ui.logchooser.LogChooserHandler#getLogInfoForRead(com.ixora.rms.logging.LogRepositoryInfo)
     */
    public LogRepositoryInfo getLogInfoForRead(LogRepositoryInfo last) {
        return getLogFile(last, true);
    }

    /**
     * @see com.ixora.rms.ui.logchooser.LogChooserHandler#getLogInfoForWrite(com.ixora.rms.logging.LogRepositoryInfo)
     */
    public LogRepositoryInfo getLogInfoForWrite(LogRepositoryInfo last) {
        return getLogFile(last, false);
    }

    /**
     * @param last
     * @param forRead
     * @return
     */
    private LogRepositoryInfo getLogFile(LogRepositoryInfo last, boolean forRead) {
	    return new LogRepositoryInfo(
	                   LogRepositoryInfo.TYPE_XML,
	                   "");
    }
}
