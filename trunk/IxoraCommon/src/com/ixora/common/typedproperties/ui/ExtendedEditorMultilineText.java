/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;

/**
 * @author Daniel Moraru
 */
public final class ExtendedEditorMultilineText extends ExtendedEditorAbstract {
    /** Editor dialog */
	private static final class EditorDialog extends AppDialog {
		private JPanel fPanel;
		private JTextArea fTextArea;
		private String fResult;

		/**
		 * Constructor.
		 * @param parent
		 * @param orientation
		 */
		protected EditorDialog(Dialog parent, String text) {
			super(parent, VERTICAL);
			init(text);
		}
		/**
		 * Constructor.
		 * @param parent
		 * @param orientation
		 */
		protected EditorDialog(Frame parent, String text) {
			super(parent, VERTICAL);
			init(text);
		}
		/**
		 * @param text
		 */
		private void init(String text) {
			setPreferredSize(new Dimension(300, 200));
			setTitle(MessageRepository.get(Msg.COMMON_UI_TEXT_EDITOR));
			setModal(true);
			fPanel = new JPanel(new BorderLayout());
			JScrollPane sp = UIFactoryMgr.createScrollPane();
			fTextArea = UIFactoryMgr.createTextArea();
			fTextArea.setText(text);
			sp.setViewportView(fTextArea);
			fPanel.add(sp, BorderLayout.CENTER);
			buildContentPane();
		}
		/**
		 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
		 */
		protected Component[] getDisplayPanels() {
			return new Component[] {fPanel};
		}
		/**
		 * @see com.ixora.common.ui.AppDialog#getButtons()
		 */
		protected JButton[] getButtons() {
			return new JButton[] {
				UIFactoryMgr.createButton(new ActionOk() {
					public void actionPerformed(ActionEvent e) {
						fResult = fTextArea.getText();
						dispose();
					}}),
				UIFactoryMgr.createButton(new ActionCancel() {
					public void actionPerformed(ActionEvent e) {
						fResult = null;
						dispose();
					}})
			};
		}
		/**
		 * @param parent
		 * @param text
		 * @return
		 */
		public static EditorDialog showDialog(Window parent, String text) {
	        EditorDialog dlg = null;
	        if(parent instanceof Dialog) {
	            dlg = new EditorDialog((Dialog)parent, text);
	        } else if(parent instanceof Frame) {
	            dlg = new EditorDialog((Frame)parent, text);
	        }
	        if(dlg == null) {
	            return null;
	        }
	        UIUtils.centerDialogAndShow(parent, dlg);
	        return dlg;
		}
		/**
		 * @return
		 */
		public String getText() {
			return fResult;
		}
	}

	/**
     * Constructor.
     */
    public ExtendedEditorMultilineText() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.ExtendedEditor#launch(java.awt.Component, com.ixora.common.app.typedproperties.PropertyEntry)
     */
    public void launch(Component owner, PropertyEntry pe) {
        String s = (String)pe.getValue();
        EditorDialog dlg = EditorDialog.showDialog(
                SwingUtilities.getWindowAncestor(owner), s);
        String ret = dlg.getText();
        if(ret == null) {
            fireEditingCanceled();
        } else {
            fireEditingStopped(ret);
        }
    }

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#close()
	 */
	public void close() {
		; // not interested since it's implemented as a modal dialog
	}
}
