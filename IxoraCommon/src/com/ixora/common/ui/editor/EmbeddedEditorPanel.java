package com.ixora.common.ui.editor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.exception.FailedToSaveDocument;

/**
 * EmbeddedEditorPanel.
 * @author Daniel Moraru
 * @author Cristian Costache
 */
public class EmbeddedEditorPanel extends JPanel {
	public static final int SAVE = 0;
	public static final int SAVE_AS = 1;
	public static final int CUT = 2;
	public static final int COPY = 3;
	public static final int PASTE = 4;
	public static final int UNDO = 5;
	public static final int REDO = 6;

	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the document has changed.
		 *
		 */
		void documentChanged();
		/**
		 * Invoked after the document has been succesfully saved.
		 */
		void documentSaved();
	}

	/**
	 * The call back implementing the actual operations.
	 */
	public interface SaveCallback {
		/**
		 * Saves the document.
		 * @param doc
		 * @return true if the document was successfuly saved
		 * @throws FailedToSaveDocument
		 */
		boolean save(StyledDocument doc) throws FailedToSaveDocument;
		/**
		 * Saves the document.
		 * @param doc
		 * @return true if the document was successfuly saved
		 * @throws Exception
		 */
		boolean saveAs(StyledDocument doc) throws FailedToSaveDocument;
	}

    private JFrame owner;
	private JToolBar toolBar;
	private JTextPane editor;
	private StyledDocument styledDocument;
	private SaveCallback callBack;
	private Action actionSave;
	private Action actionSaveAs;
	private Action actionCopy;
	private Action actionCut;
	private Action actionPaste;
	private Action actionUndo;
	private Action actionRedo;

	private JButton btSave;
	private JButton btSaveAs;
	private JButton btCopy;
	private JButton btCut;
	private JButton btPaste;
	private JButton btUndo;
	private JButton btRedo;

	private UndoManager undoManager;
	private EventHandler eventHandler;
	private Listener listener;
	private EditorSyntaxHighlighter highlighter;

	private boolean saveDisabled;

	private final class EventHandler implements DocumentListener {
		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent e) {
			highlight();
		}

		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent e) {
			handleDocumentChanged(e);
		}

		/**
		 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent e)	{
			handleDocumentChanged(e);
		}

	}

	private final class ActionSave extends AbstractAction {
		public ActionSave() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_SAVE), this);
			ImageIcon icon = UIConfiguration.getIcon("save.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			handleSave();
		}
	}

	private final class ActionSaveAs extends AbstractAction {
		public ActionSaveAs() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_SAVEAS), this);
			ImageIcon icon = UIConfiguration.getIcon("saveas.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			handleSaveAs();
		}
	}

	private final class ActionCut extends AbstractAction {
		public ActionCut() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_CUT), this);
			ImageIcon icon = UIConfiguration.getIcon("cut.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			handleCut(e);
		}
	}

	private final class ActionCopy extends AbstractAction {
		public ActionCopy() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_COPY), this);
			ImageIcon icon = UIConfiguration.getIcon("copy.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			handleCopy(e);
		}
	}

	private final class ActionPaste extends AbstractAction {
		public ActionPaste() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_PASTE), this);
			ImageIcon icon = UIConfiguration.getIcon("paste.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			handlePaste(e);
		}
	}

	private final class ActionUndo extends AbstractAction {
		public ActionUndo() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_UNDO), this);
			ImageIcon icon = UIConfiguration.getIcon("undo.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
			this.enabled = false;
		}
		public void actionPerformed(ActionEvent e) {
			handleUndo(e);
		}
	}

	private final class ActionRedo extends AbstractAction {
		public ActionRedo() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_REDO), this);
            putValue(Action.SMALL_ICON, UIConfiguration.getIcon("redo.gif"));
			this.enabled = false;
		}
		public void actionPerformed(ActionEvent e) {
			handleRedo(e);
		}
	}

	/**
	 * Constructor.
	 * @param doc if the document has a style called "rms" this will be used as the default style, if not
	 * a default style will be created
     * @param owner
	 * @param cb if null save actions will be disabled
	 * @param listener
	 * @param highlighter may be null => no syntax highlighting
	 * @param saveEnabled
	 */
	public EmbeddedEditorPanel(
                JFrame owner,
				StyledDocument doc,
				SaveCallback cb,
				Listener listener,
				EditorSyntaxHighlighter highlighter,
				boolean saveEnabled) {
		super(new BorderLayout());
		if(listener == null) {
			throw new IllegalArgumentException("null listener");
		}
        if(owner == null) {
            throw new IllegalArgumentException("null owner");
        }
		if(cb == null) {
	        this.saveDisabled = true;
		}
        this.owner = owner;
		this.callBack = cb;
		this.listener = listener;
		this.highlighter = highlighter;
		setOpaque(true);
        editor = UIFactoryMgr.createTextPane();
        if(doc != null) {
            editor.setDocument(doc);
		}
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(editor);
		add(sp, BorderLayout.CENTER);

		if(!saveDisabled) {
			actionSave = new ActionSave();
			actionSave.setEnabled(saveEnabled);
			actionSaveAs = new ActionSaveAs();
		}
		actionCopy = new ActionCopy();
		actionCut = new ActionCut();
		actionPaste = new ActionPaste();
		actionUndo = new ActionUndo();
		actionRedo = new ActionRedo();

		toolBar = UIFactoryMgr.createToolBar();
		toolBar.setFloatable(false);

		if(!saveDisabled) {
			btSave = UIFactoryMgr.createButton(actionSave);
			btSave.setText(null);
			btSave.setMnemonic(KeyEvent.VK_UNDEFINED);
			toolBar.add(btSave);

	        btSaveAs = UIFactoryMgr.createButton(actionSaveAs);
	        btSaveAs.setText(null);
	        btSaveAs.setMnemonic(KeyEvent.VK_UNDEFINED);
	        toolBar.add(btSaveAs);
			toolBar.addSeparator();
		}

        btCopy = UIFactoryMgr.createButton(actionCopy);
        btCopy.setText(null);
        btCopy.setMnemonic(KeyEvent.VK_UNDEFINED);
        toolBar.add(btCopy);

        btPaste = UIFactoryMgr.createButton(actionPaste);
		btPaste.setText(null);
        btPaste.setMnemonic(KeyEvent.VK_UNDEFINED);
        toolBar.add(btPaste);

        btCut = UIFactoryMgr.createButton(actionCut);
        btCut.setText(null);
        btCut.setMnemonic(KeyEvent.VK_UNDEFINED);
		toolBar.add(btCut);
		toolBar.addSeparator();

        btUndo = UIFactoryMgr.createButton(actionUndo);
        btUndo.setText(null);
        btUndo.setMnemonic(KeyEvent.VK_UNDEFINED);
		toolBar.add(btUndo);

        btRedo = UIFactoryMgr.createButton(actionRedo);
        btRedo.setText(null);
        btRedo.setMnemonic(KeyEvent.VK_UNDEFINED);
		toolBar.add(btRedo);

		add(toolBar, BorderLayout.NORTH);

		undoManager = new UndoManager();
		eventHandler = new EventHandler();

		styledDocument = editor.getStyledDocument();

		// get the default style
		Style style = styledDocument.getStyle("rms");
		if(style == null) {
			// if not defined, go with a monospace font
			style = styledDocument.addStyle("rms", null);
	        StyleConstants.setFontFamily(style, "Monospaced");
	        StyleConstants.setBold(style, false);
		}

        // do this for inserts
	    StyledEditorKit k = (StyledEditorKit)editor.getEditorKit();
	    MutableAttributeSet inputAttributes = k.getInputAttributes();
	    inputAttributes.addAttributes(style);

	    // and this to update the style of the existing text
	    styledDocument.setParagraphAttributes(0, styledDocument.getLength(), inputAttributes, true);

		styledDocument.addUndoableEditListener(undoManager);
		styledDocument.addDocumentListener(eventHandler);

        // add extra shortcuts
        this.editor.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z,
                KeyEvent.CTRL_DOWN_MASK),
                this.actionUndo);
        this.editor.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_Y,
                KeyEvent.CTRL_DOWN_MASK),
                this.actionRedo);
        if(!saveDisabled) {
	        this.editor.getInputMap().put(KeyStroke.getKeyStroke(
	                KeyEvent.VK_S,
	                KeyEvent.CTRL_DOWN_MASK),
	                this.actionSave);
	        this.editor.getInputMap().put(KeyStroke.getKeyStroke(
	                KeyEvent.VK_S,
	                KeyEvent.ALT_DOWN_MASK),
	                this.actionSaveAs);
        }
	}

	/**
	 * Saves the document.
	 */
	private void handleSave()	{
		try {
			if (callBack.save(editor.getStyledDocument())) {
				actionSave.setEnabled(false);
				listener.documentSaved();
			}
			requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Saves the document.
	 */
	private void handleSaveAs()	{
		try {
			if (callBack.saveAs(editor.getStyledDocument())) {
				actionSave.setEnabled(false);
				listener.documentSaved();
			}
			requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Copy.
	 */
	private void handleCopy(ActionEvent ev)	{
		try {
			ActionMap am = editor.getActionMap();
			am.get(DefaultEditorKit.copyAction).actionPerformed(ev);
			actionPaste.setEnabled(true);
			requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Copy.
	 */
	private void handleCut(ActionEvent ev)	{
		try {
			ActionMap am = editor.getActionMap();
			am.get(DefaultEditorKit.cutAction).actionPerformed(ev);
			requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Copy.
	 */
	private void handlePaste(ActionEvent ev)	{
		try {
			ActionMap am = editor.getActionMap();
			am.get(DefaultEditorKit.pasteAction).actionPerformed(ev);
			requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Undo.
	 */
	private void handleUndo(ActionEvent ev)	{
		try {
			if(undoManager.canUndo()) {
				undoManager.undo();
			} else {
				actionUndo.setEnabled(false);
				if(!saveDisabled) {
					actionSave.setEnabled(false);
				}
			}
			if(!undoManager.canRedo()) {
				actionRedo.setEnabled(false);
			} else {
				actionRedo.setEnabled(true);
			}
			requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Redo.
	 */
	private void handleRedo(ActionEvent ev)	{
		try {
			if(undoManager.canRedo()) {
				undoManager.redo();
			}
			if(!undoManager.canRedo()) {
				actionRedo.setEnabled(false);
			}
			requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Handles document changed events.
	 */
	private void handleDocumentChanged(DocumentEvent ev) {
		try {
			actionUndo.setEnabled(true);
			if(!saveDisabled) {
				actionSave.setEnabled(true);
			}
			listener.documentChanged();
			highlight();
		} catch(Exception e) {
			UIExceptionMgr.userException(owner, e);
		}
	}

	/**
	 * Remove undo manager, apply highlight, put back undo manager,
	 * all in Swing's thread because these things can't be done from
	 * inside an event.
	 */
	private void highlight() {
		if (highlighter != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					styledDocument.removeUndoableEditListener(undoManager);
					styledDocument.removeDocumentListener(eventHandler);

					// TODO: future versions should highlight only visible range
					highlighter.applyHighlight(styledDocument,
							0, styledDocument.getLength());

					styledDocument.addUndoableEditListener(undoManager);
					styledDocument.addDocumentListener(eventHandler);
				}
			});
		}
	}

	/**
	 * Override this to send focus to the editor.
	 * @see java.awt.Component#requestFocus()
	 */
	public void requestFocus() {
		editor.requestFocus();
	}

	/**
	 * @param b
	 * @return
	 */
	public JButton getButton(int b) {
		switch(b) {
			case SAVE:
				return btSave;
			case SAVE_AS:
				return btSaveAs;
			case COPY:
				return btCopy;
			case CUT:
				return btCut;
			case REDO:
				return btRedo;
			case UNDO:
				return btUndo;
			case PASTE:
				return btPaste;
		}
		return null;
	}

	/**
	 * @param b
	 * @return
	 */
	public Action getAction(int b) {
		switch(b) {
			case SAVE:
				return actionSave;
			case SAVE_AS:
				return actionSaveAs;
			case COPY:
				return actionCopy;
			case CUT:
				return actionCut;
			case REDO:
				return actionRedo;
			case UNDO:
				return actionUndo;
			case PASTE:
				return actionPaste;
		}
		return null;
	}
	/**
	 * @return the editor.
	 */
	public JTextPane getTextPane() {
		return editor;
	}

	/**
	 * @return
	 */
	public JToolBar getToolBar() {
		return toolBar;
	}
}