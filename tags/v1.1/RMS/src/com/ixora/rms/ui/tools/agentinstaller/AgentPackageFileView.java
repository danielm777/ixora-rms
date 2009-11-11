/*
 * Created on 28-Oct-2004
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.utils.Utils;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;


/**
 * Agent package file view.
 */
public final class AgentPackageFileView extends FileView {
	private ImageIcon agentPackageFileIcon;

    /**
     * Constructor.
     * @param icon
     */
    public AgentPackageFileView() {
        this.agentPackageFileIcon = UIConfiguration.getIcon("agent_package_file.gif");
    }

	/**
	 * @see javax.swing.filechooser.FileView#getDescription(java.io.File)
	 */
	public String getDescription(File f) {
		return MessageRepository.get(AgentInstallerComponent.NAME, Msg.TEXT_AGENT_PACKAGE_FILE_DESCRIPTION);
	}
	/**
	 * @see javax.swing.filechooser.FileView#getIcon(java.io.File)
	 */
	public Icon getIcon(File f) {
		String ext = Utils.getFileExtension(f);
		if(ext != null && ext.equalsIgnoreCase(AgentPackageFilter.AGENT_PACKAGE_FILE_EXTENSION)) {
			return agentPackageFileIcon;
		}
		return null;
	}
}
