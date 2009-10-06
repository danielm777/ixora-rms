package com.ixora.common.ui.editor;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledDocument;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.exception.FailedToSaveDocument;

/**
 * EmbeddedEditorDialog.
 */
public class EmbeddedEditorDialog extends AppDialog {
	/**
	 * The call back implementing the actual operations.
	 */
	public interface SaveCallback {
		/**
		 * Saves the document.
		 * @param dlg
		 * @param doc
		 * @throws FailedToSaveDocument
		 */
		boolean save(JDialog dlg, StyledDocument doc) throws FailedToSaveDocument;
		/**
		 * Saves the document.
		 * @param dlg
		 * @param doc
		 * @throws Exception
		 */
		boolean saveAs(JDialog dlg, StyledDocument doc) throws FailedToSaveDocument;
	}

	/** Editor */
	protected EmbeddedEditorPanel fEditor;
	/** Document has changed since last save */
	protected boolean fDocumentChanged;
	/** Save callback */
	protected SaveCallback fCallback;

	/**
	 * Event handler.
	 */
	private final class EventHandler  extends WindowAdapter
			implements EmbeddedEditorPanel.Listener, EmbeddedEditorPanel.SaveCallback, PropertyChangeListener {
		/**
		 * @see com.ixora.common.ui.EmbeddedEditorPanel.Listener#documentChanged()
		 */
		public void documentChanged() {
			handleDocumentChanged();
		}
		/**
		 * @see com.ixora.common.ui.EmbeddedEditorPanel.Listener#documentSaved()
		 */
		public void documentSaved() {
			handleDocumentSaved();
		}
		/**
		 * @see com.ixora.common.ui.editor.EmbeddedEditorPanel.SaveCallback#save(javax.swing.text.StyledDocument)
		 */
		public boolean save(StyledDocument doc) throws FailedToSaveDocument {
			return fCallback.save(EmbeddedEditorDialog.this, doc);
		}
		/**
		 * @see com.ixora.common.ui.editor.EmbeddedEditorPanel.SaveCallback#saveAs(javax.swing.text.StyledDocument)
		 */
		public boolean saveAs(StyledDocument doc) throws FailedToSaveDocument {
			return fCallback.saveAs(EmbeddedEditorDialog.this, doc);
		}
		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			// this is to disable the modified flag when all changes are undone
			if(evt.getSource() == fEditor.getAction(EmbeddedEditorPanel.UNDO)) {
				if("enabled".equals(evt.getPropertyName())
						&& evt.getNewValue().equals(Boolean.FALSE)) {
					handleDocumentSaved();
				}
			}
		}

		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent e) {
			handleWindowClosing();
		}
	}

	/**
	 * Constructor.
	 * @param parent the parent of this dialog
	 * @param doc the initial document
	 * @param cb the callback implementing document saving
	 */
	public EmbeddedEditorDialog(
			JFrame parent,
			StyledDocument doc,
			SaveCallback cb,
			EditorSyntaxHighlighter highlighter) {
		super(parent, VERTICAL);
		init(parent, doc, null, cb, highlighter);
	}

	/**
	 * Constructor.
	 * @param parent the parent of this dialog
	 * @param doc the initial document
	 * @param editorKit the editor kit capable of editing the given document
	 * @param cb the callback implementing document saving
	 */
	public EmbeddedEditorDialog(
			JFrame parent,
			StyledDocument doc,
			DefaultEditorKit editorKit,
			SaveCallback cb,
			EditorSyntaxHighlighter highlighter) {
		super(parent, VERTICAL);
		init(parent, doc, editorKit, cb, highlighter);
	}

	/**
	 * @return
	 */
	public EmbeddedEditorPanel getEditor() {
		return fEditor;
	}

	/**
	 * When the window is activated, request focus in the editor.
	 * @see java.awt.Window#processWindowEvent(java.awt.event.WindowEvent)
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if(e.getID() == WindowEvent.WINDOW_ACTIVATED) {
			fEditor.requestFocus();
		}
	}

	/**
	 * Initializes this dialog.
	 * @param parent
	 * @param doc
	 * @param editorKit
	 * @param cb if null save actions will be disabled
	 */
	private void init(JFrame parent,
			StyledDocument doc,
			DefaultEditorKit editorKit,
			SaveCallback cb,
			EditorSyntaxHighlighter highlighter) {
		EventHandler ev = new EventHandler();
		this.fCallback = cb;
		fEditor = new EmbeddedEditorPanel(parent, doc, cb == null ? null : ev, ev, highlighter, false);
		setContentPane(fEditor);
		this.fDocumentChanged = false;
		fEditor.getAction(EmbeddedEditorPanel.UNDO).addPropertyChangeListener(ev);
		if(cb != null) {
			addWindowListener(ev);
		}
	}

	/**
	 * Handles document changed events.
	 */
	protected void handleDocumentChanged() {
		try {
			if(!fDocumentChanged && fCallback != null) {
				fDocumentChanged = true;
				String title = getTitle();
				if(title != null) {
					int idx = title.indexOf('*');
					if(idx < 0) {
						setTitle(getTitle() + "*");
					}
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles document saved events.
	 */
	protected void handleDocumentSaved() {
		try {
			if(fDocumentChanged) {
				fDocumentChanged = false;
				String title = getTitle();
				int idx = title.indexOf('*');
				if(idx > 0) {
					setTitle(title.substring(0, idx));
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	protected void handleWindowClosing() {

		try {
			if(fDocumentChanged && fCallback != null) {
				boolean save = UIUtils.getBooleanYesNoInput(this,
						MessageRepository.get(Msg.COMMON_UI_TITLE_SAVE_CHANGES),
						MessageRepository.get(Msg.COMMON_UI_TEXT_SAVE_CHANGES));
				if(save) {
					fCallback.save(this, this.fEditor.getTextPane().getStyledDocument());
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fEditor};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[]{
			UIFactoryMgr.createButton(new ActionCancel() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}

			}),
		};
	}
}
