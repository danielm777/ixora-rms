/*
 * Created on 28-Oct-2004
 */
package com.ixora.rms.ui.logchooser.xml;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.rms.ui.logchooser.LogChooserComponent;
import com.ixora.rms.ui.logchooser.LogChooserConfigurationConstants;
import com.ixora.rms.ui.messages.Msg;


/**
 * File view.
 */
public final class LogFileView extends FileView {
    private ImageIcon logFileIcon;

    /**
     * Constructor.
     * @param logFileIcon
     */
    public LogFileView(ImageIcon logFileIcon) {
        this.logFileIcon = logFileIcon;
    }

	/**
	 * @see javax.swing.filechooser.FileView#getDescription(java.io.File)
	 */
	public String getDescription(File f) {
		return MessageRepository.get(Msg.TEXT_LOG_FILTER_DESCRIPTION);
	}
	/**
	 * @see javax.swing.filechooser.FileView#getIcon(java.io.File)
	 */
	public Icon getIcon(File f) {
		String ext = Utils.getFileExtension(f);
		if(ext != null && ext.equalsIgnoreCase(ConfigurationMgr.getString(
		        LogChooserComponent.NAME,
		        LogChooserConfigurationConstants.LOG_FILE_EXTENSION))) {
			return logFileIcon;
		}
		return null;
	}
}