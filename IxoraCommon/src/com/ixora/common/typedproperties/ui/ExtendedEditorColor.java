/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.typedproperties.PropertyEntry;

/**
 * @author Daniel Moraru
 */
public final class ExtendedEditorColor extends ExtendedEditorAbstract {

    /**
     * Constructor.
     */
    public ExtendedEditorColor() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.ExtendedEditor#launch(java.awt.Component, com.ixora.common.app.typedproperties.PropertyEntry)
     */
    public void launch(Component owner, PropertyEntry pe) {
        Color c = (Color)pe.getValue();
        Color ret = JColorChooser.showDialog(
				SwingUtilities.getWindowAncestor(owner),
				MessageRepository.get(Msg.COMMON_UI_TEXT_CHOOSE_COLOR),
				c == null ? Color.WHITE : c);
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
