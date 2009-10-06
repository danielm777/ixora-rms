/**
 * 04-Feb-2006
 */
package com.ixora.rms.ui.exporter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormFieldFileSelector;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class HTMLGeneratorDialog extends AppDialog {
	private HTMLGeneratorSettings fResult;
	private FormPanel fForm;
	private JSpinner fSpinner;
	private FormFieldFileSelector fFileSelectorPanel;
	private JCheckBox fEnableRefreshCheckbox;
	private JPanel fPanelContent;

	private class EventHandler implements ItemListener {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			Object o = e.getItemSelectable();
			if(o == fEnableRefreshCheckbox) {
				handleRefreshCheckboxChanged();
			}
		}
	}

	/**
	 * @param parent
	 * @param settings
	 */
	public HTMLGeneratorDialog(Frame parent, HTMLGeneratorSettings settings) {
		super(parent, VERTICAL);
		setModal(true);
		setTitle("HTML Generation Configuration"); // TODO localize
		fForm = new FormPanel(FormPanel.VERTICAL1);
		SpinnerNumberModel model = new SpinnerNumberModel(60, 10, 3600, 1);
		fSpinner = UIFactoryMgr.createSpinner(model);
		((JSpinner.DefaultEditor)fSpinner.getEditor()).getTextField().setEditable(false);
		fSpinner.setMaximumSize(new Dimension(30, fSpinner.getPreferredSize().height));
		fEnableRefreshCheckbox = UIFactoryMgr.createCheckBox();
		fEnableRefreshCheckbox.setText("Auto refresh"); // TODO localize
		File siteRoot = null;
		if(settings != null) {
			siteRoot = settings.getSiteRoot();
		}
		fFileSelectorPanel = new FormFieldFileSelector(
			parent, siteRoot,
			new FormFieldFileSelector.JFileChooserConfigurer() {
				public void configure(JFileChooser fc) {
					fc.setDialogTitle("Select Output Folder");
					fc.setApproveButtonText("Select");
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setFileFilter(
						new FileFilter(){
							public boolean accept(File f) {
								return f.isDirectory();
							}
							public String getDescription() {
								return "Output folder (.*)";
							}
						});
				}
		});

		JPanel panelSpinner = new JPanel(new BorderLayout());
		panelSpinner.add(fSpinner, BorderLayout.WEST);
		panelSpinner.add(fEnableRefreshCheckbox, BorderLayout.CENTER);

		fFileSelectorPanel.setPreferredSize(
				new Dimension(300, fFileSelectorPanel.getPreferredSize().height));

		fForm.addPairs(
				new String[]{
					// TODO localize
					"Select Output Folder",
					"Refresh Interval (seconds)"
				},
				new Component[]{
					fFileSelectorPanel,
					panelSpinner
				});

		fPanelContent = new JPanel(new BorderLayout());
		fPanelContent.add(fForm, BorderLayout.NORTH);

		fSpinner.setEnabled(false);
		fEnableRefreshCheckbox.setSelected(false);

		fEnableRefreshCheckbox.addItemListener(new EventHandler());

		if(settings != null) {
			int ref = settings.getRefreshInterval();
			if(ref > 0) {
				fSpinner.setValue(ref);
				fEnableRefreshCheckbox.setSelected(true);
			}
		}

		buildContentPane();
	}

	/**
	 * @return
	 */
	public HTMLGeneratorSettings getResult() {
		return fResult;
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
			File file = fFileSelectorPanel.getResult();
			if(file == null) {
				throw new RMSException("Please select an output folder.");
			}
			int refresh = 0;
			if(fEnableRefreshCheckbox.isSelected()) {
				refresh = ((Integer)fSpinner.getValue()).intValue();
			}
			fResult = new HTMLGeneratorSettings(file, refresh);
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleRefreshCheckboxChanged() {
		try {
			if(fEnableRefreshCheckbox.isSelected()) {
				fSpinner.setEnabled(true);
			} else {
				fSpinner.setEnabled(false);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
