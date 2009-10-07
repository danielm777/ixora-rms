/**
 * 10-Feb-2006
 */
package com.ixora.rms.agents.logfile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionDelete;
import com.ixora.common.ui.actions.ActionNew;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.logfile.definitions.StoredLogParserDef;
import com.ixora.rms.agents.logfile.messages.Msg;

/**
 * @author Daniel Moraru
 */

public class LogParserDefinitionEditor extends AppDialog {
	private JComboBox fComboParsers;
	private TypedPropertiesEditor fConfEditor;
	private JPanel fButtonsPanel;
	private Map<String, StoredLogParserDef> fParsers;
	private String fResultSelectedParser;
	private Object fLastSelectedOkItem;

	private class EventHandler implements ItemListener {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			handleParserSelected(e);
		}
	}

	/**
	 * @param parent
	 * @param parsers
	 */
	public LogParserDefinitionEditor(Dialog parent, Map<String, StoredLogParserDef> parsers) {
		super(parent, VERTICAL);
		init(parsers);
	}

	/**
	 * @param window
	 * @param parsers
	 */
	public LogParserDefinitionEditor(Frame window, Map<String, StoredLogParserDef> parsers) {
		super(window, VERTICAL);
		init(parsers);
	}

	/**
	 * @return
	 */
	public Map<String, StoredLogParserDef> getResult() {
		return fParsers;
	}

	/**
	 * @return the name of the selected parser in the result map
	 */
	public String getSelectedParser() {
		return fResultSelectedParser;
	}

	/**
	 * @param parsers
	 */
	private void init(Map<String, StoredLogParserDef> parsers) {
		setTitle("Log File Parsers"); // TODO localize
		setModal(true);
		fParsers = parsers;
		fComboParsers = UIFactoryMgr.createComboBox();
		fButtonsPanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel();
		panel.add(UIFactoryMgr.createButton(new ActionNew(){
			public void actionPerformed(ActionEvent e) {
				handleNewParser();
			}}));
		panel.add(UIFactoryMgr.createButton(new ActionDelete(){
			public void actionPerformed(ActionEvent e) {
				handleDeleteParser();
			}}));
		fButtonsPanel.add(panel, BorderLayout.EAST);
		fConfEditor = new TypedPropertiesEditor(true);
		fConfEditor.setPreferredSize(new Dimension(350, 300));

		fComboParsers.addItemListener(new EventHandler());

		updateCombo(null);

		buildContentPane();
	}

	/**
	 * @param object
	 */
	private void updateCombo(String selected) {
		String[] data = fParsers.keySet().toArray(new String[fParsers.size()]);
		fComboParsers.setModel(
				new DefaultComboBoxModel(data));
		if(selected != null) {
			fComboParsers.setSelectedItem(selected);
		} else {
			if(data.length > 0) {
				fComboParsers.setSelectedIndex(0);
				updateViewForSelectedParser();
			} else {
				fConfEditor.setTypedProperties(Msg.LOG_FILE_NAME, null);
			}
		}
	}

	/**
	 *
	 */
	private void handleNewParser() {
		try {
			// TODO localize
			String name = UIUtils.getStringInput(this, "Input", "Specify parser name");
			if(!Utils.isEmptyString(name)) {
				fParsers.put(name, new StoredLogParserDef(name, false, new LogParserDefinition()));
				updateCombo(name);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleDeleteParser() {
		try {
			String name = (String)fComboParsers.getSelectedItem();
			if(!Utils.isEmptyString(name)) {
				boolean yes = UIUtils.getBooleanYesNoInput(
						// TODO localize
						this, "Delete Parser", "Are you sure you want to delete parser " + name + "?");
				if(yes) {
					fParsers.remove(name);
					updateCombo(null);
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
		return new Component[]{fComboParsers, fButtonsPanel, fConfEditor};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[]{
			UIFactoryMgr.createButton(new ActionOk(){
				public void actionPerformed(ActionEvent e) {
					handleOk();
				}}),
			UIFactoryMgr.createButton(new ActionCancel(){
				public void actionPerformed(ActionEvent e) {
					fResultSelectedParser = null;
					fParsers = null;
					dispose();
				}})
		};
	}

	/**
	 *
	 */
	private void handleOk() {
		try {
			fConfEditor.applyChanges();
			fResultSelectedParser = (String)fComboParsers.getSelectedItem();
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param event
	 */
	private void handleParserSelected(ItemEvent event) {
		try {
			if(event.getStateChange() == ItemEvent.DESELECTED) {
				return;
			}
			fLastSelectedOkItem = updateViewForSelectedParser();
		} catch(Exception e) {
			if(fLastSelectedOkItem != null) {
				fComboParsers.setSelectedItem(fLastSelectedOkItem);
			}
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private String updateViewForSelectedParser() {
		String selected = (String)fComboParsers.getSelectedItem();
		if(selected != null) {
			StoredLogParserDef def = fParsers.get(selected);
			fConfEditor.setTypedProperties(Msg.LOG_FILE_NAME, def.getLogParserDefinition());
		}
		return selected;
	}
}
