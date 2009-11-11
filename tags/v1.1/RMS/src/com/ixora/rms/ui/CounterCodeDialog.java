/**
 * 30-Jan-2006
 */
package com.ixora.rms.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
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
public class CounterCodeDialog extends AppDialog {
	private static final long serialVersionUID = -210661911281325950L;
	private String fCode;
	private String fName;
	private String fDescription;
	private FormPanel fForm;
	private JLabel fLabelID;
	private JTextField fTextFieldName;
	private JTextField fTextFieldDesc;
	private JTextField fTextFieldCode;


	/**
	 * @param parent
	 */
	public CounterCodeDialog(Frame parent, String id) {
		super(parent, VERTICAL);
		setTitle("Counter Code");
		setModal(true);
		setResizable(false);
		fForm = new FormPanel(FormPanel.VERTICAL1);

		fLabelID = UIFactoryMgr.createLabel(id);
		fTextFieldCode = UIFactoryMgr.createTextField();
		fTextFieldCode.setText("return " + id + ";");
		fTextFieldCode.setPreferredSize(new Dimension(300, fTextFieldCode.getPreferredSize().height));
		fTextFieldName = UIFactoryMgr.createTextField();
		fTextFieldName.setText("Modified counter");
		fTextFieldDesc = UIFactoryMgr.createTextField();
		fTextFieldDesc.setText("Modified counter description");

		fForm.addPairs(
				new String[]{
					"Id",
					"Name",
					"Description",
					"Code"},
				new Component[]{
					fLabelID,
					fTextFieldName,
					fTextFieldDesc,
					fTextFieldCode
				});

		buildContentPane();
	}

	/**
	 * @return
	 */
	public String getCode() {
		return fCode;
	}

	/**
	 * @return
	 */
	public String getCounterName() {
		return fName;
	}

	/**
	 * @return
	 */
	public String getCounterDescription() {
		return fDescription;
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
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[]{
			UIFactoryMgr.createButton(new ActionOk(){
				public void actionPerformed(ActionEvent e) {
					handleOk();
				}
			}),
			UIFactoryMgr.createButton(new ActionCancel(){
				public void actionPerformed(ActionEvent e) {
					fCode = null;
					fName = null;
					fDescription = null;
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
			String code = fTextFieldCode.getText();
			if(Utils.isEmptyString(code)) {
				throw new RMSException("Code is required.");
			}
			fCode = code;
			String name = fTextFieldName.getText();
			if(Utils.isEmptyString(name)) {
				throw new RMSException("Name is required.");
			}
			fName = name;
			String desc = fTextFieldDesc.getText();
			fDescription = desc;
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
