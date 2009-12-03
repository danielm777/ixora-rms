/*
 * Created on 29-Oct-2004
 */
package com.ixora.rms.ui.logchooser.xml;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.utils.Utils;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.logchooser.LogChooserComponent;
import com.ixora.rms.ui.logchooser.LogChooserConfigurationConstants;
import com.ixora.rms.ui.logchooser.LogChooserHandler;

/**
 * @author Daniel Moraru
 */
public final class LogChooserHandlerXML implements LogChooserHandler {
	/** Icon for the scheme files */
	private ImageIcon icon;
	/** ViewContainer */
	private RMSViewContainer vc;

	/**
	 * Constructor.
	 * @param vc
	 */
	public LogChooserHandlerXML(RMSViewContainer vc) {
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
	    String name = null;
        if(last == null || last.getRepositoryName() == null) {
	        name = Utils.getPath("/");
	    } else {
	        name = new File(last.getRepositoryName()).getParent();
	    }
		JFileChooser fc = new JFileChooser(name);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new LogFileFilter());
		fc.setFileView(new LogFileView(icon));
		int returnVal = fc.showOpenDialog(vc.getAppFrame());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		    File f = fc.getSelectedFile();
		    if(forRead) {
			    if(!f.isFile()) {
			        return null;
			    }
		    }
		    String fileExt = Utils.getFileExtension(f);
		    String extension = ConfigurationMgr.getString(
			        LogChooserComponent.NAME,
			        LogChooserConfigurationConstants.LOG_FILE_EXTENSION);
		    String path;
		    if(fileExt == null || !fileExt.equalsIgnoreCase(extension)) {
		    	path = new File(f.getParent(), f.getName() + "." + extension).getAbsolutePath();
		    } else {
		    	path = f.getAbsolutePath();
		    }
		    return new LogRepositoryInfo(
	                   LogRepositoryInfo.Type.xml,
	                   path);
		}
    	return null;
    }
}
