/**
 * 19-Aug-2005
 */
package com.ixora.common.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.popup.PopupListener;

/**
 * @author Daniel Moraru
 */
public class TextComponentHandler extends PopupListener implements DocumentListener, PropertyChangeListener {
	private JTextComponent fTextComponent;
	private UndoManager fUndoManager;
	private Action fActionCopy;
	private Action fActionCut;
	private Action fActionPaste;
	private Action fActionUndo;
	private Action fActionRedo;
	private JPopupMenu fPopupMenu;
	private JMenuItem fMenuItemCopy;
	private JMenuItem fMenuItemCut;
	private JMenuItem fMenuItemPaste;
	private JMenuItem fMenuItemUndo;
	private JMenuItem fMenuItemRedo;

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
	private final class ActionRedo extends AbstractAction {
		public ActionRedo() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_REDO), this);
			ImageIcon icon = UIConfiguration.getIcon("redo.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
        	this.enabled = false;
		}
		public void actionPerformed(ActionEvent e) {
			handleRedo(e);
		}
	}

	/**
	 * @param tc
	 */
	public TextComponentHandler(JTextComponent tc) {
		super();
		fTextComponent = tc;
		fActionCopy = new ActionCopy();
		fActionCut = new ActionCut();
		fActionPaste = new ActionPaste();
		fActionUndo = new ActionUndo();
		fActionRedo = new ActionRedo();

		fUndoManager = new UndoManager();
		fTextComponent.addMouseListener(this);
		fTextComponent.addPropertyChangeListener(this);
		fTextComponent.getDocument().addDocumentListener(this);
		fTextComponent.getDocument().addUndoableEditListener(fUndoManager);

		// add some usual shortcuts
		fTextComponent.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z,
                KeyEvent.CTRL_DOWN_MASK),
                fActionUndo);
		fTextComponent.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_Y,
                KeyEvent.CTRL_DOWN_MASK),
                fActionRedo);
        // add by default these common shortcuts for text
        // editors
        fTextComponent.getInputMap().put(KeyStroke.getKeyStroke(
                        KeyEvent.VK_DELETE,
        KeyEvent.SHIFT_DOWN_MASK),
                        DefaultEditorKit.cutAction);
        fTextComponent.getInputMap().put(KeyStroke.getKeyStroke(
                        KeyEvent.VK_INSERT,
        KeyEvent.SHIFT_DOWN_MASK),
                        DefaultEditorKit.pasteAction);

	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	public JPopupMenu getPopupMenu() {
		if(fPopupMenu == null) {
			fPopupMenu = UIFactoryMgr.createPopupMenu();
			fMenuItemCopy = new JMenuItem(fActionCopy);
			fMenuItemCopy.setToolTipText(null);
			fPopupMenu.add(fMenuItemCopy);
			fMenuItemPaste = new JMenuItem(fActionPaste);
			fMenuItemPaste.setToolTipText(null);
			fPopupMenu.add(fMenuItemPaste);
			fMenuItemCut = new JMenuItem(fActionCut);
			fMenuItemCut.setToolTipText(null);
			fPopupMenu.add(fMenuItemCut);
	//		fPopupMenu.addSeparator();
			fMenuItemUndo = new JMenuItem(fActionUndo);
			fMenuItemUndo.setToolTipText(null);
			fPopupMenu.add(fMenuItemUndo);
			fMenuItemRedo = new JMenuItem(fActionRedo);
			fMenuItemRedo.setToolTipText(null);
			fPopupMenu.add(fMenuItemRedo);
		}
		if(fTextComponent.getSelectedText() != null) {
			fActionCut.setEnabled(true);
			fActionCopy.setEnabled(true);
		} else {
			fActionCut.setEnabled(false);
			fActionCopy.setEnabled(false);
		}
		handleTextComponentWritableStateChange();
		return fPopupMenu;
	}

	/**
	 * Copy.
	 */
	private void handleCopy(ActionEvent ev)	{
		try {
			ActionMap am = fTextComponent.getActionMap();
			am.get(DefaultEditorKit.copyAction).actionPerformed(ev);
			fActionPaste.setEnabled(true);
			fTextComponent.requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Copy.
	 */
	private void handleCut(ActionEvent ev)	{
		try {
			ActionMap am = fTextComponent.getActionMap();
			am.get(DefaultEditorKit.cutAction).actionPerformed(ev);
			fTextComponent.requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Copy.
	 */
	private void handlePaste(ActionEvent ev)	{
		try {
			ActionMap am = fTextComponent.getActionMap();
			am.get(DefaultEditorKit.pasteAction).actionPerformed(ev);
			fTextComponent.requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Undo.
	 */
	private void handleUndo(ActionEvent ev)	{
		try {
			if(fUndoManager.canUndo()) {
				fUndoManager.undo();
			} else {
				fActionUndo.setEnabled(false);
			}
			if(!fUndoManager.canRedo()) {
				fActionRedo.setEnabled(false);
			} else {
				fActionRedo.setEnabled(true);
			}
			fTextComponent.requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Redo.
	 */
	private void handleRedo(ActionEvent ev)	{
		try {
			if(fUndoManager.canRedo()) {
				fUndoManager.redo();
			}
			if(!fUndoManager.canRedo()) {
				fActionRedo.setEnabled(false);
			}
			fTextComponent.requestFocus();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e) {
		handleDocumentChanged();
	}

	/**
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) {
		handleDocumentChanged();
	}

	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {
		handleDocumentChanged();
	}

	/**
	 *
	 */
	private void handleDocumentChanged() {
		fActionUndo.setEnabled(true);
		fActionRedo.setEnabled(true);
	}

	/**
	 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
	 */
	protected void showPopup(MouseEvent e) {
		getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 *
	 */
	private void handleTextComponentWritableStateChange() {
		if(fPopupMenu == null) {
			return;
		}
		if(!fTextComponent.isEditable() || !fTextComponent.isEnabled()) {
			fMenuItemCut.setVisible(false);
			fMenuItemPaste.setVisible(false);
			fMenuItemUndo.setVisible(false);
			fMenuItemRedo.setVisible(false);
		} else if(fTextComponent.isEditable()) {
			fMenuItemCut.setVisible(true);
			fMenuItemPaste.setVisible(true);
			fMenuItemUndo.setVisible(true);
			fMenuItemRedo.setVisible(true);
		}
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		handleTextComponentWritableStateChange();
	}

// methods to allow customization
	public Action getActionCopy() {
		return fActionCopy;
	}

	public Action getActionCut() {
		return fActionCut;
	}

	public Action getActionPaste() {
		return fActionPaste;
	}

	public Action getActionRedo() {
		return fActionRedo;
	}

	public Action getActionUndo() {
		return fActionUndo;
	}

	public JMenuItem getMenuItemCopy() {
		return fMenuItemCopy;
	}

	public JMenuItem getMenuItemCut() {
		return fMenuItemCut;
	}

	public JMenuItem getMenuItemPaste() {
		return fMenuItemPaste;
	}

	public JMenuItem getMenuItemRedo() {
		return fMenuItemRedo;
	}

	public JMenuItem getMenuItemUndo() {
		return fMenuItemUndo;
	}
}
