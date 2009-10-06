/*
 * Created on 14-Dec-2004
 */
package com.ixora.common.ui.filter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.forms.FormPanel;

/**
 * @author Daniel Moraru
 */
public class FilterEditorDialogString extends FilterEditorDialog {
	private JCheckBox fCheckIsRegex;
	private JCheckBox fCheckIsNegative;
	private JTextField fTextFieldFilter;
	private FormPanel fFormPanel;
	private JPanel mainPanel;
	private Filter fFilter;

	/**
	 * Constructor.
	 * @param parent
	 */
	public FilterEditorDialogString(Frame parent) {
		super(parent, VERTICAL);
		init();
	}

	/**
	 * Constructor.
	 * @param parent
	 */
	public FilterEditorDialogString(Dialog parent) {
		super(parent, VERTICAL);
		init();
	}

	/**
	 */
	private void init() {
		setModal(true);
		setTitle("Text Filter"); // TODO localize
		setPreferredSize(new Dimension(320, 190));

		fCheckIsRegex = UIFactoryMgr.createCheckBox();
		fCheckIsRegex.setText("Filter is a regular expression");

		fCheckIsNegative = UIFactoryMgr.createCheckBox();
		fCheckIsNegative.setText("Negative match");

		fTextFieldFilter = UIFactoryMgr.createTextField();

		fFormPanel = new FormPanel(FormPanel.VERTICAL2);
		fFormPanel.addPairs(
				new String[] {
						"Pattern",
						"",
						""},
				new Component[] {
						fTextFieldFilter,
						fCheckIsRegex,
						fCheckIsNegative
		});
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(fFormPanel, BorderLayout.NORTH);

		// set focus on the filter text field when
		// the dialog is activated
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
		        fTextFieldFilter.requestFocusInWindow();
		    }
		});

		buildContentPane();
	}

	/**
	 * @return the filter regex.
	 */
	public Filter getFilter() {
		return fFilter;
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {mainPanel};
	}

	/**
	 * @see com.ixora.common.ui.filter.FilterEditorDialog#handleOk()
	 */
	protected void handleOk() {
		try {
			boolean regex = fCheckIsRegex.isSelected();
			String filterText = fTextFieldFilter.getText();
			if(filterText == null || filterText.trim().length() == 0) {
				UIUtils.showError(this,
						// TODO localize
						"Invalid Filter",
						"Filter will not be set.");
				return;
			}
			if(regex) {
				fFilter = createRegexFilter(filterText.trim(), fCheckIsNegative.isSelected());
			} else {
				fFilter = createStringFilter(filterText.trim(), fCheckIsNegative.isSelected());
			}
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Subclasses could return here their own filter implementation.
	 * @param text
	 * @param negative
	 * @return
	 */
	protected Filter createRegexFilter(String text, boolean negative) {
		return new FilterRegex(text, negative);
	}

	/**
	 * Subclasses could return here their own filter implementation.
	 * @param text
	 * @param negative
	 * @return
	 */
	protected Filter createStringFilter(String text, boolean negative) {
		return new FilterString(text, negative);
	}

	/**
	 * @see com.ixora.common.ui.filter.FilterEditorDialog#handleCancel()
	 */
	protected void handleCancel() {
		try {
			fFilter = null;
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
