/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;
import java.util.Date;

import javax.swing.SwingUtilities;

import com.ixora.common.calendar.JCalendarDialog;
import com.ixora.common.typedproperties.PropertyEntry;

/**
 * @author Daniel Moraru
 */
public final class ExtendedEditorDate extends ExtendedEditorAbstract {
    /**
     * Constructor.
     */
    public ExtendedEditorDate() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.ExtendedEditor#launch(java.awt.Component, com.ixora.common.app.typedproperties.PropertyEntry)
     */
    public void launch(Component owner, PropertyEntry pe) {
        Date d = (Date)pe.getValue();
        JCalendarDialog calendarDialog = JCalendarDialog.showDialog(
                SwingUtilities.getWindowAncestor(owner), true, d);
        Date ret = calendarDialog.getDate();
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
