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
import java.io.File;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionBrowse;

/**
 * @author Daniel Moraru
 */
public class FormFieldFileSelector extends JPanel {
	private static final long serialVersionUID = 428910235448494741L;

	public interface JFileChooserConfigurer {
		void configure(JFileChooser fc);
	}

	private JLabel fLabel;
	private JButton fButtonBrowse;
	private JFileChooserConfigurer fConfig;
	private Window fWindow;
	private File fResult;

	/**
	 * @param frame
	 * @param initialFile
	 * @param conf
	 */
	public FormFieldFileSelector(
			Frame frame,
			File initialFile,
			JFileChooserConfigurer conf) {
		super(new BorderLayout());
		init(frame, initialFile, conf);
	}

	/**
	 * @param dlg
	 * @param initialFile
	 * @param conf
	 */
	public FormFieldFileSelector(
			Dialog dlg,
			File initialFile,
			JFileChooserConfigurer conf) {
		super(new BorderLayout());
		init(dlg, initialFile, conf);
	}

	/**
	 * @param win
	 * @param initialFile
	 * @param conf
	 */
	@SuppressWarnings("serial")
	private void init(
			Window win,
			File initialFile,
			JFileChooserConfigurer conf) {
		fConfig = conf;
		fWindow = win;
		fLabel = UIFactoryMgr.createLabel("");
		add(fLabel, BorderLayout.CENTER);
		Action browse = new ActionBrowse() {
			public void actionPerformed(ActionEvent e) {
				handleBrowse();
			}
		};
		fButtonBrowse = UIFactoryMgr.createButton(browse);
		fButtonBrowse.setMnemonic(KeyEvent.VK_UNDEFINED);
		fButtonBrowse.setMaximumSize(
				new Dimension(110, fButtonBrowse.getPreferredSize().height));
		add(fButtonBrowse, BorderLayout.WEST);

		if(initialFile != null) {
			fResult = initialFile;
			prepareLabelForFile(initialFile);
		}
	}

	/**
	 * @return
	 */
	public File getResult() {
		return fResult;
	}

	/**
	 *
	 */
	private void handleBrowse() {
		try {
			JFileChooser fc = new JFileChooser();
			fc.setAcceptAllFileFilterUsed(false);
			if(fResult != null) {
				fc.setCurrentDirectory(fResult);
			}
			if(fConfig != null) {
				fConfig.configure(fc);
			}
			int returnVal = fc.showOpenDialog(fWindow);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
			    fResult = fc.getSelectedFile();
			    prepareLabelForFile(fResult);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param f
	 */
	private void prepareLabelForFile(File f) {
	    String labelText = f.getAbsolutePath();
	    fLabel.setText("  " + labelText);
	    fLabel.setToolTipText(labelText);
	}
}
