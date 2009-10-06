/*
 * Created on 31-Dec-2004
 */
package com.ixora.rms.ui.tools.providermanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.exception.NoParsersInstalledForProvider;
import com.ixora.rms.exception.ParserIsMissing;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;
import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;
import com.ixora.rms.providers.parsers.ui.ParsingRulesPanel;
import com.ixora.rms.repository.ParserInstallationData;
import com.ixora.rms.repository.ParserInstance;
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.ui.tools.providermanager.exception.ParsingRulesMissing;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ParserPanel extends JPanel {
	private static final String LABEl_NAME = MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PARSER_NAME);
	private FormPanel fForm;
	private JPanel fRulesPanel;
	private JComboBox fParserName;
	private ParserRepositoryService fParserRepository;
	private EventHandler fEventHandler;
	private ParsingRulesPanel fCustomRulesPanel;
	/** Cache with the installed parsers */
	private Map<String, ParserInstallationData> fParsers;
	/** Parsers grouped by provider name */
	private Map<String, List<ParserNameData>> fParsersPerProvider;
	/** Event handler */
	private final class EventHandler implements ItemListener {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			handleParserNameChanged(e);
		}
	}

	/**
	 * Object for the combo box with parser names.
	 */
	private static final class ParserNameData {
		String name;
		String translatedName;

		/**
		 * Constructor.
		 * @param name
		 */
		ParserNameData(String name) {
			this.name = name;
			this.translatedName = MessageRepository.get(ProvidersComponent.NAME, name);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return translatedName;
		}
	}

	/**
	 * Constructor.
	 */
	public ParserPanel(ParserRepositoryService prs) {
		super(new BorderLayout());
		if(prs == null) {
			throw new IllegalArgumentException("null params");
		}
		fEventHandler = new EventHandler();
		fParserRepository = prs;
		fParsers = this.fParserRepository.getInstalledParsers();
		fParsersPerProvider = new HashMap<String, List<ParserNameData>>();
		// group parsers by provider names
		for(ParserInstallationData pid : this.fParsers.values()) {
			String[] providers = pid.getSupportedProviders();
			for(int i = 0; i < providers.length; i++) {
				String provider = providers[i];
				List<ParserNameData> lst = fParsersPerProvider.get(provider);
				if(lst == null) {
					lst = new LinkedList<ParserNameData>();
					fParsersPerProvider.put(provider, lst);
				}
				lst.add(new ParserNameData(pid.getParserName()));
			}
		}
		fRulesPanel = new JPanel(new BorderLayout());
		fRulesPanel.setBorder(UIFactoryMgr.createTitledBorder(
                MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PARSING_RULES)));
		fParserName = UIFactoryMgr.createComboBox();
		fParserName.setModel(new DefaultComboBoxModel(getParserNamesData()));

		fForm = new FormPanel(FormPanel.VERTICAL1);
		fForm.addPairs(new String[]{LABEl_NAME},
			new Component[] {fParserName});

		add(fForm, BorderLayout.NORTH);
		add(fRulesPanel, BorderLayout.CENTER);

		fParserName.addItemListener(this.fEventHandler);
	}

	/**
	 * Sets the provider for which this panel will create
	 * a parser instance. This must be invoked before this panel becomes
	 * visible. If <code>providerName</code> is null all parsers will be shown,
	 * else only the parsers which are available for the given provider.
	 * @param providerName
	 * @param pi initial data
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws RMSException
	 */
	public void setParserInstanceData(String providerName, ParserInstance pi) throws RMSException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		// set the new data for the parsers combo box
		List<ParserNameData> lst = this.fParsersPerProvider.get(providerName);
		if(lst == null || lst.size() == 0) {
			throw new NoParsersInstalledForProvider(providerName);
		}
		this.fParserName.setModel(new DefaultComboBoxModel(
				lst.toArray()));
		// update controls with data from pi
		if(pi != null) {
            ParserNameData pd = null;
            String pn = pi.getParserName();
            for(ParserNameData pnd : lst) {
                if(pnd.name.equals(pn)) {
                    pd = pnd;
                    break;
                }
            }
            this.fParserName.setSelectedItem(pd);
            ParsingRulesDefinition rules = pi.getRules();
            setRulesPanelFor(pd.name, rules);
		} else {
		    setRulesPanelFor(((ParserNameData)this.fParserName.getItemAt(0)).name, null);
        }
	}

    /**
     * Stops all editing
     */
    public void stopEditing() {
        if(fCustomRulesPanel != null) {
            fCustomRulesPanel.stopEditing();
        }
    }

	/**
	 * The edit will stop and a parser instance will be return
	 * @throws ParsingRulesMissing
	 * @throws InvalidParsingRules
	 */
	public ParserInstance getParserInstanceData() throws ParsingRulesMissing, InvalidParsingRules {
		// stop editing and return
		if(fCustomRulesPanel == null) {
			throw new ParsingRulesMissing();
		}
		String parserName = ((ParserNameData)fParserName.getSelectedItem()).name;
		ParsingRulesDefinition rules = fCustomRulesPanel.getRules();
		if(rules == null) {
			throw new ParsingRulesMissing();
		}
		ParserInstance ret = new ParserInstance(parserName, rules);
		return ret;
	}

	/**
	 * @return the names of all parsers that are applicable for the current provider
	 */
	private ParserNameData[] getParserNamesData() {
		ParserNameData[] ret = new ParserNameData[fParsers.size()];
		int i = 0;
		for(ParserInstallationData pid : fParsers.values()) {
			ret[i] = new ParserNameData(pid.getParserName());
			++i;
		}
		return ret;
	}

	/**
	 * @param parserName
	 * @return
	 * @throws RMSException
	 */
	private String getParserRulesPanelClass(String parserName) throws RMSException {
		Map<String, ParserInstallationData> parser = this.fParserRepository.getInstalledParsers();
		ParserInstallationData pid = parser.get(parserName);
		if(pid == null) {
			RMSException ex = new ParserIsMissing(parserName);
			ex.setIsInternalAppError();
			throw ex;
		}
		return pid.getParsingRulesPanelClass();
	}

	/**
	 * Changes the parser rules panel to match the newly
	 * selected parser.
	 * @param e
	 */
	private void handleParserNameChanged(ItemEvent e) {
		try {
            if(e.getStateChange() == ItemEvent.SELECTED) {
    			String name = ((ParserNameData)fParserName.getSelectedItem()).name;
    			setRulesPanelFor(name, null);
            }
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param name the name of the parser
	 * @param conf null to create new rules
	 * @throws RMSException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void setRulesPanelFor(String name, ParsingRulesDefinition rules) throws RMSException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String confPanelClass = getParserRulesPanelClass(name);
		Class c = Class.forName(confPanelClass);
		ParsingRulesPanel rp = (ParsingRulesPanel)c.newInstance();
		if(rules == null) {
			rules = rp.createRules();
		}
		rp.setRules(rules);
		if(this.fCustomRulesPanel != null) {
			fRulesPanel.remove(this.fCustomRulesPanel);
		}
		fRulesPanel.add(rp, BorderLayout.CENTER);
		this.fCustomRulesPanel = rp;
        fRulesPanel.validate();
        fRulesPanel.repaint();
    }
}
