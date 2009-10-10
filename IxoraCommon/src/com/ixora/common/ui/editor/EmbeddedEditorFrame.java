package com.ixora.common.ui.editor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledDocument;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.exception.FailedToSaveDocument;

/**
 * EmbeddedEditorDialog.
 */
public class EmbeddedEditorFrame extends JFrame {
	private static final long serialVersionUID = 5640889903083753767L;

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
		boolean save(JFrame dlg, StyledDocument doc) throws FailedToSaveDocument;
		/**
		 * Saves the document.
		 * @param dlg
		 * @param doc
		 * @throws Exception
		 */
		boolean saveAs(JFrame dlg, StyledDocument doc) throws FailedToSaveDocument;
	}

	/** Editor */
	protected EmbeddedEditorPanel fEditor;
	/** Document has changed since last save */
	protected boolean fDocumentChanged;
	/** Callback */
	protected SaveCallback fCallback;

	/**
	 * Event handler.
	 */
	private final class EventHandler extends WindowAdapter
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
			return fCallback.save(EmbeddedEditorFrame.this, doc);
		}
		/**
		 * @see com.ixora.common.ui.editor.EmbeddedEditorPanel.SaveCallback#saveAs(javax.swing.text.StyledDocument)
		 */
		public boolean saveAs(StyledDocument doc) throws FailedToSaveDocument {
			return fCallback.saveAs(EmbeddedEditorFrame.this, doc);
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
	 * @param doc the initial document
	 * @param cb the callback implementing document saving
	 * @param saveEnabled
	 */
	public EmbeddedEditorFrame(
			StyledDocument doc,
			SaveCallback cb,
			EditorSyntaxHighlighter highlighter, boolean saveEnabled) {
		super();
		init(doc, null, cb, highlighter, saveEnabled);
	}

	/**
	 * Constructor.
	 * @param doc the initial document
	 * @param editorKit the editor kit capable of editing the given document
	 * @param cb the callback implementing document saving
	 * @param saveEnabled save enabled initially
	 */
	public EmbeddedEditorFrame(
			StyledDocument doc,
			DefaultEditorKit editorKit,
			SaveCallback cb,
			EditorSyntaxHighlighter highlighter, boolean saveEnabled) {
		init(doc, editorKit, cb, highlighter, saveEnabled);
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
	 * @param cb
	 * @param saveEnabled
	 */
	private void init(StyledDocument doc,
			DefaultEditorKit editorKit,
			SaveCallback cb,
			EditorSyntaxHighlighter highlighter,
			boolean saveEnabled) {
		EventHandler ev = new EventHandler();
		this.fCallback = cb;
		fEditor = new EmbeddedEditorPanel(this, doc, ev, ev, highlighter, saveEnabled);
		setContentPane(fEditor);
		this.fDocumentChanged = saveEnabled;
		fEditor.getAction(EmbeddedEditorPanel.UNDO).addPropertyChangeListener(ev);
		addWindowListener(ev);
	}

	/**
	 * Handles document changed events.
	 */
	protected void handleDocumentChanged() {
		try {
			if(!fDocumentChanged && fEditor.getAction(EmbeddedEditorPanel.SAVE).isEnabled()) {
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
			UIExceptionMgr.userException(this, e);
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
			UIExceptionMgr.userException(this, e);
		}
	}

    /**
	 *
	 */
	protected void handleWindowClosing() {
		try {
			if(fDocumentChanged && fEditor.getAction(EmbeddedEditorPanel.SAVE).isEnabled()) {
				boolean save = UIUtils.getBooleanYesNoInput(this,
						MessageRepository.get(Msg.COMMON_UI_TITLE_SAVE_CHANGES),
						MessageRepository.get(Msg.COMMON_UI_TEXT_SAVE_CHANGES));
				if(save) {
					fCallback.save(EmbeddedEditorFrame.this, this.fEditor.getTextPane().getStyledDocument());
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(this, e);
		}
	}
}
