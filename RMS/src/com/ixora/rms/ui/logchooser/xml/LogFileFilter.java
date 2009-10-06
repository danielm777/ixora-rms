/*
 * Created on 28-Oct-2004
 */
package com.ixora.rms.ui.logchooser.xml;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.rms.ui.logchooser.LogChooserComponent;
import com.ixora.rms.ui.logchooser.LogChooserConfigurationConstants;
import com.ixora.rms.ui.messages.Msg;


/**
 * File chooser.
 */
public final class LogFileFilter extends FileFilter {
	/**
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if(f.isDirectory()) {
			return true;
		}
		String ext = Utils.getFileExtension(f);
		if(ext != null && ext.equalsIgnoreCase(ConfigurationMgr.getString(
		        LogChooserComponent.NAME,
		        LogChooserConfigurationConstants.LOG_FILE_EXTENSION))) {
			return true;
		}
		return false;
	}
	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return MessageRepository.get(Msg.TEXT_LOG_FILTER_DESCRIPTION);
	}
}