/*
 * Created on 14-Dec-2004
 */
package com.ixora.common.ui.filter;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;

/**
 * @author Daniel Moraru
 */
public abstract class FilterEditorDialog extends AppDialog {
	private static final long serialVersionUID = 8076496850285971626L;

	/**
	 * Constructor.
	 * @param parent
	 * @param orientation
	 */
	protected FilterEditorDialog(Frame parent, int orientation) {
		super(parent, orientation);
	}

	/**
	 * Constructor.
	 * @param parent
	 * @param orientation
	 */
	protected FilterEditorDialog(Dialog parent, int orientation) {
		super(parent, orientation);
	}

	/**
	 * @return the filter.
	 */
	public abstract Filter getFilter();

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[] {
			new JButton(new ActionOk() {
				public void actionPerformed(ActionEvent e) {
					handleOk();
				}
			}),
			new JButton(new ActionCancel() {
				public void actionPerformed(ActionEvent e) {
					handleCancel();
				}
			})
		};
	}

	/**
	 * Handles Ok button pressed.
	 */
	protected abstract void handleOk();

	/**
	 * Handles Ok button pressed.
	 */
	protected abstract void handleCancel();
}
