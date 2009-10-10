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

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ixora.common.exception.AppException;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class FilterEditorDialogNumber extends FilterEditorDialog {
	private static final long serialVersionUID = -9138562430352958457L;
	private JTextField fMin;
	private JTextField fMax;
	private FormPanel fFormPanel;
	private JPanel mainPanel;
	private Filter fFilter;

	/**
	 * Constructor.
	 * @param parent
	 */
	public FilterEditorDialogNumber(Frame parent) {
		super(parent, VERTICAL);
		init();
	}

	/**
	 * Constructor.
	 * @param parent
	 */
	public FilterEditorDialogNumber(Dialog parent) {
		super(parent, VERTICAL);
		init();
	}

	/**
	 * Constructor.
	 * @param parent
	 */
	private void init() {
		setModal(true);
		setTitle("Number Filter"); // TODO localize
		setPreferredSize(new Dimension(320, 170));

		fMin = UIFactoryMgr.createTextField();
		fMax = UIFactoryMgr.createTextField();

		fFormPanel = new FormPanel(FormPanel.VERTICAL2);
		fFormPanel.addPairs(
				new String[] {
						"Low limit",
						"High limit"},
				new Component[] {
						fMin,
						fMax,
		});
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(fFormPanel, BorderLayout.NORTH);

		// set focus on the filter text field when
		// the dialog is activated
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
		        fMin.requestFocusInWindow();
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
			String lowText = fMin.getText();
			String highText = fMax.getText();
			Number low = null;
			Number high = null;
			try {
				low = Utils.isEmptyString(lowText) ? null : Double.parseDouble(lowText);
			} catch(NumberFormatException e) {
				throw new AppException("Invalid number: " + lowText);
			}
			try {
				high = Utils.isEmptyString(highText) ? null : Double.parseDouble(highText);
			} catch(NumberFormatException e) {
				throw new AppException("Invalid number: " + highText);
			}
			if(low == null && high == null) {
				UIUtils.showError(this, "Invalid Filter", "Filter will not be set.");
				return;
			}
			fFilter = createFilter(low, high);
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Subclasses could return their own date filter here.
	 * @param low
	 * @param high
	 * @return
	 */
	protected Filter createFilter(Number low, Number high) {
		return new FilterNumber(low, high);
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
