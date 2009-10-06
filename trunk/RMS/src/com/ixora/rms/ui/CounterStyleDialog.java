/**
 * 30-Jan-2006
 */
package com.ixora.rms.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JTextField;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class CounterStyleDialog extends AppDialog {
	private Double fMax;
	private Double fMin;
	private FormPanel fForm;
	private JTextField fTextFieldMax;
	private JTextField fTextFieldMin;


	/**
	 * @param parent
	 */
	public CounterStyleDialog(Frame parent) {
		super(parent, VERTICAL);
		setTitle("Counter Style");
		setModal(true);
		setResizable(false);
		fForm = new FormPanel(FormPanel.VERTICAL1);

//		NumberFormatter formatter = new NumberFormatter();
//		formatter.setAllowsInvalid(false);
		fTextFieldMax = UIFactoryMgr.createTextField();
		fTextFieldMax.setPreferredSize(new Dimension(100, fTextFieldMax.getPreferredSize().height));
//		formatter = new NumberFormatter();
//		formatter.setAllowsInvalid(false);
		fTextFieldMin = UIFactoryMgr.createTextField();

		fForm.addPairs(
				new String[]{
					"Min possible value",
					"Max possible value"},
				new Component[]{
					fTextFieldMin,
					fTextFieldMax
				});

		buildContentPane();
	}

	/**
	 * @return
	 */
	public Double getMax() {
		return fMax;
	}

	/**
	 * @return
	 */
	public Double getMin() {
		return fMin;
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {fForm};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[]{
			UIFactoryMgr.createButton(new ActionOk(){
				public void actionPerformed(ActionEvent e) {
					handleOk();
				}
			}),
			UIFactoryMgr.createButton(new ActionCancel(){
				public void actionPerformed(ActionEvent e) {
					fMax = null;
					fMin = null;
					dispose();
				}
			})
		};
	}

	/**
	 *
	 */
	private void handleOk() {
		try {
			String min = fTextFieldMin.getText();
			if(!Utils.isEmptyString(min)) {
				try {
					fMin = Double.valueOf(min);
				} catch(NumberFormatException e) {
					throw new RMSException("Min must be a number");
				}
			}
			String max = fTextFieldMax.getText();
			if(!Utils.isEmptyString(max)) {
				try {
					fMax = Double.valueOf(max);
				} catch(NumberFormatException e) {
					throw new RMSException("Max must be a number");
				}
			}
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
