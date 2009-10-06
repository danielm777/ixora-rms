/**
 * 04-Feb-2006
 */
package com.ixora.common.ui.forms;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ixora.common.calendar.JCalendarDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
public class FormFieldDateSelector extends JPanel {
	private JLabel fLabel;
	private JButton fButtonSelectDate;
	private Window fWindow;
	private Date fResult;
	private Date fInitialDate;

	private class ActionSelectDate extends AbstractAction {
		/**
		 * Constructor.
		 */
		public ActionSelectDate() {
			super();
			// TODO localize
			UIUtils.setUsabilityDtls("Select Date", this);
			//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("calendar.gif"));
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleSelectDate();
		}
	}

	/**
	 * @param frame
	 * @param initialDate
	 */
	public FormFieldDateSelector(
			Frame frame,
			Date initialDate) {
		super(new BorderLayout());
		init(frame, initialDate);
	}

	/**
	 * @param dlg
	 * @param initialDate
	 */
	public FormFieldDateSelector(
			Dialog dlg,
			Date initialDate) {
		super(new BorderLayout());
		init(dlg, initialDate);
	}

	/**
	 * @param win
	 * @param initialDate
	 */
	private void init(Window win, Date initialDate) {
		fWindow = win;
		fLabel = UIFactoryMgr.createLabel("");
		add(fLabel, BorderLayout.CENTER);
		fButtonSelectDate = UIFactoryMgr.createButton(new ActionSelectDate());
		fButtonSelectDate.setMnemonic(KeyEvent.VK_UNDEFINED);
		fButtonSelectDate.setMaximumSize(
				new Dimension(110, fButtonSelectDate.getPreferredSize().height));
		add(fButtonSelectDate, BorderLayout.WEST);

		if(initialDate != null) {
			fInitialDate = initialDate;
			fResult = initialDate;
			prepareLabelForDate(initialDate);
		}
	}

	/**
	 * @return
	 */
	public Date getResult() {
		return fResult;
	}

	/**
	 *
	 */
	private void handleSelectDate() {
		try {
			JCalendarDialog fc = JCalendarDialog.showDialog(fWindow, true,
					fResult == null ? (fInitialDate == null ? new Date() : fInitialDate) : fResult);
			fResult = fc.getDate();
			if(fResult != null) {
				prepareLabelForDate(fResult);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param d
	 */
	private void prepareLabelForDate(Date d) {
	    String labelText = (d == null ? "" : d.toString());
	    fLabel.setText("  " + labelText);
	    fLabel.setToolTipText(labelText);
	}
}
