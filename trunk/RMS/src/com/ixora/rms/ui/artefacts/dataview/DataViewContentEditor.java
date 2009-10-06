/*
 * Created on 31-Mar-2005
 */
package com.ixora.rms.ui.artefacts.dataview;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.editor.EditorSyntaxHighlighter;
import com.ixora.common.ui.editor.EmbeddedEditorFrame;
import com.ixora.common.ui.editor.EmbeddedEditorPanel;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.definitions.ReactionDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.AgentVersionsSelectorPanel;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.ResourceSelectorDialog;
import com.ixora.rms.ui.SessionTreeExplorer;
import com.ixora.rms.ui.artefacts.dataview.messages.Msg;
import com.ixora.rms.ui.reactions.ReactionEditorDialog;


/**
 * Customised editor.
 * @author Daniel Moraru
 */
final class DataViewContentEditor extends EmbeddedEditorFrame {
    private JPopupMenu fPopupMenu;
	private JMenuItem fMenuItemInsertResourceId;
	private JMenuItem fMenuItemInsertReaction;
	private SessionTreeExplorer fTreeExplorer;
	private SessionModel fSessionModel;
	private ResourceId fContext;
	private AgentVersionsSelectorPanel fAgentVersionsSelector;
	private EventHandler fEventHandler;

	private Action fActionInsertResourceId = new ActionInsertResourceId();
	private Action fActionInsertReaction = new ActionInsertReaction();

	/**
	 * Insert resource id.
	 */
	private final class ActionInsertResourceId extends AbstractAction {
		public ActionInsertResourceId() {
			super();
			ImageIcon icon = UIConfiguration.getIcon("insert_resource_id.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_INSERT_RESOURCE_ID), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleInsertResourceId();
		}
	}

	/**
	 * Edit provider action.
	 */
	private final class ActionInsertReaction extends AbstractAction {
		public ActionInsertReaction() {
			super();
			ImageIcon icon = UIConfiguration.getIcon("reactions.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_INSERT_REACTION), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleInsertReaction();
		}
	}


	private final class EventHandler implements ReactionEditorDialog.Callback, AgentVersionsSelectorPanel.Listener {
		/**
		 * @see com.ixora.rms.ui.reactions.ReactionEditorDialog.Callback#handleReactionDef(com.ixora.rms.dataengine.definitions.ReactionDef)
		 */
		public void handleReactionDef(ReactionDef def) throws Exception {
			handleReactionDefCallback(def);
		}

		/**
		 * @see com.ixora.rms.ui.AgentVersionsSelectorPanel.Listener#selectionModified()
		 */
		public void selectionModified() {
			handleAgentVersionsChanged();
		}
	}

	/**
	 * Constructor.
	 * @param vc
	 * @param te
	 * @param model
	 * @param context
	 * @param doc
	 * @param cb
     * @param allAvailableAgentVersions
     * @param selectedAgentVersions
	 * @param highlighter
	 * @param saveEnabled
	 */
	public DataViewContentEditor(
            RMSViewContainer vc,
			SessionTreeExplorer te,
			SessionModel model,
			ResourceId context,
			StyledDocument doc,
            Set<String> allAvailableAgentVersions,
            Set<String> selectedAgentVersions,
			SaveCallback cb,
            EditorSyntaxHighlighter highlighter, boolean saveEnabled) {
		super(doc, cb, highlighter, saveEnabled);
        setIconImage(UIConfiguration.getIcon("dv_editor.gif").getImage());
		fTreeExplorer = te;
		fSessionModel = model;
		fContext = context;
		fEventHandler = new EventHandler();
        fAgentVersionsSelector = new AgentVersionsSelectorPanel(this, allAvailableAgentVersions,
                MessageRepository.get(Msg.ACTIONS_ASSIGN_AGENT_VERSIONS), AgentVersionsSelectorPanel.BUTTON_FIRST);
        fAgentVersionsSelector.setSelectedAgentVersions(selectedAgentVersions);
        fAgentVersionsSelector.setMaximumSize(new Dimension(300, super.getPreferredSize().height));
        fAgentVersionsSelector.addListener(fEventHandler);
		fEditor.getToolBar().addSeparator();
        JButton button = UIFactoryMgr.createButton(fActionInsertResourceId);
        button.setText(null);
        button.setMnemonic(KeyEvent.VK_UNDEFINED);
		fEditor.getToolBar().add(button);
        button = UIFactoryMgr.createButton(fActionInsertReaction);
        button.setText(null);
        button.setMnemonic(KeyEvent.VK_UNDEFINED);
        fEditor.getToolBar().add(button);
        fEditor.getToolBar().addSeparator();
        fEditor.getToolBar().add(fAgentVersionsSelector);
        fEditor.getTextPane().getInputMap().put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_I,
                        KeyEvent.CTRL_DOWN_MASK),
                        fActionInsertResourceId);
        fEditor.getTextPane().getInputMap().put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_R,
                        KeyEvent.CTRL_DOWN_MASK),
                        fActionInsertReaction);
		fEditor.getTextPane().addMouseListener(new PopupListener() {
			protected void showPopup(MouseEvent e) {
				getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
			}});
		if(Utils.isEmptyCollection(allAvailableAgentVersions)) {
		    fAgentVersionsSelector.setVisible(false);
        }
    }

	/**
	 * @param def
     * @throws XMLException
     * @throws BadLocationException
	 */
	private void handleReactionDefCallback(ReactionDef def) throws XMLException, BadLocationException {
		StringBuffer buff = XMLUtils.toXMLBuffer(ReactionDef.class, def, "tmp", false);
		buff.delete(0, "<tmp>".length());
		buff.delete(buff.length() - 2 - "</tmp>".length(), buff.length());
		removeSelection();
		Document doc = fEditor.getTextPane().getDocument();
		doc.insertString(fEditor.getTextPane().getCaretPosition(), buff.toString(), null);
	}

	/**
	 *
	 */
	private void handleInsertReaction() {
		try {
			// gather all parameter ids in sight
			String[] params = getParametersFromDocument();
			if(Utils.isEmptyArray(params)) {
				// TODO localize
				throw new RMSException(
						"The current data view has no resource ids defined " +
						"that could be used as parameters for a reaction.");
			}
			ReactionEditorDialog dlg = new ReactionEditorDialog(this, params, fEventHandler);
			UIUtils.centerDialogAndShow(this, dlg);
		} catch(Exception e) {
			UIExceptionMgr.userException(this, e);
		}
	}

	/**
	 * @return
	 * @throws BadLocationException
	 */
	private String[] getParametersFromDocument() throws BadLocationException {
		// regex used to extract parameters
		String regex = "resource.*[\\s]id[\\s]*=[\\s]*\"([^\"]+)\"";
		StyledDocument doc = fEditor.getTextPane().getStyledDocument();
		String text = doc.getText(0, doc.getLength());
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(text);
		List<String> ret = new LinkedList<String>();
		while(mat.find()) {
			ret.add(mat.group(1));
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
     * @return
     */
    public Set<String> getAgentVersionsForDataView() {
       return fAgentVersionsSelector.getSelectedAgentVersions();
    }

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if(fPopupMenu == null) {
			fPopupMenu = UIFactoryMgr.createPopupMenu();
			JMenuItem mi = new JMenuItem(fEditor.getAction(EmbeddedEditorPanel.COPY));
			mi.setToolTipText(null);
			fPopupMenu.add(mi);
			mi = new JMenuItem(fEditor.getAction(EmbeddedEditorPanel.PASTE));
			mi.setToolTipText(null);
			fPopupMenu.add(mi);
			mi = new JMenuItem(fEditor.getAction(EmbeddedEditorPanel.CUT));
			mi.setToolTipText(null);
			fPopupMenu.add(mi);
			fPopupMenu.addSeparator();
			fPopupMenu.add(getJMenuItemInsertResourceId());
			fPopupMenu.add(getJMenuItemInsertReaction());
		}
		return fPopupMenu;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemInsertResourceId() {
		if(fMenuItemInsertResourceId == null) {
			fMenuItemInsertResourceId = UIFactoryMgr.createMenuItem(
					fActionInsertResourceId);
		}
		return fMenuItemInsertResourceId;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemInsertReaction() {
		if(fMenuItemInsertReaction == null) {
			fMenuItemInsertReaction = UIFactoryMgr.createMenuItem(
					fActionInsertReaction);
		}
		return fMenuItemInsertReaction;
	}

	/**
	 * Inserts a resource id at the location of the mouse.
	 */
	private void handleInsertResourceId() {
		try {
			ResourceSelectorDialog dlg = new ResourceSelectorDialog(this,
					ResourceSelectorDialog.SELECT_COUNTER, fTreeExplorer, fSessionModel, fContext, true);
			UIUtils.centerDialogAndShow(this, dlg);
			// get result
			List<ResourceId> lst = dlg.getResult();
			if(!Utils.isEmptyCollection(lst)) {
				ResourceId rid = lst.get(0);
				String txt = rid.toString();
				Document doc = fEditor.getTextPane().getDocument();
				removeSelection();
				doc.insertString(fEditor.getTextPane().getCaretPosition(), txt, null);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(this, e);
		} finally {
			requestFocus();
		}
	}

	/**
	 * @throws BadLocationException
	 */
	private void removeSelection() throws BadLocationException {
		JTextPane pane = fEditor.getTextPane();
		int s = pane.getSelectionStart();
		if(s >= 0) {
			int e = pane.getSelectionEnd();
			if(e - s > 0) {
				pane.getDocument().remove(s, e - s);
			}
		}
	}

	/**
	 *
	 */
	private void handleAgentVersionsChanged() {
		try {
			fEditor.getAction(EmbeddedEditorPanel.SAVE).setEnabled(true);
			fEditor.getAction(EmbeddedEditorPanel.SAVE_AS).setEnabled(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
