/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.PropertyEntryFile;

/**
 * @author Daniel Moraru
 */
public final class ExtendedEditorFile extends ExtendedEditorAbstract {
    /**
     * Constructor.
     */
    public ExtendedEditorFile() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.ExtendedEditor#launch(java.awt.Component, PropertyEntry)
     */
    public void launch(Component owner, PropertyEntry pe) {
        File f = (File)pe.getValue();
		JFileChooser fileChooser = new JFileChooser(f);
		PropertyEntryFile pef = (PropertyEntryFile)pe;
		fileChooser.setFileSelectionMode(
		        pef.allowsFolders() ? JFileChooser.FILES_AND_DIRECTORIES
		                : JFileChooser.FILES_ONLY);
		int returnVal = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(owner));
		File ret = null;
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ret = fileChooser.getSelectedFile();
		}
		fileChooser = null;
        if(ret == null) {
            fireEditingCanceled();
        } else {
            fireEditingStopped(ret);
        }
    }

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#close()
	 */
	public void close() {
		; // not interested since it's implemented as a modal dialog
	}
}
