/*
 * Created on Dec 25, 2005
 */
package com.ixora.rms.agents.logfile;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.logfile.definitions.StoredLogParserDef;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.agents.ui.DefaultAgentCustomConfigurationPanel;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class ConfigurationPanel extends DefaultAgentCustomConfigurationPanel {
	private static final long serialVersionUID = -3846900400370078753L;
	private static final AppLogger logger = AppLoggerFactory.getLogger(ConfigurationPanel.class);
	private static final String PARSERS_FILE = "config/repository/agents.logfile/parsers.dat";
	private static final String NO_PARSER = "-";
	private LinkedHashMap<String, StoredLogParserDef> fParsers;
	private EventHandler fEventHandler;
	private JEditorPane fPaneEditParsersList;

	private class EventHandler implements HyperlinkListener {
		/**
		 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
		 */
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getEventType() == EventType.ACTIVATED) {
				handleEditParserList();
			}
		}
	}

	/**
	 * Constructor.
	 * @param agentId
	 * @param ctxt
	 */
	public ConfigurationPanel(String agentId, AgentCustomConfigurationPanelContext ctxt) {
		super(agentId, ctxt);
		fParsers = new LinkedHashMap<String, StoredLogParserDef>();
		XMLExternalizable[] parsers = null;
		try {
			 parsers = XMLUtils.readObjectsFromFile(
					new File(PARSERS_FILE), StoredLogParserDef.class, "logParser");
		} catch(XMLException e) {
			logger.error(e);
		}
		if(!Utils.isEmptyArray(parsers)) {
			for(XMLExternalizable ext : parsers) {
				StoredLogParserDef def = (StoredLogParserDef)ext;
				fParsers.put(def.getName(), def);
			}
		}
		if(Utils.isEmptyMap(fParsers)) {
			fParsers.put(NO_PARSER, null);
		}
	}

	/**
	 * @see com.ixora.rms.agents.ui.DefaultAgentCustomConfigurationPanel#createContentPanel()
	 */
	protected JPanel createContentPanel() {
		if(fEventHandler == null) {
			fEventHandler = new EventHandler();
		}
		JPanel panel = super.createContentPanel();
		fPaneEditParsersList = UIFactoryMgr.createHtmlPane();
		fPaneEditParsersList.setPreferredSize(new Dimension(300, 30));
		fPaneEditParsersList.setText("<html><a href='#'>Edit the list of available log parsers.</a></html>");
		fPaneEditParsersList.addHyperlinkListener(fEventHandler);
		panel.add(fPaneEditParsersList, BorderLayout.NORTH);
		return panel;
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.ui.AgentCustomConfigurationPanel#createAgentCustomConfiguration()
	 */
	public AgentCustomConfiguration createAgentCustomConfiguration() throws RMSException {
		return new Configuration(fParsers);
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#setAgentCustomConfiguration(com.ixora.rms.agents.AgentCustomConfiguration)
	 */
	@SuppressWarnings("unchecked")
	public void setAgentCustomConfiguration(AgentCustomConfiguration configuration) {
		// make sure that the passed configuration has values which are
		// not stale, for instance the list of available parsers might have changed...
		PropertyEntry pe = configuration.getEntry(Configuration.AVAILABLE_LOG_PARSERS);
		pe.setValueSet(fParsers.keySet().toArray(new Object[fParsers.size()]));
		// now set config as usual
		super.setAgentCustomConfiguration(configuration);
	}

	/**
	 * Overriden to set the current log parser definition object.
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#applyChanges()
	 */
	public void applyChanges() throws InvalidPropertyValue, VetoException {
		// before applying changes, set the current parser
		updateCurrentParser();
		super.applyChanges();
	}

	/**
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		// make the panel that allows the editing of available parsers invisible
		fPaneEditParsersList.setVisible(enabled);
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	private void handleEditParserList() {
		try {
			Window parent = SwingUtilities.getWindowAncestor(this);
			Map<String, StoredLogParserDef> map =
				(Map<String, StoredLogParserDef>)Utils.deepClone(fParsers);
			map.remove(NO_PARSER);
			LogParserDefinitionEditor dlg = null;
			if(parent instanceof Dialog) {
				dlg = new LogParserDefinitionEditor((Dialog)parent, map);
			} else if(parent instanceof Frame) {
				dlg = new LogParserDefinitionEditor((Frame)parent, map);
			}
			if(dlg == null) {
				return;
			}
			UIUtils.centerDialogAndShow(parent, dlg);
			Map<String, StoredLogParserDef> result = dlg.getResult();
			if(result != null) {
				fParsers = new LinkedHashMap<String, StoredLogParserDef>(result);
				String newSelectedParser = dlg.getSelectedParser();
				if(newSelectedParser != null) {
					PropertyEntry entry = fConfiguration.getEntry(Configuration.AVAILABLE_LOG_PARSERS);
					entry.setValueSet(fParsers.keySet().toArray(new String[fParsers.size()]));
					fConfiguration.setString(Configuration.AVAILABLE_LOG_PARSERS, newSelectedParser);
					fTypedPropsEditor.setTypedProperties(fAgentMessageCatalog, fConfiguration);
				}
				persistParsers();
			} else {
				// set the current parser the first one in the list
				PropertyEntry entry = fConfiguration.getEntry(Configuration.AVAILABLE_LOG_PARSERS);
				Object[] set = entry.getValueSet();
				if(!Utils.isEmptyArray(set)) {
					fConfiguration.setString(Configuration.AVAILABLE_LOG_PARSERS,
							(String)set[0]);
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void updateCurrentParser() {
		try {
			// set the selected parser
			String selParserName = fConfiguration.getString(Configuration.AVAILABLE_LOG_PARSERS);
			if(selParserName != null && !NO_PARSER.equals(selParserName)) {
				LogParserDefinition selParser = fParsers.get(selParserName).getLogParserDefinition();
				fConfiguration.setObject(Configuration.CURRENT_LOG_PARSER, selParser);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Saves the list of defined parsers.
	 * @throws XMLException
	 */
	private void persistParsers() throws XMLException {
		fContext.getViewContainer().getAppWorker().runJob(
				new UIWorkerJobDefault(
						fContext.getViewContainer().getAppFrame(),
						Cursor.WAIT_CURSOR,
						"Saving log parsers...") { // TODO localize
			public void work() throws Throwable {
				Map<String, StoredLogParserDef> map = new LinkedHashMap<String, StoredLogParserDef>(fParsers);
				XMLUtils.writeObjectsToFile(
						new File(Utils.getPath(PARSERS_FILE)),
						map.values().toArray(new StoredLogParserDef[map.size()]),
						StoredLogParserDef.class);
			}
			public void finished(Throwable ex) throws Throwable {
			}
		});
	}
}
