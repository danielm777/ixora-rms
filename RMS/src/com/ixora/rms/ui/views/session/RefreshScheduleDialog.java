/**
 * 08-Jul-2006
 */
package com.ixora.rms.ui.views.session;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;

/**
 * @author Daniel Moraru
 */
public class RefreshScheduleDialog extends AppDialog {
	private Integer fResult;
	private JSpinner fSpinner;
	private JPanel fPanelContent;
	private JCheckBox fCheckBoxDisable;

	/**
	 * @param parent
	 * @param initialInterval
	 */
	public RefreshScheduleDialog(Frame parent, Integer initialInterval) {
		super(parent, VERTICAL);
		setTitle("Entity Refresh Period"); // TODO localize
		setModal(true);
		SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 100, 1);
		fSpinner = UIFactoryMgr.createSpinner(model);
		fSpinner.setMaximumSize(new Dimension(30, fSpinner.getPreferredSize().height));

		fCheckBoxDisable = UIFactoryMgr.createCheckBox();
		fCheckBoxDisable.setText("Disable auto refresh"); // TODO localize
		fCheckBoxDisable.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				handleDisableCheckboxEvent();
			}
		});

		JPanel panelSpinner = new JPanel(new BorderLayout());
		panelSpinner.add(fSpinner, BorderLayout.WEST);
		panelSpinner.add(fCheckBoxDisable, BorderLayout.CENTER);

		FormPanel form = new FormPanel(FormPanel.VERTICAL2);
		form.addPairs(
				new String[]{
					// TODO localize
					"Select the refresh period as multiple of the sampling interval",
				},
				new Component[]{
					panelSpinner
				});
		fPanelContent = new JPanel(new BorderLayout());
		fPanelContent.add(form, BorderLayout.NORTH);

		if(initialInterval != null) {
			fSpinner.setValue(initialInterval);
			if(initialInterval == 0) {
				fCheckBoxDisable.setSelected(true);
				handleDisableCheckboxEvent();
			}
		} else {
			fCheckBoxDisable.setSelected(true);
			handleDisableCheckboxEvent();
		}

		buildContentPane();
	}


	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fPanelContent};
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
					fResult = null;
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
			fSpinner.commitEdit();
			fResult = ((Integer)fSpinner.getValue()).intValue();
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @return result.
	 */
	public Integer getResult() {
		return fResult;
	}

	/**
	 * @param ev
	 */
	private void handleDisableCheckboxEvent() {
		try {
			if(fCheckBoxDisable.isSelected()) {
				fSpinner.setEnabled(false);
				fSpinner.setValue(new Integer(0));
			} else {
				fSpinner.setEnabled(true);
				fSpinner.setValue(new Integer(1));
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}

	}
}
