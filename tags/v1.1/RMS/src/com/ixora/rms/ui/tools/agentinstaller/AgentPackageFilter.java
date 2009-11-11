/*
 * Created on 28-Oct-2004
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * File chooser.
 */
public final class AgentPackageFilter extends FileFilter {
	public static final String AGENT_PACKAGE_FILE_EXTENSION = "agent";
	/**
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if(f.isDirectory()) {
			return true;
		}
		String ext = Utils.getFileExtension(f);
		if(ext != null && ext.equalsIgnoreCase(AGENT_PACKAGE_FILE_EXTENSION)) {
			return true;
		}
		return false;
	}
	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return MessageRepository.get(AgentInstallerComponent.NAME, Msg.TEXT_AGENT_PACKAGE_FILE_DESCRIPTION);
	}
}