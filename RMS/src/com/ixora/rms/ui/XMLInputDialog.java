/**
 * 17-Aug-2005
 */
package com.ixora.rms.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.editor.EmbeddedEditorDialog;
import com.ixora.common.ui.editor.SyntaxHighlightXML;

/**
 * Dialog used to get or view XML data.
 * @author Daniel Moraru
 */
public final class XMLInputDialog extends EmbeddedEditorDialog {

	/**
	 * Callback.
	 */
	public interface Callback {
		/**
		 * @param data
		 * @throws Exception
		 */
		void handleXMLInput(String data) throws Exception;
	}

	private Callback fCallback;

	/**
	 * @param parent
	 * @param cb can be null if not in input mode
	 */
	public XMLInputDialog(JFrame parent, Callback cb) {
		super(parent, null,
				// register a null save callback
				// so that save ops will be disabled
				null,
				new SyntaxHighlightXML());
		init(parent, cb, null);
	}

	/**
	 * @param parent
	 * @param dataToView
	 */
	public XMLInputDialog(JFrame parent, String dataToView) {
		super(parent, null,
				// register a null save callback
				// so that save ops will be disabled
				null,
				new SyntaxHighlightXML());
		init(parent, null, dataToView);
	}

	/**
	 * @param parent
	 * @param cb
	 * @param initialData
	 */
	private void init(JFrame parent, Callback cb, String initialData) {
		fCallback = cb;
		fEditor.getToolBar().setVisible(false);
		// TODO localize
		if(fCallback == null) { // view mode
			setTitle("XML Data");
			this.fEditor.getTextPane().setEditable(false);
		} else { // input mode
			setTitle("Input XML Data");
		}
		setModal(true);
		fEditor.setPreferredSize(new Dimension(500, 400));
		if(initialData != null) {
			fEditor.getTextPane().setText(initialData);
		}
		buildContentPane();
	}

	/**
	 * @see com.ixora.common.ui.editor.EmbeddedEditorDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		if(fCallback != null) {
			return new JButton[]{
					UIFactoryMgr.createButton(new ActionOk() {
						public void actionPerformed(ActionEvent e) {
							handleOk();
						}}),
					UIFactoryMgr.createButton(new ActionCancel() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					})};
		} else {
			return new JButton[]{
					UIFactoryMgr.createButton(new ActionClose() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					})};
		}
	}

	/**
	 *
	 */
	private void handleOk() {
		try {
			if(fCallback != null) {
				fCallback.handleXMLInput(fEditor.getTextPane().getText());
			}
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
