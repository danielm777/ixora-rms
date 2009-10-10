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
import java.util.Date;

import javax.swing.JPanel;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.forms.FormFieldDateSelector;
import com.ixora.common.ui.forms.FormPanel;

/**
 * @author Daniel Moraru
 */
public class FilterEditorDialogDate extends FilterEditorDialog {
	private static final long serialVersionUID = -4136496563506453525L;
	private FormFieldDateSelector fDateSelMin;
	private FormFieldDateSelector fDateSelMax;
	private FormPanel fFormPanel;
	private JPanel mainPanel;
	private Filter fFilter;

	/**
	 * Constructor.
	 * @param parent
	 */
	public FilterEditorDialogDate(Frame parent) {
		super(parent, VERTICAL);
		init();
	}

	/**
	 * Constructor.
	 * @param parent
	 */
	public FilterEditorDialogDate(Dialog parent) {
		super(parent, VERTICAL);
		init();
	}

	/**
	 */
	private void init() {
		setModal(true);
		setTitle("Date Filter"); // TODO localize
		setPreferredSize(new Dimension(320, 170));

		fDateSelMin = new FormFieldDateSelector(this, null);
		fDateSelMax = new FormFieldDateSelector(this, null);

		fFormPanel = new FormPanel(FormPanel.VERTICAL2);
		fFormPanel.addPairs(
				new String[] {
						"Low limit", // TODO localize
						"High limit"},
				new Component[] {
						fDateSelMin,
						fDateSelMax,
		});
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(fFormPanel, BorderLayout.NORTH);

		// set focus on the filter text field when
		// the dialog is activated
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
		        fDateSelMin.requestFocusInWindow();
		    }
		});

		buildContentPane();
	}

	/**
	 * @return the filter.
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
			Date lowDate = fDateSelMin.getResult();
			Date highDate = fDateSelMax.getResult();
			if(lowDate == null && highDate == null) {
				UIUtils.showError(this, "Invalid Filter", "Filter will not be set.");
				return;
			}
			fFilter = createFilter(lowDate == null ? 0 : lowDate.getTime(),
					highDate == null ? 0 : highDate.getTime());
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Subclasses could return their own date filter here.
	 * @param dateLow
	 * @param dateHigh
	 * @return
	 */
	protected Filter createFilter(long dateLow, long dateHigh) {
		return new FilterDate(dateLow, dateHigh);
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
